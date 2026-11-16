package android.content.res

import javax.xml.parsers.DocumentBuilderFactory
import org.nekomanga.desktop.res.RTable
import org.w3c.dom.Element

/**
 * Desktop implementation of android.content.res.Resources for value resources (strings, plurals,
 * string arrays, colors). The XML files are the unmodified upstream ones, bundled into the jar;
 * ids come from the generated org.nekomanga.R.
 */
class Resources private constructor() {
  private val strings = HashMap<String, String>()
  private val plurals = HashMap<String, Map<String, String>>()
  private val arrays = HashMap<String, List<String>>()
  private val colors = HashMap<String, Int>()

  init {
    val factory = DocumentBuilderFactory.newInstance()
    RTable.xmlFiles.forEach { file ->
      val stream = Resources::class.java.getResourceAsStream("/android-res/$file") ?: return@forEach
      val root = stream.use { factory.newDocumentBuilder().parse(it).documentElement }
      val nodes = root.childNodes
      for (i in 0 until nodes.length) {
        val e = nodes.item(i) as? Element ?: continue
        val name = e.getAttribute("name").replace('.', '_')
        when (e.tagName) {
          "string" -> strings[name] = unescape(e.textContent)
          "plurals" -> plurals[name] = e.getElementsByTagName("item").let { items ->
            (0 until items.length).associate { (items.item(it) as Element).getAttribute("quantity") to unescape(items.item(it).textContent) }
          }
          "string-array" -> arrays[name] = e.getElementsByTagName("item").let { items ->
            (0 until items.length).map { unescape(items.item(it).textContent) }
          }
          "color" -> parseColor(e.textContent.trim())?.let { colors[name] = it }
        }
      }
    }
  }

  private fun parseColor(v: String): Int? = when {
    v.startsWith("@color/") -> colors[v.removePrefix("@color/").replace('.', '_')]
    v.startsWith("#") -> v.drop(1).let { hex ->
      when (hex.length) {
        6 -> (0xFF000000L or hex.toLong(16)).toInt()
        8 -> hex.toLong(16).toInt()
        3 -> hex.map { "$it$it" }.joinToString("").let { (0xFF000000L or it.toLong(16)).toInt() }
        else -> null
      }
    }
    else -> null
  }

  /** Applies Android's string-resource escaping rules. */
  private fun unescape(raw: String): String {
    var s = raw.trim()
    if (s.length >= 2 && s.startsWith('"') && s.endsWith('"')) s = s.substring(1, s.length - 1)
    val sb = StringBuilder()
    var i = 0
    while (i < s.length) {
      val c = s[i]
      if (c == '\\' && i + 1 < s.length) {
        when (val n = s[i + 1]) {
          'n' -> sb.append('\n')
          't' -> sb.append('\t')
          'u' -> if (i + 5 < s.length + 0 && s.length >= i + 6) { sb.append(s.substring(i + 2, i + 6).toInt(16).toChar()); i += 4 } else sb.append(n)
          else -> sb.append(n)
        }
        i += 2
      } else { sb.append(c); i++ }
    }
    return sb.toString()
  }

  private fun name(id: Int, base: Int, table: Array<String>): String =
      table.getOrNull(id - base) ?: throw NotFoundException("Resource ID #0x${id.toString(16)}")

  class NotFoundException(message: String) : RuntimeException(message)

  fun getString(id: Int): String {
    val n = name(id, RTable.STRING_BASE, RTable.string)
    return strings[n] ?: throw NotFoundException("String resource $n")
  }

  fun getString(id: Int, vararg args: Any?): String = getString(id).format(*args)

  fun getText(id: Int): CharSequence = getString(id)

  fun getStringArray(id: Int): Array<String> {
    val n = name(id, RTable.ARRAY_BASE, RTable.array)
    val items = arrays[n] ?: throw NotFoundException("Array resource $n")
    return items.map { if (it.startsWith("@string/")) strings[it.removePrefix("@string/").replace('.', '_')].orEmpty() else it }.toTypedArray()
  }

  fun getQuantityString(id: Int, quantity: Int): String {
    val n = name(id, RTable.PLURALS_BASE, RTable.plurals)
    val forms = plurals[n] ?: throw NotFoundException("Plurals resource $n")
    return forms[if (quantity == 1) "one" else "other"] ?: forms["other"] ?: forms.values.first()
  }

  fun getQuantityString(id: Int, quantity: Int, vararg args: Any?): String = getQuantityString(id, quantity).format(*args)

  fun getQuantityText(id: Int, quantity: Int): CharSequence = getQuantityString(id, quantity)

  fun getColor(id: Int, theme: Any? = null): Int {
    val pastel = id - com.zedlabs.pastelplaceholder.Pastel.BASE
    if (pastel in com.zedlabs.pastelplaceholder.Pastel.light.indices) return com.zedlabs.pastelplaceholder.Pastel.light[pastel]
    val n = name(id, RTable.COLOR_BASE, RTable.color)
    return colors[n] ?: throw NotFoundException("Color resource $n")
  }

  val displayMetrics: android.util.DisplayMetrics by lazy { android.util.DisplayMetrics() }
  val configuration: Configuration by lazy { Configuration() }

  companion object {
    val instance: Resources by lazy { Resources() }

    @JvmStatic fun getSystem(): Resources = instance
  }
}
