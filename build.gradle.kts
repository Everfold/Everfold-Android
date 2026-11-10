plugins {
    base
    alias(libs.plugins.spotless)
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
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
            ".github/**/*.yml",
            ".github/**/*.yaml",
            "*/gradle.properties",
        )
        targetExclude("local.properties", "**/build/**")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
