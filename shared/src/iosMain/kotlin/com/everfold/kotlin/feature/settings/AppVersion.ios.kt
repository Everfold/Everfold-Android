package com.everfold.kotlin.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSBundle

@Composable
internal actual fun appVersion(): String? =
    remember {
        (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String)
            ?.takeIf { it.isNotBlank() }
    }
