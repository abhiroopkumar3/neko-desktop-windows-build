package androidx.work

class WorkQuery private constructor(val tags: List<String>, val states: List<WorkInfo.State>) {
  class Builder private constructor(private val tags: List<String>) {
    private var states = emptyList<WorkInfo.State>()
    fun addStates(s: List<WorkInfo.State>) = apply { states = s }
    fun build() = WorkQuery(tags, states)
    companion object { @JvmStatic fun fromTags(tags: List<String>) = Builder(tags) }
  }
}
