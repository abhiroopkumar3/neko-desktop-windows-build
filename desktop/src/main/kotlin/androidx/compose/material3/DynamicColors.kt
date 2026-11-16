package androidx.compose.material3

import android.content.Context

/** Wallpaper-based colors are an Android 12 feature; desktop uses the app's own palettes. */
fun dynamicLightColorScheme(context: Context): ColorScheme = lightColorScheme()
fun dynamicDarkColorScheme(context: Context): ColorScheme = darkColorScheme()
