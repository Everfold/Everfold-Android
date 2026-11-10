package com.everfold.kotlin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.everfold.kotlin.app.EverfoldApp
import com.everfold.kotlin.feature.onboarding.OnboardingScreen
import com.everfold.kotlin.mock.MockWorkspace
import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.AppLanguageEnvironment
import com.everfold.kotlin.ui.theme.EverfoldTheme

@Composable
fun App(modifier: Modifier = Modifier) {
    var language by rememberSaveable { mutableStateOf(AppLanguage.System) }
    val workspace = viewModel { MockWorkspace() }
    AppLanguageEnvironment(language) {
        EverfoldTheme(mode = workspace.themeMode, color = workspace.themeColor) {
            if (workspace.initialized) {
                EverfoldApp(workspace, language, { language = it }, modifier)
            } else {
                OnboardingScreen(workspace, language, { language = it }, workspace::completeSetup, modifier)
            }
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}
