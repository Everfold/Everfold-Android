# Everfold Kotlin

## Usage

### Nix

```bash
nix run github:Everfold/Everfold-Kotlin#everfold-kotlin
```

## Build

### Development Environment

#### Nix Develop

```bash
# Nix Shell
nix develop

# OR Direnv
apt install -y direnv
echo 'use flake' > .envrc
direnv allow

# Update
nix flake update

# Update nix/deps.json
"$(nix build --no-link --print-out-paths .#everfold-kotlin.mitmCache.updateScript)"
```

```fish
# Fish
set -l update_script (nix build --no-link --print-out-paths .#everfold-kotlin.mitmCache.updateScript)
and "$update_script"
```

### Project

```bash
# Android Build
./gradlew :androidApp:assembleDebug

# Desktop Hot reload
./gradlew :desktopApp:hotRun --auto

# Desktop
./gradlew :desktopApp:run

# Check formatting
./gradlew spotlessCheck

# Formatting
./gradlew spotlessApply

# Verification
./gradlew :check

# Android tests
./gradlew :shared:testAndroidHostTest

# Desktop tests
./gradlew :shared:jvmTest

# iOS tests
./gradlew :shared:iosSimulatorArm64Test

# Check dependency and plugin updates
./gradlew dependencyUpdates
```

### Android Emulator

```bash
# Create Android Emulator
avdmanager create avd --name everfold --package 'system-images;android-37.0;google_apis;x86_64' --device pixel_9_pro

# Run in Android Emulator
emulator -avd everfold -gpu host
android emulator list
android run androidApp/build/outputs/apk/debug/androidApp-debug.apk
```
