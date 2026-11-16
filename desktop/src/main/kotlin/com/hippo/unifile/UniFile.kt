package com.hippo.unifile

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URLConnection

/**
 * Desktop implementation of UniFile on plain files (the Android library bridges SAF content URIs,
 * which don't exist here). Same member names and null-on-failure behavior as the original.
 */
class UniFile private constructor(val file: File) {
  val name: String? get() = file.name.takeIf { it.isNotEmpty() }
  val uri: Uri get() = Uri.fromFile(file)
  val filePath: String? get() = file.absolutePath
  val parentFile: UniFile? get() = file.parentFile?.let { UniFile(it) }
  val isDirectory: Boolean get() = file.isDirectory
  val isFile: Boolean get() = file.isFile
  val type: String? get() = if (file.isDirectory) null else URLConnection.guessContentTypeFromName(file.name)

  fun exists(): Boolean = file.exists()
  fun length(): Long = file.length()
  fun lastModified(): Long = file.lastModified()
  fun canRead(): Boolean = file.canRead()
  fun canWrite(): Boolean = file.canWrite()
  fun delete(): Boolean = if (file.isDirectory) file.deleteRecursively() else file.delete()

  fun createDirectory(displayName: String): UniFile? {
    val d = File(file, displayName)
    return if (d.isDirectory || d.mkdirs()) UniFile(d) else null
  }

  fun createFile(displayName: String): UniFile? {
    val f = File(file, displayName)
    return runCatching { file.mkdirs(); if (f.exists() || f.createNewFile()) UniFile(f) else null }.getOrNull()
  }

  fun findFile(displayName: String): UniFile? = File(file, displayName).takeIf { it.exists() }?.let { UniFile(it) }

  fun listFiles(): Array<UniFile>? = file.listFiles()?.map { UniFile(it) }?.toTypedArray()

  fun listFiles(filter: (UniFile, String) -> Boolean): Array<UniFile>? =
      file.listFiles()?.filter { filter(this, it.name) }?.map { UniFile(it) }?.toTypedArray()

  fun renameTo(displayName: String): Boolean {
    val target = File(file.parentFile, displayName)
    return file.renameTo(target)
  }

  fun openOutputStream(append: Boolean = false): OutputStream = file.outputStream().let { if (append) java.io.FileOutputStream(file, true) else it }
  fun openInputStream(): InputStream = file.inputStream()

  override fun equals(other: Any?) = other is UniFile && other.file == file
  override fun hashCode() = file.hashCode()
  override fun toString() = file.path

  companion object {
    @JvmStatic fun fromFile(file: File?): UniFile? = file?.let { UniFile(it) }

    @JvmStatic
    fun fromUri(context: Context?, uri: Uri?): UniFile? {
      uri ?: return null
      val path = when (uri.scheme) {
        "file" -> uri.path?.let { if (Regex("^/[A-Za-z]:.*").matches(it)) it.drop(1) else it }
        null, "" -> uri.toString()
        else -> null
      } ?: return null
      return UniFile(File(path))
    }

    @JvmStatic fun isFileUri(uri: Uri?): Boolean = uri?.scheme == "file"
  }
}
