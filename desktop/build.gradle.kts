import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("jvm") version "2.4.21"
  kotlin("plugin.serialization") version "2.4.21"
  id("org.jetbrains.kotlin.plugin.compose") version "2.4.21"
  id("org.jetbrains.compose") version "1.12.2"
  id("com.google.devtools.ksp") version "2.3.13"
}

// ---------------------------------------------------------------------------
// Shared upstream sources
//
// Instead of copying upstream code into this folder (which would diverge on every sync),
// the files listed in shared-sources.txt are copied verbatim from the Android modules into
// build/shared-src at build time and compiled together with the desktop code. Upstream
// changes to those files therefore flow in automatically. Files that still need Android APIs
// are simply not listed yet; see tools/classify.py and tools/prune.py.
// ---------------------------------------------------------------------------
val sharedRoots =
    mapOf(
        "app" to "../app/src/main/java",
        "core" to "../core/src/main/kotlin",
        "constants" to "../constants/src/main/kotlin",
    )
val sharedList =
    file("shared-sources.txt").readLines().map { it.trim() }.filter {
      it.isNotEmpty() && !it.startsWith("#")
    }

val syncSharedSources by
    tasks.registering(Sync::class) {
      into(layout.buildDirectory.dir("shared-src"))
      sharedRoots.forEach { (module, dir) ->
        val includes = sharedList.filter { it.startsWith("$module/") }.map { it.removePrefix("$module/") }
        if (includes.isNotEmpty()) from(dir) { include(includes) }
      }
    }


// ---------------------------------------------------------------------------
// Android resource shim: generates org.nekomanga.R (ids) and the lookup table from the unmodified
// upstream value XML files, and bundles those files into the jar for runtime lookup.
// ---------------------------------------------------------------------------
val resValueFiles =
    listOf(
        file("../constants/src/main/res/values/strings.xml") to "strings.xml",
        file("../app/src/main/res/values/arrays.xml") to "arrays.xml",
    ) +
        (file("../app/src/main/res/values").listFiles { f -> f.name.startsWith("colors") && f.extension == "xml" }
            ?: emptyArray()).sortedBy { it.name }.map { it to it.name }

val generateRShim by
    tasks.registering {
      val outDir = layout.buildDirectory.dir("generated-r")
      inputs.files(resValueFiles.map { it.first })
      outputs.dir(outDir)
      doLast {
        val names = mapOf("string" to sortedSetOf<String>(), "plurals" to sortedSetOf<String>(), "array" to sortedSetOf<String>(), "color" to sortedSetOf<String>())
        val tagToType = mapOf("string" to "string", "plurals" to "plurals", "string-array" to "array", "color" to "color")
        val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        resValueFiles.forEach { (f, _) ->
          val nodes = factory.newDocumentBuilder().parse(f).documentElement.childNodes
          for (i in 0 until nodes.length) {
            val e = nodes.item(i) as? org.w3c.dom.Element ?: continue
            val type = tagToType[e.tagName] ?: continue
            names.getValue(type).add(e.getAttribute("name").replace('.', '_'))
          }
        }
        val bases = mapOf("string" to 0x7f120000, "plurals" to 0x7f130000, "array" to 0x7f030000, "color" to 0x7f060000)
        val dir = outDir.get().asFile.also { it.deleteRecursively(); it.mkdirs() }
        // Upstream has one R class per Gradle module; they all see the same ids here.
        listOf("org.nekomanga", "org.nekomanga.core", "org.nekomanga.constants").forEach { pkg ->
          File(dir, "R_${pkg.replace('.', '_')}.kt").writeText(buildString {
            appendLine("package $pkg\n\n/** Generated from upstream value resources. Do not edit. */\nobject R {")
            names.forEach { (type, set) ->
              appendLine("  object $type {")
              set.forEachIndexed { idx, n -> appendLine("    const val `$n`: Int = ${bases.getValue(type) + idx}") }
              appendLine("  }")
            }
            appendLine("}")
          })
        }
        File(dir, "RTable.kt").writeText(buildString {
          appendLine("package org.nekomanga.desktop.res\n\n/** Generated id tables. Do not edit. */\nobject RTable {")
          bases.forEach { (type, base) -> appendLine("  const val ${type.uppercase()}_BASE = $base") }
          appendLine("  val xmlFiles = listOf(${resValueFiles.joinToString { "\"${it.second}\"" }})")
          names.forEach { (type, set) ->
            appendLine("  val $type: Array<String> = arrayOf(")
            set.chunked(200).forEach { chunk -> appendLine("    " + chunk.joinToString { "\"$it\"" } + ",") }
            appendLine("  )")
          }
          appendLine("}")
        })
      }
    }

tasks.processResources {
  resValueFiles.forEach { (f, name) -> from(f) { into("android-res"); rename { name } } }
}

kotlin {
  jvmToolchain(21)
  sourceSets.main {
    kotlin.srcDir(syncSharedSources.map { it.destinationDir })
    kotlin.srcDir(layout.buildDirectory.dir("generated-r"))
  }
  compilerOptions {
    freeCompilerArgs.addAll(
        "-opt-in=kotlin.RequiresOptIn",
        "-opt-in=kotlin.time.ExperimentalTime",
        "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
        "-opt-in=kotlin.ExperimentalStdlibApi",
        "-opt-in=kotlinx.coroutines.FlowPreview",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
        "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
        "-Xskip-prerelease-check",
    )
  }
}

tasks.named("compileKotlin") { dependsOn(syncSharedSources, generateRShim) }

// KSP (Room) reads the synced upstream sources too.
tasks.matching { it.name == "kspKotlin" }.configureEach { dependsOn(syncSharedSources, generateRShim) }

// Room needs a schema folder for exportSchema = true; keep generated schemas out of upstream.
ksp { arg("room.schemaLocation", layout.buildDirectory.dir("room-schemas").get().asFile.path) }

// Versions mirror the upstream Android catalogs (gradle/*.versions.toml).
dependencies {
  implementation(compose.desktop.currentOs)
  implementation(compose.material3)
  implementation(compose.materialIconsExtended)
  implementation("org.jetbrains.compose.material3:material3-window-size-class:1.9.0")
  implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
  implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
  implementation("androidx.navigation3:navigation3-runtime:1.2.1")
  implementation("org.jetbrains.androidx.navigation3:navigation3-ui:1.2.0")
  implementation("androidx.annotation:annotation:1.11.0")

  val coroutines = "1.11.0"
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutines")
  val serialization = "1.12.0"
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serialization")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-okio:$serialization")

  val okhttp = "5.5.1"
  implementation("com.squareup.okhttp3:okhttp:$okhttp")
  implementation("com.squareup.okhttp3:logging-interceptor:$okhttp")
  implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:$okhttp")
  implementation("com.squareup.okio:okio:3.18.2")

  val retrofit = "3.0.0"
  implementation("com.squareup.retrofit2:retrofit:$retrofit")
  implementation("com.squareup.retrofit2:converter-kotlinx-serialization:$retrofit")
  implementation("com.squareup.retrofit2:converter-scalars:$retrofit")

  val sandwich = "2.4.0"
  implementation("com.github.skydoves:sandwich:$sandwich")
  implementation("com.github.skydoves:sandwich-retrofit:$sandwich")
  implementation("com.github.skydoves:sandwich-retrofit-serialization:$sandwich")

  implementation("org.jsoup:jsoup:1.24.1")
  implementation("uy.kohesive.injekt:injekt-core:1.16.1")
  implementation("com.michael-bull.kotlin-result:kotlin-result:2.3.1")
  implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:2.3.1")
  implementation("org.isomorphism:token-bucket:1.7")
  implementation("io.github.g00fy2:versioncompare:1.5.0")
  implementation("com.jakewharton:disklrucache:2.0.2")
  implementation("org.tachiyomi:source-api:1.1")

  val room = "2.8.5"
  implementation("androidx.room:room-runtime:$room")
  implementation("androidx.sqlite:sqlite-bundled:2.6.2")
  ksp("androidx.room:room-compiler:$room")

  val coil = "3.6.3"
  implementation("io.coil-kt.coil3:coil:$coil")
  implementation("io.coil-kt.coil3:coil-compose:$coil")
  implementation("io.coil-kt.coil3:coil-svg:$coil")
  implementation("io.coil-kt.coil3:coil-network-okhttp:$coil")
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

// Headless smoke test for the shared database layer (runs on any OS with a JDK).
tasks.register<JavaExec>("dbSelfTest") {
  group = "verification"
  classpath = sourceSets["main"].runtimeClasspath
  mainClass.set("org.nekomanga.desktop.selftest.DbSelfTestKt")
}
