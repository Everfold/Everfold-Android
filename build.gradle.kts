import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    checkConstraints = true
}

// Prune directories in the file tree itself so Gradle never snapshots their contents.
val spotlessSources =
    fileTree(rootDir) {
        exclude(
            "**/build/**",
            "**/generated/**",
            "**/.gradle/**",
            "**/.kotlin/**",
            "**/.git/**",
            "**/.codegraph/**",
            "**/.direnv/**",
            "**/.idea/**",
            "**/node_modules/**",
        )
    }

spotless {
    kotlin {
        target(spotlessSources.matching { include("**/src/**/*.kt") })
        ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
    }
    kotlinGradle {
        target(spotlessSources.matching { include("**/*.gradle.kts") })
        ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
    }
    format("xml") {
        target(
            spotlessSources.matching {
                include("**/src/**/AndroidManifest.xml", "**/src/**/res/**/*.xml")
                // Text resources can contain significant whitespace; raw resources are arbitrary
                // data.
                exclude("**/res/values*/**", "**/res/raw*/**")
            }
        )
        eclipseWtp(EclipseWtpFormatterStep.XML, libs.versions.eclipseWtp.get())
            .configFile("config/spotless/xml.properties")
        trimTrailingWhitespace()
        endWithNewline()
    }
    flexmark {
        target(spotlessSources.matching { include("**/*.md") })
        flexmark(libs.versions.flexmark.get())
            .emulationProfile("COMMONMARK")
            .extensions(
                "Tables",
                "TaskList",
                "YamlFrontMatter",
                "com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension",
                "com.vladsch.flexmark.ext.autolink.AutolinkExtension",
            )
        endWithNewline()
    }
    format("misc") {
        target(
            spotlessSources.matching {
                include(
                    ".editorconfig",
                    ".gitattributes",
                    "**/.gitignore",
                    "**/gradle.properties",
                    "**/gradle/*.toml",
                    "**/gradle/wrapper/*.properties",
                    ".github/workflows/*.yml",
                    ".github/workflows/*.yaml",
                    "config/**/*.yml",
                    "config/**/*.yaml",
                    "config/**/*.properties",
                )
            }
        )
        trimTrailingWhitespace()
        endWithNewline()
    }
}
