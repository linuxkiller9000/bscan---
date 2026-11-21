# bscan (بسكان) — Android Application

Independent, offline-first Android document scanner built with Kotlin, Jetpack Compose, Material 3, and Room.

- **Application ID:** `com.bscan.app`
- **Minimum SDK:** Android 8.0 (API 26)
- **Target / Compile SDK:** Android 15 (API 35)
- **Primary Performance Target:** Realme 5 Pro (Snapdragon 712, Adreno 616, 8 GB RAM, 1080×2340)
- **Network Permission:** Intentionally omitted (`android.permission.INTERNET` is not declared)

## Build Instructions

### Prerequisites
- JDK 17
- Android SDK Platform 35 (`platforms;android-35`) and Build-Tools 35.0.0 (`build-tools;35.0.0`)
- Either Android Studio Ladybug+ or command-line Gradle wrapper (`./gradlew`)

### Command-Line Debug Build
```bash
cd android
# Ensure ANDROID_HOME points to your Android SDK (or create local.properties with sdk.dir=/path/to/sdk)
./gradlew assembleDebug
```

Debug APK output:
```text
android/app/build/outputs/apk/debug/app-debug.apk
```

### Run JVM Unit Tests
```bash
cd android
./gradlew testDebugUnitTest
```

### Install on Connected Device (ADB)
```bash
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
```
