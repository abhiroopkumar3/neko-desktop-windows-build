package android.net

import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

/** Compact re-implementation of android.net.Uri (parsing, query access, builder). */
class Uri private constructor(
    val scheme: String?,
    val encodedAuthority: String?,
    val encodedPath: String?,
    val encodedQuery: String?,
    val encodedFragment: String?,
) : Comparable<Uri> {
  val authority: String? get() = encodedAuthority?.let { Uri.dec(it) }
  val path: String? get() = encodedPath?.let { Uri.dec(it) }
  val query: String? get() = encodedQuery?.let { Uri.dec(it) }
  val fragment: String? get() = encodedFragment?.let { Uri.dec(it) }
  val schemeSpecificPart: String get() = buildString {
    if (encodedAuthority != null) append("//").append(encodedAuthority)
    encodedPath?.let(::append)
    if (encodedQuery != null) append('?').append(encodedQuery)
  }
  private val hostAndPort: Pair<String?, Int> get() {
    val a = encodedAuthority?.substringAfterLast('@') ?: return null to -1
    val idx = a.lastIndexOf(':')
    return if (idx > 0 && a.substring(idx + 1).all(Char::isDigit) && !a.endsWith("]")) a.substring(0, idx) to a.substring(idx + 1).toInt() else a to -1
  }
  val host: String? get() = hostAndPort.first
  val port: Int get() = hostAndPort.second
  val userInfo: String? get() = encodedAuthority?.takeIf { '@' in it }?.substringBeforeLast('@')?.let { Uri.dec(it) }
  val pathSegments: List<String> get() = path?.split('/')?.filter { it.isNotEmpty() } ?: emptyList()
  val lastPathSegment: String? get() = pathSegments.lastOrNull()
  val isAbsolute: Boolean get() = scheme != null
  val isRelative: Boolean get() = scheme == null
  val isHierarchical: Boolean get() = true
  val queryParameterNames: Set<String> get() =
      encodedQuery?.split('&')?.filter { it.isNotEmpty() }?.map { Uri.dec(it.substringBefore('=')) }?.toCollection(linkedSetOf()) ?: emptySet()

  fun getQueryParameter(key: String): String? = getQueryParameters(key).firstOrNull()

  fun getQueryParameters(key: String): List<String> =
      encodedQuery?.split('&')?.filter { it.isNotEmpty() }
          ?.filter { Uri.dec(it.substringBefore('=')) == key }
          ?.map { Uri.dec(it.substringAfter('=', "")) } ?: emptyList()

  fun getBooleanQueryParameter(key: String, defaultValue: Boolean): Boolean =
      getQueryParameter(key)?.lowercase()?.let { it != "false" && it != "0" } ?: defaultValue

  fun buildUpon(): Builder = Builder().scheme(scheme).encodedAuthority(encodedAuthority)
      .encodedPath(encodedPath).encodedQuery(encodedQuery).encodedFragment(encodedFragment)

  fun normalizeScheme(): Uri = scheme?.lowercase()?.takeIf { it != scheme }?.let { buildUpon().scheme(it).build() } ?: this

  override fun toString(): String = buildString {
    if (scheme != null) append(scheme).append(':')
    if (encodedAuthority != null) append("//").append(encodedAuthority)
    encodedPath?.let(::append)
    if (encodedQuery != null) append('?').append(encodedQuery)
    if (encodedFragment != null) append('#').append(encodedFragment)
  }
  override fun equals(other: Any?) = other is Uri && toString() == other.toString()
  override fun hashCode() = toString().hashCode()
  override fun compareTo(other: Uri) = toString().compareTo(other.toString())

  class Builder {
    private var scheme: String? = null
    private var authority: String? = null
    private var path: String? = null
    private var query: String? = null
    private var fragment: String? = null
    fun scheme(v: String?) = apply { scheme = v }
    fun authority(v: String?) = apply { authority = v }
    fun encodedAuthority(v: String?) = apply { authority = v }
    fun path(v: String?) = apply { path = v?.let { encodePath(it) } }
    fun encodedPath(v: String?) = apply { path = v }
    fun appendPath(seg: String) = apply { path = join(path, Uri.enc(seg)) }
    fun appendEncodedPath(seg: String) = apply { path = join(path, seg) }
    fun query(v: String?) = apply { query = v?.let { Uri.enc(it, "=&") } }
    fun encodedQuery(v: String?) = apply { query = v }
    fun appendQueryParameter(key: String, value: String) = apply {
      val p = "${Uri.enc(key)}=${Uri.enc(value)}"
      query = if (query.isNullOrEmpty()) p else "$query&$p"
    }
    fun clearQuery() = apply { query = null }
    fun fragment(v: String?) = apply { fragment = v?.let { Uri.enc(it) } }
    fun encodedFragment(v: String?) = apply { fragment = v }
    fun build() = Uri(scheme, authority, path, query, fragment)
    override fun toString() = build().toString()
    private fun join(a: String?, b: String): String {
      val left = a.orEmpty()
      return if (left.endsWith("/") || b.startsWith("/")) left + b.removePrefix(if (left.endsWith("/")) "/" else "") else "$left/$b"
    }
    private fun encodePath(p: String) = p.split('/').joinToString("/") { Uri.enc(it) }
  }

  companion object {
    val EMPTY: Uri = Uri(null, null, "", null, null)
    private val PATTERN = Regex("^(?:([A-Za-z][A-Za-z0-9+.-]*):)?(?://([^/?#]*))?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?$", RegexOption.DOT_MATCHES_ALL)

    @JvmStatic fun fromParts(scheme: String, ssp: String, fragment: String?): Uri = parse("$scheme:$ssp" + (fragment?.let { "#$it" } ?: ""))
    @JvmStatic fun parse(uriString: String): Uri {
      val m = PATTERN.matchEntire(uriString) ?: return Uri(null, null, uriString, null, null)
      val (scheme, auth, path, q, frag) = m.destructured
      return Uri(scheme.ifEmpty { null }, if (m.groups[2] != null) auth else null, path, if (m.groups[4] != null) q else null, if (m.groups[5] != null) frag else null)
    }
    @JvmStatic fun fromFile(file: File): Uri = parse(file.toURI().toString().replaceFirst("file:/", "file:///"))
    @JvmStatic fun withAppendedPath(base: Uri, seg: String): Uri = base.buildUpon().appendPath(seg).build()
    @JvmStatic fun encode(s: String?): String? = s?.let { Uri.enc(it) }
    @JvmStatic fun decode(s: String?): String? = s?.let { Uri.dec(it) }
    internal fun enc(s: String, allow: String = ""): String {
      val sb = StringBuilder()
      for (c in s) {
        if (c.isLetterOrDigit() && c.code < 128 || c in "-_.!~*'()" || c in allow) sb.append(c)
        else sb.append(URLEncoder.encode(c.toString(), "UTF-8").replace("+", "%20"))
      }
      return sb.toString()
    }
    internal fun dec(s: String): String = try { URLDecoder.decode(s.replace("+", "%2B"), "UTF-8") } catch (_: Exception) { s }
  }
}
