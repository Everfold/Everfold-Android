package com.everfold.kotlin.feature.settings

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.everfold.kotlin.ui.components.SettingsLayout
import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.theme.ThemeColorSelection
import com.everfold.kotlin.ui.theme.ThemeMode
import com.everfold.kotlin.ui.theme.ThemePreset
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.about
import everfold.shared.generated.resources.display_settings
import everfold.shared.generated.resources.nav_settings

private enum class SettingsPage { Display, About }

@Composable
fun SettingsScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    themeMode: ThemeMode,
    themeColor: ThemeColorSelection,
    onThemeChange: (ThemeMode) -> Unit,
    onSystemColorChange: () -> Unit,
    onPresetChange: (ThemePreset) -> Unit,
    onCustomColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    var page by rememberSaveable { mutableStateOf(SettingsPage.Display) }
    val displayScrollState = rememberScrollState()
    val aboutScrollState = rememberScrollState()
    SettingsLayout(
        title = localizedString(Res.string.nav_settings),
        modifier = modifier,
        onBack = onBack,
        scrollState = if (page == SettingsPage.Display) displayScrollState else aboutScrollState,
        navigation = {
            SecondaryTabRow(selectedTabIndex = page.ordinal) {
                SettingsPage.entries.forEach { item ->
                    Tab(
                        selected = page == item,
                        onClick = { page = item },
                        text = {
                            Text(localizedString(if (item == SettingsPage.Display) Res.string.display_settings else Res.string.about))
                        },
                    )
                }
            }
        },
    ) {
        when (page) {
            SettingsPage.Display -> {
                DisplaySettingsPage(
                    language = language,
                    onLanguageChange = onLanguageChange,
                    themeMode = themeMode,
                    themeColor = themeColor,
                    onThemeChange = onThemeChange,
                    onSystemColorChange = onSystemColorChange,
                    onPresetChange = onPresetChange,
                    onCustomColorChange = onCustomColorChange,
                )
            }

            SettingsPage.About -> {
                AboutSettingsPage()
            }
        }
    }
}
