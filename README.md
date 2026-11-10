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
# Init
./kotlin

# Android
./kotlin build -m androidApp
./kotlin run -m androidApp

# Desktop
./kotlin build -m desktopApp
./kotlin run -m desktopApp

# iOS
./kotlin build -m iosApp
./kotlin run -m iosApp

# Check
./kotlin check

# Test
./kotlin test

# Update
./kotlin update
```
