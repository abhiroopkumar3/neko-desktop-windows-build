package coil3.request

/** Hardware bitmaps are an Android concept; accepted and ignored on desktop. */
fun ImageRequest.Builder.allowHardware(enable: Boolean): ImageRequest.Builder = this
