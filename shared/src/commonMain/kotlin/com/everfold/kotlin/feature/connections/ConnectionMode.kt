package com.everfold.kotlin.feature.connections

import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.connection_local
import everfold.shared.generated.resources.connection_remote
import org.jetbrains.compose.resources.StringResource

enum class ConnectionMode(
    val label: StringResource,
) {
    Local(Res.string.connection_local),
    Remote(Res.string.connection_remote),
}
