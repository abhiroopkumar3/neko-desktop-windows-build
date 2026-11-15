// Standalone Gradle build for the Windows desktop port.
// Kept separate from the Android build so upstream syncs never conflict with it
// and so it can be built without the Android SDK.
pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
    maven { setUrl("https://jitpack.io") }
  }
}

rootProject.name = "NekoDesktop"
