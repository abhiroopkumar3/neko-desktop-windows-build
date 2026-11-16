package coil3

import android.content.Context

/** Android's Context.imageLoader extension: the app-wide singleton loader. */
val Context.imageLoader: ImageLoader get() = SingletonImageLoader.get(PlatformContext.INSTANCE)
