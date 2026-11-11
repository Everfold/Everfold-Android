pkgs:
let
  jdk = pkgs.zulu25;
  buildToolsVersion = "36.0.0";
  androidSdk = pkgs.androidenv.composeAndroidPackages {
    platformVersions = [ "37.0" ];
    buildToolsVersions = [ buildToolsVersion ];
    includeEmulator = true;
    includeSystemImages = true;
    systemImageTypes = [ "google_apis" ];
    abiVersions = [ "x86_64" ];
    includeSources = false;
    includeNDK = true;
    ndkVersions = [ "28.2.13676358" ];
  };
  sdk = "${androidSdk.androidsdk}/libexec/android-sdk";
in
pkgs.mkShell rec {
  buildInputs = with pkgs; [
    libglvnd
    libX11
    libXext
    libXi
    libXrender
    libXtst
    fontconfig
    freetype
    stdenv.cc.cc.lib
  ];

  packages = [
    jdk
    androidSdk.androidsdk
  ];

  env = {
    LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath buildInputs;
    JAVA_HOME = jdk.home;
    ANDROID_HOME = sdk;
    ANDROID_SDK_ROOT = sdk;
    GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${sdk}/build-tools/${buildToolsVersion}/aapt2";
  };

  shellHook = ''
    export ANDROID_AVD_HOME="$HOME/.android/avd"
    mkdir -p "$ANDROID_AVD_HOME"
  '';
}
