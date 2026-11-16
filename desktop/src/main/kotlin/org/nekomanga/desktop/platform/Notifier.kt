package org.nekomanga.desktop.platform

import android.app.Notification
import java.awt.SystemTray
import java.awt.TrayIcon

/** Turns upstream notifications into Windows tray balloons. Later phases add actions and a notification centre. */
object Notifier {
  private var icon: TrayIcon? = null

  private fun tray(): TrayIcon? = icon ?: runCatching {
    if (!SystemTray.isSupported()) return null
    val image = java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB)
    TrayIcon(image, "Neko").also { it.isImageAutoSize = true; SystemTray.getSystemTray().add(it); icon = it }
  }.getOrNull()

  fun show(id: Int, n: Notification) {
    val title = n.title?.toString() ?: return
    tray()?.displayMessage(title, n.text?.toString().orEmpty(), TrayIcon.MessageType.INFO)
  }
}
