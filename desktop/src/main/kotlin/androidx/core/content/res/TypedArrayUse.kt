package androidx.core.content.res

import android.content.res.TypedArray

inline fun <R> TypedArray.use(block: (TypedArray) -> R): R = try { block(this) } finally { recycle() }
