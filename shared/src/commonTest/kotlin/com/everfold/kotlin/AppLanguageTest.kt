package com.everfold.kotlin

import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.resolve
import kotlin.test.Test
import kotlin.test.assertEquals

class AppLanguageTest {
    @Test
    fun systemLanguageUsesEnglishOnlyWhenSupported() {
        listOf("en", "en-US", "en_GB", "EN-au").forEach {
            assertEquals(AppLanguage.English, AppLanguage.System.resolve(it))
        }
        listOf("zh", "zh-Hans-CN", "zh-Hant-TW", "ja-JP", "fr", "").forEach {
            assertEquals(AppLanguage.Chinese, AppLanguage.System.resolve(it))
        }
    }

    @Test
    fun explicitSelectionOverridesSystemLanguage() {
        assertEquals(AppLanguage.Chinese, AppLanguage.Chinese.resolve("en-US"))
        assertEquals(AppLanguage.English, AppLanguage.English.resolve("zh-CN"))
    }
}
