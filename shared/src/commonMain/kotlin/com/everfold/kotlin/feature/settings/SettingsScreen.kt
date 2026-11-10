package com.everfold.kotlin.feature.settings

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.SettingsLayout
import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.theme.ThemeColorSelection
import com.everfold.kotlin.ui.theme.ThemeMode
import com.everfold.kotlin.ui.theme.ThemePreset
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.about
import everfold.shared.generated.resources.conversation_settings
import everfold.shared.generated.resources.display_settings
import everfold.shared.generated.resources.nav_settings
import everfold.shared.generated.resources.theme_settings

private enum class SettingsPage { Theme, Display, Conversation, About }

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
    textSize: ConversationTextSize,
    onTextSizeChange: (ConversationTextSize) -> Unit,
    shortcut: SendShortcut,
    onShortcutChange: (SendShortcut) -> Unit,
    collapsePrompts: Boolean,
    onCollapsePromptsChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    var page by rememberSaveable { mutableStateOf(SettingsPage.Theme) }
    val themeScrollState = rememberScrollState()
    val conversationScrollState = rememberScrollState()
    val displayScrollState = rememberScrollState()
    val aboutScrollState = rememberScrollState()
    SettingsLayout(
        title = localizedString(Res.string.nav_settings),
        modifier = modifier,
        onBack = onBack,
        scrollState =
            when (page) {
                SettingsPage.Theme -> themeScrollState
                SettingsPage.Display -> displayScrollState
                SettingsPage.Conversation -> conversationScrollState
                SettingsPage.About -> aboutScrollState
            },
        navigation = {
            SecondaryScrollableTabRow(selectedTabIndex = page.ordinal, edgePadding = 0.dp) {
                SettingsPage.entries.forEach { item ->
                    Tab(
                        selected = page == item,
                        onClick = { page = item },
                        text = {
                            Text(
                                localizedString(
                                    when (item) {
                                        SettingsPage.Theme -> Res.string.theme_settings
                                        SettingsPage.Display -> Res.string.display_settings
                                        SettingsPage.Conversation -> Res.string.conversation_settings
                                        SettingsPage.About -> Res.string.about
                                    },
                                ),
                            )
                        },
                    )
                }
            }
        },
    ) {
        when (page) {
            SettingsPage.Theme -> {
                ThemeSettingsPage(
                    themeMode = themeMode,
                    themeColor = themeColor,
                    onThemeChange = onThemeChange,
                    onSystemColorChange = onSystemColorChange,
                    onPresetChange = onPresetChange,
                    onCustomColorChange = onCustomColorChange,
                )
            }

            SettingsPage.Display -> {
                DisplaySettingsPage(language, onLanguageChange, textSize, onTextSizeChange)
            }

            SettingsPage.Conversation -> {
                ConversationSettingsPage(shortcut, onShortcutChange, collapsePrompts, onCollapsePromptsChange)
            }

            SettingsPage.About -> {
                AboutSettingsPage()
            }
        }
    }
}
