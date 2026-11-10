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
# Android
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
