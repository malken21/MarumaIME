package dev.marumasa.marumaime.ui.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import dev.marumasa.marumaime.*
import dev.marumasa.marumaime.ui.components.FlickKeyButton
import dev.marumasa.marumaime.ui.components.KeyButton
import dev.marumasa.marumaime.ui.theme.KeyboardColors

@Composable
fun TenKeyLayout(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit,
    onMoveCursor: (CursorDirection) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val japaneseKeys = listOf(
        listOf("あ", "か", "さ"),
        listOf("た", "な", "は"),
        listOf("ま", "や", "ら"),
        listOf("゛゜", "わ", "小")
    )

    val symbolKeys = if (viewModel.isShifted) {
        listOf(
            listOf("S1", "S2", "S3"),
            listOf("S4", "S5", "S6"),
            listOf("S7", "S8", "S9"),
            listOf("", "S0", "")
        )
    } else {
        listOf(
            listOf(".,", "@#", "()"),
            listOf("-_", "\"'", "¥$"),
            listOf("%|", "…", "±"),
            listOf("", "", "")
        )
    }

    val keys = if (viewModel.mode == KeyboardMode.Symbol) symbolKeys else japaneseKeys

    val mapping = FlickMapping.mapping

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Column: Configuration (Weight 1.2f)
        Column(
            modifier = Modifier.weight(1.2f).padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Shift
            KeyButton(
                text = if (viewModel.isShifted) "⬆️" else "⇧",
                modifier = Modifier.fillMaxWidth().weight(2f),
                backgroundColor = if (viewModel.isShifted) KeyboardColors.Action else KeyboardColors.Special,
                contentColor = if (viewModel.isShifted) Color.White else KeyboardColors.Text
            ) {
                viewModel.toggleShift()
            }
            // Mode Toggle
            KeyButton(
                text = when (viewModel.mode) {
                    KeyboardMode.English -> "EN"
                    KeyboardMode.Japanese -> "あ"
                    KeyboardMode.Symbol -> "SYM"
                },
                modifier = Modifier.fillMaxWidth().weight(2f),
                backgroundColor = KeyboardColors.Special,
                contentColor = KeyboardColors.Text
            ) {
                viewModel.toggleMode()
            }
            // Layout Toggle
            KeyButton(
                text = "QW",
                modifier = Modifier.fillMaxWidth().weight(2.0f),
                backgroundColor = KeyboardColors.Special,
                contentColor = KeyboardColors.Text
            ) {
                viewModel.toggleLayout()
            }
        }

        // Center Column: Flick Grid & Compact Navigation (Weight 3.6f)
        Column(
            modifier = Modifier.weight(3.6f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rows 0-3: Flick Grid (Height 1.3f each)
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1.3f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { key ->
                        val chars = mapping[key] ?: listOf(key, "", "", "", "")
                        FlickKeyButton(
                            chars = chars,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onFlick = { direction ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.onFlick(key, direction, onCommit, onUpdateComposing)
                            }
                        )
                    }
                }
            }

            // Row 4: Compact Navigation Row (Height 0.8f)
            Row(
                modifier = Modifier.fillMaxWidth().weight(0.8f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                KeyButton(
                    text = "←",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Special.copy(alpha = 0.8f),
                    contentColor = KeyboardColors.Action,
                    onClick = { onMoveCursor(CursorDirection.Left) }
                )
                KeyButton(
                    text = "↑",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Special.copy(alpha = 0.8f),
                    contentColor = KeyboardColors.Action,
                    onClick = { onMoveCursor(CursorDirection.Up) }
                )
                KeyButton(
                    text = "↓",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Special.copy(alpha = 0.8f),
                    contentColor = KeyboardColors.Action,
                    onClick = { onMoveCursor(CursorDirection.Down) }
                )
                KeyButton(
                    text = "→",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Special.copy(alpha = 0.8f),
                    contentColor = KeyboardColors.Action,
                    onClick = { onMoveCursor(CursorDirection.Right) }
                )
            }
        }

        // Right Column: Actions (Weight 1.2f)
        Column(
            modifier = Modifier.weight(1.2f).padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Delete
            KeyButton(
                text = "⌫",
                modifier = Modifier.fillMaxWidth().weight(2f),
                backgroundColor = KeyboardColors.Special,
                contentColor = KeyboardColors.Text
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onDeleteClick(onDelete, onUpdateComposing)
            }

            // Space
            KeyButton(
                text = "␣",
                modifier = Modifier.fillMaxWidth().weight(2f),
                backgroundColor = KeyboardColors.Surface,
                contentColor = KeyboardColors.Text
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onSpaceClick(onCommit, onUpdateComposing)
            }

            // Enter
            KeyButton(
                text = "⏎",
                modifier = Modifier.fillMaxWidth().weight(2f),
                backgroundColor = KeyboardColors.Action,
                contentColor = Color.White
            ) {
                viewModel.commitComposing(onCommit)
                onUpdateComposing("")
            }
        }
    }
}
