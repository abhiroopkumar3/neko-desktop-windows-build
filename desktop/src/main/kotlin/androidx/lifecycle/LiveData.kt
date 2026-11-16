package androidx.lifecycle

import kotlinx.coroutines.flow.Flow

/** Thin holder so upstream's `liveData.asFlow()` call sites work; the in-process WorkManager is flow based. */
class LiveData<T>(val flow: Flow<T>)

fun <T> LiveData<T>.asFlow(): Flow<T> = flow
