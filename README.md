# Everfold Kotlin

## Build

### Development Environment

#### Nix

```bash
# Nix Shell
nix develop

# OR Direnv
apt install -y direnv
echo 'use flake' > .envrc
direnv allow
```

### Project

```bash
# Android Build
./gradlew :androidApp:assembleDebug

# Desktop Hot reload
./gradlew :desktopApp:hotRun --auto

# Desktop
./gradlew :desktopApp:run

# Android tests
./gradlew :shared:testAndroidHostTest

# Desktop tests
./gradlew :shared:jvmTest

# iOS tests
./gradlew :shared:iosSimulatorArm64Test
```

#### Android Emulator

```bash
# Create Android Emulator
avdmanager create avd --name everfold --package 'system-images;android-37.0;google_apis;x86_64' --device pixel_9_pro

# Run in Android Emulator
emulator -avd everfold -gpu host
android emulator list
android run androidApp/build/outputs/apk/debug/androidApp-debug.apk
```
