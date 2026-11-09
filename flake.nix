{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flakelight.url = "github:nix-community/flakelight";
  };

  outputs =
    { flakelight, ... }@inputs:
    flakelight ./. {
      inherit inputs;

      nixpkgs.config = {
        allowUnfree = true;
        android_sdk.accept_license = true;
      };

      devShell =
        pkgs:
        let
          jdk = pkgs.jdk17;
          buildToolsVersion = "36.0.0";
          androidSdk = pkgs.androidenv.composeAndroidPackages {
            platformVersions = [ "37" ];
            buildToolsVersions = [ buildToolsVersion ];
            includeEmulator = false;
            includeSystemImages = false;
            includeSources = false;
            includeNDK = false;
          };
          sdk = "${androidSdk.androidsdk}/libexec/android-sdk";
        in
        {
          packages = [
            jdk
            pkgs.android-cli
            androidSdk.androidsdk
          ];

          env = {
            JAVA_HOME = jdk.home;
            ANDROID_HOME = sdk;
            ANDROID_SDK_ROOT = sdk;
            GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${sdk}/build-tools/${buildToolsVersion}/aapt2";
          };

          shellHook = ''
            export ANDROID_AVD_HOME="$HOME/.android/avd"
            mkdir -p "$ANDROID_AVD_HOME"
          '';
        };
    };
}
