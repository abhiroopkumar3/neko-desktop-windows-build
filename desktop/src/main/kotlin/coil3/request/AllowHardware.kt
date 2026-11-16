package coil3.request

/** Hardware bitmaps are an Android concept; accepted and ignored on desktop. */
fun coil3.ImageLoader.Builder.allowRgb565(enable: Boolean): coil3.ImageLoader.Builder = this
fun coil3.ImageLoader.Builder.allowHardware(enable: Boolean): coil3.ImageLoader.Builder = this
fun ImageRequest.Builder.allowRgb565(enable: Boolean): ImageRequest.Builder = this
fun ImageRequest.Builder.allowHardware(enable: Boolean): ImageRequest.Builder = this
