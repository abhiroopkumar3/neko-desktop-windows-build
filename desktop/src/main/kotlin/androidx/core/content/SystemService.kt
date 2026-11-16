package androidx.core.content

import android.content.Context

/** There are no Android system services on desktop; callers get null and must cope, as on a device without the service. */
inline fun <reified T : Any> Context.getSystemService(): T? = null
