package com.everfold.kotlin.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.components.AppSymbol
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.github_repository
import everfold.shared.generated.resources.logo
import everfold.shared.generated.resources.official_website
import everfold.shared.generated.resources.open_in_browser
import everfold.shared.generated.resources.open_source_license
import everfold.shared.generated.resources.preview_notice
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun AboutSettingsPage() {
    val version = appVersion()
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(75.dp),
            )
            Text("Everfold", style = MaterialTheme.typography.headlineSmall)
            if (version != null) Text("v$version", style = MaterialTheme.typography.labelLarge)
            Text(
                localizedString(Res.string.preview_notice),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column {
                AboutLink(
                    title = localizedString(Res.string.official_website),
                    detail = "everfold.org",
                    url = "https://everfold.org/",
                )
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                AboutLink(
                    title = localizedString(Res.string.github_repository),
                    detail = "github.com/Everfold/Everfold-Kotlin",
                    url = "https://github.com/Everfold/Everfold-Kotlin",
                )
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                AboutLink(
                    title = localizedString(Res.string.open_source_license),
                    detail = "Apache License, Version 2.0",
                    url = "https://www.apache.org/licenses/LICENSE-2.0",
                )
            }
        }
    }
}

@Composable
private fun AboutLink(
    title: String,
    detail: String,
    url: String,
) {
    val uriHandler = LocalUriHandler.current
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(detail) },
        trailingContent = { AppIcon(AppSymbol.OpenInNew) },
        modifier =
            Modifier.clickable(
                role = Role.Button,
                onClickLabel = localizedString(Res.string.open_in_browser),
                onClick = { uriHandler.openUri(url) },
            ),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    )
}
