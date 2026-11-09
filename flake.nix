{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flakelight.url = "github:nix-community/flakelight";
  };

  outputs =
    { flakelight, ... }:
    flakelight ./. {
      nixpkgs.config = {
        allowUnfree = true;
        android_sdk.accept_license = true;
      };

      devShell =
        pkgs:
        let
          jdk = pkgs.openjdk25_headless;

          gradle = pkgs.gradle_9.override {
            java = jdk;
          };

          androidSdk = pkgs.androidenv.composeAndroidPackages {
            platformVersions = [ "36" ];
            buildToolsVersions = [ "36.0.0" ];
            includeEmulator = false;
            includeSystemImages = false;
            includeSources = false;
          };
        in
        {
          packages = with pkgs; [
            jdk
            gradle
          ];

          env = rec {
            ANDROID_HOME = "${androidSdk.androidsdk}/libexec/android-sdk/";
            ANDROID_SDK_ROOT = ANDROID_HOME;
          };

          shellHook = ''
            export ANDROID_AVD_HOME="$HOME/.android/avd"
            export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$PATH"
            mkdir -p "$ANDROID_AVD_HOME"
          '';
        };
    };
}
