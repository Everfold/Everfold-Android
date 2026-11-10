package com.everfold.kotlin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.logo
import everfold.shared.generated.resources.window_close
import everfold.shared.generated.resources.window_maximize
import everfold.shared.generated.resources.window_minimize
import everfold.shared.generated.resources.window_restore
import org.jetbrains.compose.resources.painterResource
import java.awt.event.MouseEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WindowScope.DesktopTitleBar(
    state: WindowState,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maximized = state.placement == WindowPlacement.Maximized
    val toggleMaximize = {
        state.placement = if (state.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized
    }
    Surface(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceContainer) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WindowDraggableArea(Modifier.weight(1f)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .onPointerEvent(PointerEventType.Press, PointerEventPass.Initial) {
                            // Observe double clicks without consuming the press needed by WindowDraggableArea.
                            val mouse = it.nativeEvent as? MouseEvent
                            if (mouse?.button == MouseEvent.BUTTON1 && mouse.clickCount == 2) toggleMaximize()
                        }.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painterResource(Res.drawable.logo), contentDescription = null, modifier = Modifier.size(24.dp))
                    Text("Everfold", style = MaterialTheme.typography.titleSmall, maxLines = 1)
                }
            }
            WindowControl(WindowGlyph.Minimize, localizedString(Res.string.window_minimize), { state.isMinimized = true })
            WindowControl(
                if (maximized) WindowGlyph.Restore else WindowGlyph.Maximize,
                localizedString(if (maximized) Res.string.window_restore else Res.string.window_maximize),
                toggleMaximize,
            )
            WindowControl(WindowGlyph.Close, localizedString(Res.string.window_close), onClose)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WindowControl(
    glyph: WindowGlyph,
    label: String,
    onClick: () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        tooltip = { PlainTooltip { Text(label) } },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
            Icon(glyph.vector, contentDescription = label, modifier = Modifier.size(20.dp))
        }
    }
}

private enum class WindowGlyph(
    path: String,
) {
    Minimize("M5,12 L19,12"),
    Maximize("M5,5 L19,5 L19,19 L5,19 Z"),
    Restore("M8,8 L8,4 L20,4 L20,16 L16,16 M4,8 L16,8 L16,20 L4,20 Z"),
    Close("M6,6 L18,18 M18,6 L6,18"),
    ;

    val vector =
        ImageVector
            .Builder(name, 24.dp, 24.dp, 24f, 24f)
            .apply {
                addPath(addPathNodes(path), stroke = SolidColor(Color.Black), strokeLineWidth = 1.5f)
            }.build()
}
