{
  lib,
  stdenv,
  stdenvNoCC,
  gradle-packages,
  zulu25,
  autoPatchelfHook,
  alsa-lib,
  zlib,
  makeWrapper,
  makeDesktopItem,
  copyDesktopItems,
  libglvnd,
  libX11,
  libXext,
  libXi,
  libXrender,
  libXtst,
  fontconfig,
  gtk3,
}:
let
  gradle =
    (gradle-packages.mkGradle {
      version = "9.7.0";
      hash = "sha256-hPu6Rcf0xkq8d0YOHAD1Qen5YOPH7SU48e3hnqzYc64=";
      defaultJava = zulu25;
    }).wrapped;

  runtimeLibraries = [
    libglvnd
    libX11
    fontconfig
    gtk3
    stdenv.cc.cc.lib
  ];
in
stdenvNoCC.mkDerivation (finalAttrs: {
  pname = "everfold-kotlin";
  version = "1.0.0";

  src = lib.cleanSourceWith {
    src = ../../.;
    filter =
      path: type:
      lib.cleanSourceFilter path type
      && !(builtins.elem (baseNameOf path) [
        "build"
        ".gradle"
        ".kotlin"
        ".codegraph"
        ".direnv"
        ".agents"
        "local.properties"
        "result"
      ]);
  };

  nativeBuildInputs = [
    gradle
    autoPatchelfHook
    makeWrapper
    copyDesktopItems
  ];

  buildInputs = [
    alsa-lib
    zlib
    libX11
    libXext
    libXi
    libXrender
    libXtst
  ];

  mitmCache = gradle.fetchDeps {
    pkg = finalAttrs.finalPackage;
    data = ../deps.json;
  };

  env.JAVA_HOME = zulu25.home;

  gradleFlags = [
    "--no-configuration-cache"
    "-Pkotlin.compiler.execution.strategy=in-process"
  ];

  gradleBuildTask = ":desktopApp:packageUberJarForCurrentOS";
  gradleCheckTask = ":detekt :shared:jvmTest";
  gradleUpdateTask = "${finalAttrs.gradleBuildTask} ${finalAttrs.gradleCheckTask}";

  doCheck = true;

  desktopItems = [
    (makeDesktopItem {
      name = "everfold-kotlin";
      desktopName = "Everfold";
      exec = "everfold-kotlin";
      icon = "everfold-kotlin";
      categories = [ "Development" ];
      terminal = false;
    })
  ];

  installPhase = ''
    runHook preInstall

    install -Dm644 desktopApp/build/compose/jars/*.jar "$out/share/everfold-kotlin/everfold-kotlin.jar"
    install -Dm644 assets/branding/logo.svg "$out/share/icons/hicolor/scalable/apps/everfold-kotlin.svg"

    javaModules=$(${zulu25}/bin/jdeps --ignore-missing-deps --multi-release 25 \
      --print-module-deps "$out/share/everfold-kotlin/everfold-kotlin.jar")

    ${zulu25}/bin/jlink --add-modules "$javaModules,jdk.crypto.ec" \
      --strip-debug --no-man-pages --no-header-files --compress=zip-6 --output "$out/lib/jre"

    makeWrapper "$out/lib/jre/bin/java" "$out/bin/everfold-kotlin" \
      --prefix LD_LIBRARY_PATH : "$out/lib/jre/lib:${lib.makeLibraryPath runtimeLibraries}" \
      --add-flags "--enable-native-access=ALL-UNNAMED -jar $out/share/everfold-kotlin/everfold-kotlin.jar"

    runHook postInstall
  '';

  disallowedReferences = [
    zulu25
    gradle
  ];

  meta = {
    description = "Everfold Kotlin Desktop";
    license = lib.licenses.asl20;
    mainProgram = "everfold-kotlin";
    platforms = [ "x86_64-linux" ];
  };
})
