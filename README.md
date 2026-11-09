# Everfold Android

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
# Debug Build
./gradlew :app:assembleDebug

# Release Build
./gradlew :app:assembleRelease

# AAB Release Bundle
./gradlew :app:bundleRelease

# Check dependency updates
./gradlew dependencyUpdates

# Check formatting
./gradlew spotlessCheck

# Apply formatting
./gradlew spotlessApply

# Kotlin & Compose static analysis
./gradlew :app:detekt

# Unit Test
./gradlew :app:testDebugUnitTest

# Lint
./gradlew :app:lintDebug

# Compose Preview
android preview list :app

# ADB Debug
./gradlew :app:installDebug

# Create Emulator
avdmanager create avd --name everfold-37 --device pixel_9_pro --package 'system-images;android-37.0;google_apis;x86_64'

# Start Emulator
emulator -avd everfold-37 -gpu host

# Test on device
./gradlew :app:connectedDebugAndroidTest
```
