package android.os

class StatFs(path: String) {
  private val f = java.io.File(path)
  val availableBytes: Long get() = f.usableSpace
  val totalBytes: Long get() = f.totalSpace
  val blockSizeLong: Long get() = 4096
  val availableBlocksLong: Long get() = f.usableSpace / 4096
  val blockCountLong: Long get() = f.totalSpace / 4096
  val freeBytes: Long get() = f.freeSpace
}
