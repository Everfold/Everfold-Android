package com.everfold.kotlin.feature.settings

import androidx.compose.runtime.Composable
import java.util.Properties

private object DesktopAppVersion {
    val value: String? =
        javaClass.getResourceAsStream("/everfold-app.properties")?.use { stream ->
            Properties().apply { load(stream) }.getProperty("version")?.takeIf { it.isNotBlank() }
        }
}

@Composable
internal actual fun appVersion(): String? = DesktopAppVersion.value
