import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask

plugins {
    base
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

detekt {
    // Analyze every platform source set without requiring platform compilation.
    source.setFrom("androidApp/src", "desktopApp/src", "shared/src")
    config.setFrom(layout.projectDirectory.file("config/detekt/detekt.yml"))
    baseline.set(layout.projectDirectory.file("config/detekt/baseline.xml"))
    buildUponDefaultConfig.set(true)
    basePath.set(layout.projectDirectory)
}

tasks.withType<Detekt>().configureEach {
    include("**/*.kt")
    exclude("**/build/**")
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    include("**/*.kt")
    exclude("**/build/**")
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    checkConstraints = true
    rejectPreReleases = true
    outputFormatter = "plain,json"
    // Hot Reload's custom usage attributes cannot be serialized during update checks.
    filterConfigurations = Spec<Configuration> { !it.name.startsWith("composeHotReload") }
}

spotless {
    kotlin {
        target("*/src/**/*.kt")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
            .customRuleSets(
                listOf(
                    libs.composeRules.ktlint
                        .get()
                        .toString(),
                ),
            )
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts", "*/build.gradle.kts", "gradle/**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("config") {
        target(
            ".editorconfig",
            ".gitignore",
            "*.properties",
            "*.toml",
            "*.yml",
            "*.yaml",
            "gradle/**/*.properties",
            "gradle/**/*.toml",
            "config/**/*.yml",
            "config/**/*.yaml",
            ".github/**/*.yml",
            ".github/**/*.yaml",
            "*/gradle.properties",
        )
        targetExclude("local.properties", "**/build/**")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
