package org.nekomanga.desktop.platform

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/** Messages that Android would show as a Toast. The desktop UI collects these into a snackbar. */
object Toasts {
  private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 32, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  val messages: SharedFlow<String> = _messages

  fun show(message: String) {
    System.err.println("[toast] $message")
    _messages.tryEmit(message)
  }
}
