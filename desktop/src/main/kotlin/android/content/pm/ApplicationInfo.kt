package android.content.pm

class ApplicationInfo { val flags: Int = 0; val dataDir: String get() = org.nekomanga.desktop.platform.AppPaths.home.path; companion object { const val FLAG_DEBUGGABLE = 2 } }
