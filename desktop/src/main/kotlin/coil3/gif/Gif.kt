package coil3.gif

import coil3.ImageLoader
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options

/** Animated decoding is a later step: these factories decline, so GIFs show as their first frame via the image decoder. */
class AnimatedImageDecoder { class Factory : Decoder.Factory { override fun create(result: SourceFetchResult, options: Options, imageLoader: ImageLoader): Decoder? = null } }
class GifDecoder { class Factory : Decoder.Factory { override fun create(result: SourceFetchResult, options: Options, imageLoader: ImageLoader): Decoder? = null } }
