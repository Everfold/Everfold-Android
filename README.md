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

# Unit Test
./gradlew :app:testDebugUnitTest

# Lint
./gradlew :app:lintDebug

# Check
./gradlew :app:check

# ADB Debug
./gradlew :app:installDebug

# Test on device
./gradlew :app:connectedDebugAndroidTest
```
