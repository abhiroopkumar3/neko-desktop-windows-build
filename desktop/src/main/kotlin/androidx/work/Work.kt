package androidx.work

import android.content.Context
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * In-process WorkManager for the desktop app: unique work, periodic work, delays, retry with
 * backoff, tags and WorkInfo flows. Work is not persisted across restarts (Android's survives
 * process death); upstream already re-registers its periodic jobs at startup, which keeps working.
 */
class Data private constructor(private val values: Map<String, Any?>) {
  fun getString(k: String, def: String? = null): String? = values[k] as? String ?: def
  fun getInt(k: String, def: Int): Int = values[k] as? Int ?: def
  fun getLong(k: String, def: Long): Long = values[k] as? Long ?: def
  fun getBoolean(k: String, def: Boolean): Boolean = values[k] as? Boolean ?: def
  fun getStringArray(k: String): Array<String>? = @Suppress("UNCHECKED_CAST") (values[k] as? Array<String>)
  fun getLongArray(k: String): LongArray? = values[k] as? LongArray
  val keyValueMap: Map<String, Any?> get() = values
  class Builder {
    private val m = LinkedHashMap<String, Any?>()
    fun putString(k: String, v: String?) = apply { m[k] = v }
    fun putInt(k: String, v: Int) = apply { m[k] = v }
    fun putLong(k: String, v: Long) = apply { m[k] = v }
    fun putBoolean(k: String, v: Boolean) = apply { m[k] = v }
    fun putStringArray(k: String, v: Array<String>) = apply { m[k] = v }
    fun putLongArray(k: String, v: LongArray) = apply { m[k] = v }
    fun build() = Data(m.toMap())
  }
  companion object { @JvmField val EMPTY = Data(emptyMap()) }
}

fun workDataOf(vararg pairs: Pair<String, Any?>): Data = Data.Builder().also { b ->
  pairs.forEach { (k, v) ->
    when (v) {
      is String? -> b.putString(k, v as String?)
      is Int -> b.putInt(k, v)
      is Long -> b.putLong(k, v)
      is Boolean -> b.putBoolean(k, v)
      is Array<*> -> @Suppress("UNCHECKED_CAST") b.putStringArray(k, v as Array<String>)
      is LongArray -> b.putLongArray(k, v)
    }
  }
}.build()

enum class NetworkType { NOT_REQUIRED, CONNECTED, UNMETERED, NOT_ROAMING, METERED }
enum class ExistingWorkPolicy { REPLACE, KEEP, APPEND, APPEND_OR_REPLACE }
enum class ExistingPeriodicWorkPolicy { REPLACE, KEEP, UPDATE, CANCEL_AND_REENQUEUE }
enum class BackoffPolicy { LINEAR, EXPONENTIAL }
enum class OutOfQuotaPolicy { RUN_AS_NON_EXPEDITED_WORK_REQUEST, DROP_WORK_REQUEST }

class Constraints private constructor(val requiredNetworkType: NetworkType, val requiresCharging: Boolean, val requiresBatteryNotLow: Boolean) {
  class Builder {
    private var network = NetworkType.NOT_REQUIRED
    private var charging = false
    private var battery = false
    fun setRequiredNetworkType(t: NetworkType) = apply { network = t }
    fun setRequiresCharging(v: Boolean) = apply { charging = v }
    fun setRequiresBatteryNotLow(v: Boolean) = apply { battery = v }
    fun setRequiresDeviceIdle(v: Boolean) = apply { }
    fun setRequiresStorageNotLow(v: Boolean) = apply { }
    fun build() = Constraints(network, charging, battery)
  }
  companion object { @JvmField val NONE = Builder().build() }
}

class Configuration private constructor() {
  class Builder {
    fun setWorkerFactory(f: Any?) = this
    fun setMinimumLoggingLevel(l: Int) = this
    fun build() = Configuration()
  }
  interface Provider { val workManagerConfiguration: Configuration }
}

class WorkInfo(val id: UUID, val state: State, val tags: Set<String>, val progress: Data = Data.EMPTY, val outputData: Data = Data.EMPTY) {
  enum class State { ENQUEUED, RUNNING, SUCCEEDED, FAILED, BLOCKED, CANCELLED;
    val isFinished: Boolean get() = this == SUCCEEDED || this == FAILED || this == CANCELLED }
}

class ForegroundInfo(val notificationId: Int, val notification: androidx.core.app.Notification, val foregroundServiceType: Int = 0)

class WorkerParameters internal constructor(val id: UUID, val inputData: Data, val tags: Set<String>, val runAttemptCount: Int)

abstract class ListenableWorker(val applicationContext: Context, val workerParams: WorkerParameters) {
  val inputData: Data get() = workerParams.inputData
  val id: UUID get() = workerParams.id
  val tags: Set<String> get() = workerParams.tags
  val runAttemptCount: Int get() = workerParams.runAttemptCount
  var isStopped: Boolean = false
    internal set
  open fun onStopped() {}

  sealed class Result {
    object Success : Result()
    object Failure : Result()
    object Retry : Result()
    companion object {
      @JvmStatic fun success(): Result = Success
      @JvmStatic fun success(data: Data): Result = Success
      @JvmStatic fun failure(): Result = Failure
      @JvmStatic fun failure(data: Data): Result = Failure
      @JvmStatic fun retry(): Result = Retry
    }
  }
}

abstract class CoroutineWorker(appContext: Context, params: WorkerParameters) : ListenableWorker(appContext, params) {
  abstract suspend fun doWork(): Result
  open suspend fun getForegroundInfo(): ForegroundInfo = throw IllegalStateException("No foreground info")
  suspend fun setForeground(info: ForegroundInfo) { /* desktop apps are already in the foreground */ }
  suspend fun setProgress(data: Data) { WorkManager.updateProgress(id, data) }
}

abstract class WorkRequest internal constructor(val id: UUID, internal val workerClass: Class<out ListenableWorker>, internal val spec: Spec) {
  class Spec(
      val inputData: Data, val tags: Set<String>, val constraints: Constraints, val initialDelayMs: Long,
      val periodMs: Long, val backoffMs: Long, val backoff: BackoffPolicy,
  )
  val tags: Set<String> get() = spec.tags

  abstract class Builder<B : Builder<B, W>, W : WorkRequest>(protected val workerClass: Class<out ListenableWorker>) {
    protected var input = Data.EMPTY
    protected val tagSet = LinkedHashSet<String>()
    protected var constraints = Constraints.NONE
    protected var delayMs = 0L
    protected var backoffMs = 30_000L
    protected var backoff = BackoffPolicy.EXPONENTIAL
    protected abstract val self: B
    fun setInputData(d: Data): B { input = d; return self }
    fun addTag(t: String): B { tagSet.add(t); return self }
    fun setConstraints(c: Constraints): B { constraints = c; return self }
    fun setInitialDelay(d: Long, unit: TimeUnit): B { delayMs = unit.toMillis(d); return self }
    fun setBackoffCriteria(p: BackoffPolicy, d: Long, unit: TimeUnit): B { backoff = p; backoffMs = unit.toMillis(d); return self }
    fun setExpedited(policy: OutOfQuotaPolicy): B = self
    abstract fun build(): W
  }
}

class OneTimeWorkRequest internal constructor(id: UUID, cls: Class<out ListenableWorker>, spec: Spec) : WorkRequest(id, cls, spec) {
  class Builder(cls: Class<out ListenableWorker>) : WorkRequest.Builder<Builder, OneTimeWorkRequest>(cls) {
    override val self get() = this
    override fun build() = OneTimeWorkRequest(UUID.randomUUID(), workerClass, Spec(input, tagSet.toSet(), constraints, delayMs, 0, backoffMs, backoff))
  }
}

class PeriodicWorkRequest internal constructor(id: UUID, cls: Class<out ListenableWorker>, spec: Spec) : WorkRequest(id, cls, spec) {
  class Builder(cls: Class<out ListenableWorker>, private val periodMs: Long) : WorkRequest.Builder<Builder, PeriodicWorkRequest>(cls) {
    override val self get() = this
    override fun build() = PeriodicWorkRequest(UUID.randomUUID(), workerClass, Spec(input, tagSet.toSet(), constraints, delayMs, periodMs, backoffMs, backoff))
  }
}

inline fun <reified W : ListenableWorker> OneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder = OneTimeWorkRequest.Builder(W::class.java)

inline fun <reified W : ListenableWorker> PeriodicWorkRequestBuilder(repeatInterval: Long, repeatIntervalTimeUnit: TimeUnit): PeriodicWorkRequest.Builder =
    PeriodicWorkRequest.Builder(W::class.java, repeatIntervalTimeUnit.toMillis(repeatInterval))

inline fun <reified W : ListenableWorker> PeriodicWorkRequestBuilder(repeatInterval: Long, repeatIntervalTimeUnit: TimeUnit, flexTimeInterval: Long, flexTimeIntervalUnit: TimeUnit): PeriodicWorkRequest.Builder =
    PeriodicWorkRequest.Builder(W::class.java, repeatIntervalTimeUnit.toMillis(repeatInterval))

class WorkManager private constructor() {
  private class Entry(val request: WorkRequest, val uniqueName: String?, val state: MutableStateFlow<WorkInfo>, var job: Job? = null)

  fun enqueue(request: WorkRequest) { start(request, null) }

  fun enqueueUniqueWork(name: String, policy: ExistingWorkPolicy, request: OneTimeWorkRequest) {
    synchronized(lock) {
      val running = unique[name]?.takeIf { !it.state.value.state.isFinished }
      if (running != null && policy == ExistingWorkPolicy.KEEP) return
      if (running != null && policy == ExistingWorkPolicy.REPLACE) cancelEntry(running)
      start(request, name)
    }
  }

  fun enqueueUniquePeriodicWork(name: String, policy: ExistingPeriodicWorkPolicy, request: PeriodicWorkRequest) {
    synchronized(lock) {
      val running = unique[name]?.takeIf { !it.state.value.state.isFinished }
      if (running != null && (policy == ExistingPeriodicWorkPolicy.KEEP || policy == ExistingPeriodicWorkPolicy.UPDATE)) return
      if (running != null) cancelEntry(running)
      start(request, name)
    }
  }

  fun cancelUniqueWork(name: String) { synchronized(lock) { unique[name]?.let(::cancelEntry) } }
  fun cancelAllWorkByTag(tag: String) { synchronized(lock) { entries.filter { tag in it.request.tags }.forEach(::cancelEntry) } }
  fun cancelWorkById(id: UUID) { synchronized(lock) { entries.firstOrNull { it.request.id == id }?.let(::cancelEntry) } }

  fun getWorkInfosByTag(tag: String): java.util.concurrent.Future<List<WorkInfo>> =
      java.util.concurrent.CompletableFuture.completedFuture(synchronized(lock) { entries.filter { tag in it.request.tags }.map { it.state.value } })

  fun getWorkInfosByTagFlow(tag: String): Flow<List<WorkInfo>> = changes.map { synchronized(lock) { entries.filter { tag in it.request.tags }.map { e -> e.state.value } } }
  fun getWorkInfosForUniqueWorkFlow(name: String): Flow<List<WorkInfo>> = changes.map { synchronized(lock) { listOfNotNull(unique[name]?.state?.value) } }
  fun getWorkInfosForUniqueWork(name: String): java.util.concurrent.Future<List<WorkInfo>> =
      java.util.concurrent.CompletableFuture.completedFuture(synchronized(lock) { listOfNotNull(unique[name]?.state?.value) })

  private fun cancelEntry(e: Entry) {
    e.job?.cancel()
    setState(e, WorkInfo.State.CANCELLED)
  }

  private fun setState(e: Entry, s: WorkInfo.State, progress: Data? = null) {
    e.state.value = WorkInfo(e.request.id, s, e.request.tags, progress ?: e.state.value.progress)
    changes.value = changes.value + 1
  }

  private fun start(request: WorkRequest, uniqueName: String?) {
    val entry = Entry(request, uniqueName, MutableStateFlow(WorkInfo(request.id, WorkInfo.State.ENQUEUED, request.tags)))
    entries.add(entry)
    uniqueName?.let { unique[it] = entry }
    entry.job = scope.launch {
      val spec = request.spec
      delay(spec.initialDelayMs)
      var attempt = 0
      while (true) {
        setState(entry, WorkInfo.State.RUNNING)
        val worker = runCatching {
          request.workerClass.getConstructor(Context::class.java, WorkerParameters::class.java)
              .newInstance(android.content.DesktopContext, WorkerParameters(request.id, spec.inputData, spec.tags, attempt))
        }.getOrElse { System.err.println("Cannot create worker ${request.workerClass.name}: $it"); setState(entry, WorkInfo.State.FAILED); return@launch }
        val result = try { (worker as? CoroutineWorker)?.doWork() ?: ListenableWorker.Result.Failure }
        catch (c: CancellationException) { worker.isStopped = true; worker.onStopped(); throw c }
        catch (t: Throwable) { System.err.println("Worker ${request.workerClass.simpleName} failed: $t"); ListenableWorker.Result.Failure }
        when {
          result is ListenableWorker.Result.Retry -> {
            attempt++
            setState(entry, WorkInfo.State.ENQUEUED)
            delay(if (spec.backoff == BackoffPolicy.LINEAR) spec.backoffMs * attempt else spec.backoffMs shl (attempt - 1).coerceAtMost(10))
          }
          spec.periodMs > 0 -> { setState(entry, WorkInfo.State.ENQUEUED); attempt = 0; delay(spec.periodMs) }
          else -> { setState(entry, if (result is ListenableWorker.Result.Success) WorkInfo.State.SUCCEEDED else WorkInfo.State.FAILED); return@launch }
        }
      }
    }
  }

  companion object {
    private val lock = Any()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val entries = ArrayList<Entry>()
    private val unique = ConcurrentHashMap<String, Entry>()
    private val changes = MutableStateFlow(0)
    private val instance = WorkManager()

    @JvmStatic fun getInstance(context: Context): WorkManager = instance

    internal fun updateProgress(id: UUID, data: Data) {
      synchronized(lock) { entries.firstOrNull { it.request.id == id }?.let { instance.setState(it, it.state.value.state, data) } }
    }
  }
}
