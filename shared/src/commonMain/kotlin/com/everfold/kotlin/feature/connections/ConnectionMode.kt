package com.everfold.kotlin.feature.connections

import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.connection_local
import everfold.shared.generated.resources.connection_remote
import everfold.shared.generated.resources.local_description
import everfold.shared.generated.resources.remote_description
import org.jetbrains.compose.resources.StringResource

enum class ConnectionMode(
    val label: StringResource,
    val description: StringResource,
) {
    Local(Res.string.connection_local, Res.string.local_description),
    Remote(Res.string.connection_remote, Res.string.remote_description),
}
