package com.everfold.kotlin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.everfold.kotlin.app.EverfoldApp
import com.everfold.kotlin.mock.MockWorkspace
import com.everfold.kotlin.ui.theme.EverfoldTheme

@Composable
fun App(modifier: Modifier = Modifier) {
    val workspace = viewModel { MockWorkspace() }
    EverfoldTheme(mode = workspace.themeMode, seed = workspace.themeColor.seed) {
        EverfoldApp(workspace, modifier)
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}
