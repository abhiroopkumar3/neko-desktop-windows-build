package android.util

import java.util.Base64 as JBase64

/** android.util.Base64 on top of java.util.Base64, keeping Android's flag semantics. */
object Base64 {
  const val DEFAULT = 0
  const val NO_PADDING = 1
  const val NO_WRAP = 2
  const val CRLF = 4
  const val URL_SAFE = 8

  @JvmStatic fun encodeToString(input: ByteArray, flags: Int): String = String(encode(input, flags), Charsets.US_ASCII)

  @JvmStatic fun encodeToString(input: ByteArray, offset: Int, len: Int, flags: Int): String =
      encodeToString(input.copyOfRange(offset, offset + len), flags)

  @JvmStatic fun encode(input: ByteArray, flags: Int): ByteArray {
    var enc = if (flags and URL_SAFE != 0) JBase64.getUrlEncoder() else JBase64.getEncoder()
    if (flags and NO_PADDING != 0) enc = enc.withoutPadding()
    var out = String(enc.encode(input), Charsets.US_ASCII)
    if (flags and NO_WRAP == 0) {
      val nl = if (flags and CRLF != 0) "\r\n" else "\n"
      out = out.chunked(76).joinToString(nl) + (if (out.isNotEmpty()) nl else "")
    }
    return out.toByteArray(Charsets.US_ASCII)
  }

  @JvmStatic fun decode(str: String, flags: Int): ByteArray {
    val clean = str.filterNot { it == '\n' || it == '\r' || it == ' ' }
    val dec = if (flags and URL_SAFE != 0) JBase64.getUrlDecoder() else JBase64.getDecoder()
    return dec.decode(clean.trimEnd('=').let { it + "=".repeat((4 - it.length % 4) % 4) })
  }

  @JvmStatic fun decode(input: ByteArray, flags: Int): ByteArray = decode(String(input, Charsets.US_ASCII), flags)
}
