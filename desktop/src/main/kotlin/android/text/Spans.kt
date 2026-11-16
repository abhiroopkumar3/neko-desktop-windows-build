package android.text

import java.util.Collections

/** Character styling holder: spans are recorded so Compose code can read them into an AnnotatedString. */
interface Spanned : CharSequence {
  fun <T> getSpans(start: Int, end: Int, kind: Class<T>): Array<T>
  fun getSpanStart(span: Any): Int
  fun getSpanEnd(span: Any): Int
  companion object {
    const val SPAN_EXCLUSIVE_EXCLUSIVE = 33
    const val SPAN_INCLUSIVE_EXCLUSIVE = 17
    const val SPAN_EXCLUSIVE_INCLUSIVE = 34
    const val SPAN_INCLUSIVE_INCLUSIVE = 18
  }
}

interface Spannable : Spanned {
  fun setSpan(what: Any, start: Int, end: Int, flags: Int)
  fun removeSpan(what: Any)
  companion object {
    const val SPAN_EXCLUSIVE_EXCLUSIVE = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    const val SPAN_INCLUSIVE_EXCLUSIVE = Spanned.SPAN_INCLUSIVE_EXCLUSIVE
  }
}

open class SpannableStringBuilder(text: CharSequence = "") : Spannable, Appendable {
  private val sb = StringBuilder(text)
  private class Mark(val what: Any, var start: Int, var end: Int, val flags: Int)
  private val marks = ArrayList<Mark>()
  init { if (text is Spanned) text.getSpans(0, text.length, Any::class.java).forEach { marks.add(Mark(it, text.getSpanStart(it), text.getSpanEnd(it), 0)) } }

  override val length: Int get() = sb.length
  override fun get(index: Int): Char = sb[index]
  override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = sb.subSequence(startIndex, endIndex)
  override fun toString(): String = sb.toString()

  override fun append(csq: CharSequence?): SpannableStringBuilder { val off = sb.length; sb.append(csq); if (csq is Spanned) csq.getSpans(0, csq.length, Any::class.java).forEach { marks.add(Mark(it, off + csq.getSpanStart(it), off + csq.getSpanEnd(it), 0)) }; return this }
  override fun append(csq: CharSequence?, start: Int, end: Int): SpannableStringBuilder { sb.append(csq, start, end); return this }
  override fun append(c: Char): SpannableStringBuilder { sb.append(c); return this }
  fun append(text: CharSequence?, what: Any, flags: Int): SpannableStringBuilder { val s = sb.length; append(text); setSpan(what, s, sb.length, flags); return this }

  override fun setSpan(what: Any, start: Int, end: Int, flags: Int) { marks.add(Mark(what, start, end, flags)) }
  override fun removeSpan(what: Any) { marks.removeAll { it.what === what } }
  @Suppress("UNCHECKED_CAST")
  override fun <T> getSpans(start: Int, end: Int, kind: Class<T>): Array<T> {
    val list = marks.filter { kind.isInstance(it.what) && it.start < end && it.end > start }.map { it.what as T }
    return java.lang.reflect.Array.newInstance(kind, list.size).also { arr -> list.forEachIndexed { i, v -> java.lang.reflect.Array.set(arr, i, v) } } as Array<T>
  }
  override fun getSpanStart(span: Any): Int = marks.firstOrNull { it.what === span }?.start ?: -1
  override fun getSpanEnd(span: Any): Int = marks.firstOrNull { it.what === span }?.end ?: -1
}

class SpannableString(text: CharSequence) : SpannableStringBuilder(text)
