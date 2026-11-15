import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("jvm") version "2.4.21"
  id("org.jetbrains.kotlin.plugin.compose") version "2.4.21"
  id("org.jetbrains.compose") version "1.12.2"
}

kotlin { jvmToolchain(21) }

dependencies {
  implementation(compose.desktop.currentOs)
  implementation(compose.material3)
}

compose.desktop {
  application {
    mainClass = "org.nekomanga.desktop.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Exe, TargetFormat.Msi)
      packageName = "Neko"
      packageVersion = "3.8.6"
      description = "Unofficial Windows desktop port of the Neko manga reader"
      vendor = "Neko Desktop (unofficial)"

      windows {
        menuGroup = "Neko"
        shortcut = true
        dirChooser = true
        perUserInstall = true
        // Stable id so upgrades replace the previous install. Do not change.
        upgradeUuid = "6f1c8c3e-5a0b-4f0e-9d57-2b8f3e7a41c2"
      }
    }
  }
}
