package com.everfold.kotlin.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.components.AppSymbol
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.cluster_hint
import everfold.shared.generated.resources.feature_agent
import everfold.shared.generated.resources.feature_agent_detail
import everfold.shared.generated.resources.feature_modes
import everfold.shared.generated.resources.feature_modes_detail
import everfold.shared.generated.resources.feature_sessions
import everfold.shared.generated.resources.feature_sessions_detail
import everfold.shared.generated.resources.mode_cluster
import everfold.shared.generated.resources.mode_offline
import everfold.shared.generated.resources.offline_hint
import everfold.shared.generated.resources.onboarding_intro

@Composable
internal fun OnboardingScrollArea(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    Box(modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(scroll).padding(top = 24.dp, bottom = 24.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            content = content,
        )
        OnboardingScrollbar(scroll, Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
internal expect fun OnboardingScrollbar(
    scroll: ScrollState,
    modifier: Modifier = Modifier,
)

@Composable
internal fun WelcomeFeatures(wide: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
        Text(
            localizedString(Res.string.onboarding_intro),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val features =
            listOf(
                Triple(AppSymbol.Chat, Res.string.feature_sessions, Res.string.feature_sessions_detail),
                Triple(AppSymbol.Spark, Res.string.feature_agent, Res.string.feature_agent_detail),
                Triple(AppSymbol.Connection, Res.string.feature_modes, Res.string.feature_modes_detail),
            )
        FlowRow(
            Modifier.fillMaxWidth(),
            maxItemsInEachRow = if (wide) 3 else 1,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            features.forEach { (icon, title, detail) ->
                Surface(Modifier.weight(1f), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                    Column(
                        Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        FeatureIcon(icon)
                        Text(localizedString(title), style = MaterialTheme.typography.titleMedium)
                        Text(
                            localizedString(detail),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ModeSelection(
    cluster: Boolean,
    onChange: (Boolean) -> Unit,
    wide: Boolean,
) {
    FlowRow(
        Modifier.fillMaxWidth().selectableGroup(),
        maxItemsInEachRow = if (wide) 2 else 1,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        listOf(false, true).forEach { option ->
            val selected = cluster == option
            Surface(
                Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .selectable(selected, role = Role.RadioButton, onClick = { onChange(option) })
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FeatureIcon(if (option) AppSymbol.Connection else AppSymbol.Spark)
                        RadioButton(selected, onClick = null)
                    }
                    Text(
                        localizedString(if (option) Res.string.mode_cluster else Res.string.mode_offline),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        localizedString(if (option) Res.string.cluster_hint else Res.string.offline_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureIcon(symbol: AppSymbol) {
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer) {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { AppIcon(symbol) }
    }
}
