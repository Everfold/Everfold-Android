package com.everfold.kotlin.feature.conversation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.localization.localizedString
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.rememberMarkdownState
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.code_block
import everfold.shared.generated.resources.copied
import everfold.shared.generated.resources.copy_code
import kotlinx.coroutines.delay

@Composable
internal fun MessageMarkdown(content: String) {
    val state = rememberMarkdownState(content, retainState = true)
    Markdown(
        markdownState = state,
        modifier = Modifier.fillMaxWidth(),
        components =
            markdownComponents(
                codeFence = { block -> MarkdownCodeFence(block.content, block.node) { code, language, _ -> CodeSnippet(code, language) } },
                codeBlock = { block -> MarkdownCodeBlock(block.content, block.node) { code, language, _ -> CodeSnippet(code, language) } },
            ),
        loading = { Text(content, style = MaterialTheme.typography.bodyLarge) },
        error = { Text(content, style = MaterialTheme.typography.bodyLarge) },
    )
}

@Composable
private fun CodeSnippet(
    code: String,
    language: String?,
) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    language?.takeIf { it.isNotBlank() } ?: localizedString(Res.string.code_block),
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                )
                CopyTextButton(code, localizedString(Res.string.copy_code))
            }
            Text(
                code,
                Modifier.horizontalScroll(rememberScrollState()),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
internal fun CopyTextButton(
    text: String,
    label: String,
) {
    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(1600)
            copied = false
        }
    }
    TextButton(onClick = {
        clipboard.setText(AnnotatedString(text))
        copied = true
    }, enabled = text.isNotEmpty()) {
        Text(if (copied) localizedString(Res.string.copied) else label)
    }
}
