# Neko Desktop (Windows port)

Standalone Gradle build, separate from the Android build.

## How upstream code is reused

`shared-sources.txt` lists upstream files (from `app`, `core`, `constants`) that compile
unchanged on desktop. At build time they are copied verbatim into `build/shared-src`, so upstream
changes to them flow in through the normal sync. Nothing upstream is edited.

Android APIs those files rely on are provided by a thin compatibility layer in `src/main/kotlin`:

| Package | What it does on Windows |
|---|---|
| `android.content.Context`, `android.content.res.Resources` | Resolves `R.string`, plurals, arrays and colors from upstream's own value XML (bundled in the jar) |
| `org.nekomanga.R`, `org.nekomanga.desktop.res.RTable` | Generated from upstream XML at build time (task `generateRShim`) |
| `android.net.Uri`, `android.util.Base64`, `androidx.core.net` | Pure-JVM implementations |
| `android.os.Build`, `android.os.Bundle`, `android.graphics.Color` | Desktop values / map-backed |
| `android.widget.Toast` | Routed to `Toasts.messages` (shown as a snackbar by the UI) |
| `timber.log.Timber` | JVM implementation (Timber 5 is Android only) |
| `org.nekomanga.BuildConfig` | Constants, keep in sync with `app/build.gradle.kts` |

## Growing the shared set

```
python3 -I tools/classify.py .. shared-sources.txt --all   # start from every upstream file
python3 -I tools/prune.py                                  # drop files that do not compile yet
```

`prune.py` compiles, removes every file with errors, and repeats until the build is green.
Add a shim or dependency, then re-run to see how many files come back.

## Build

```
./gradlew run                    # launch the app
./gradlew packageExe packageMsi  # Windows installers (on Windows; CI does this)
```
