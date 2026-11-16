package android.os

object FileUtils {
  @JvmStatic fun copy(from: java.io.InputStream, to: java.io.OutputStream): Long = from.copyTo(to)
}
