package com.everfold.kotlin.app.navigation

import com.everfold.kotlin.ui.components.AppSymbol
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.nav_connections
import everfold.shared.generated.resources.nav_sessions
import everfold.shared.generated.resources.nav_settings
import org.jetbrains.compose.resources.StringResource

enum class AppDestination(
    val label: StringResource,
    val icon: AppSymbol,
) {
    Sessions(Res.string.nav_sessions, AppSymbol.Chat),
    Connections(Res.string.nav_connections, AppSymbol.Connection),
    Settings(Res.string.nav_settings, AppSymbol.Settings),
}
