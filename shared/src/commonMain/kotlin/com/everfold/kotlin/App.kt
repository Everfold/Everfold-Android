package com.everfold.kotlin

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.everfold.kotlin.app.EverfoldApp
import com.everfold.kotlin.mock.MockWorkspace
import com.everfold.kotlin.ui.theme.EverfoldTheme

@Composable
@Preview
fun App() {
    val workspace = viewModel { MockWorkspace() }
    EverfoldTheme(mode = workspace.themeMode) {
        EverfoldApp(workspace)
    }
}
