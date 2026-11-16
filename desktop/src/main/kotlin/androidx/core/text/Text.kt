package androidx.core.text

fun CharSequence.isDigitsOnly(): Boolean = all { it in '0'..'9' }

/** Minimal HTML to text: tags are dropped and the common entities decoded (no inline styling). */
fun String.parseAsHtml(flags: Int = 0): android.text.Spanned {
  val plain = replace(Regex("(?i)<br\\s*/?>|</p>"), "\n").replace(Regex("<[^>]+>"), "")
      .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&#39;", "'").replace("&nbsp;", " ")
  return android.text.SpannableString(plain)
}
