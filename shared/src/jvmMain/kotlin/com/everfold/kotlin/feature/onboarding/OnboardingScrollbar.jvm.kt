package com.everfold.kotlin.feature.onboarding

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun OnboardingScrollbar(
    scroll: ScrollState,
    modifier: Modifier,
) {
    if (scroll.maxValue > 0) VerticalScrollbar(rememberScrollbarAdapter(scroll), modifier.fillMaxHeight())
}
