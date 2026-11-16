package android.widget

/** Only the bits upstream preference helpers touch; desktop settings are Compose, so nothing renders this. */
open class CompoundButton {
  var isChecked: Boolean = false
  private var listener: ((CompoundButton, Boolean) -> Unit)? = null
  fun setOnCheckedChangeListener(l: ((CompoundButton, Boolean) -> Unit)?) { listener = l }
}
