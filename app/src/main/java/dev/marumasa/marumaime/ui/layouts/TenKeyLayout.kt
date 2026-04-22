package dev.marumasa.marumaime.ui.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
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
        // Left Column: Configuration & Left Navigation (Weight 1.0f)
        Column(
            modifier = Modifier.weight(1.0f).padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 0-1: Shift (Large corner button)
            KeyButton(
                text = if (viewModel.isShifted) "⬆️" else "⇧",
                modifier = Modifier.fillMaxWidth().weight(2f),
                backgroundColor = if (viewModel.isShifted) KeyboardColors.Action else KeyboardColors.Special,
                contentColor = if (viewModel.isShifted) Color.White else KeyboardColors.Text
            ) {
                viewModel.toggleShift()
            }
            // Row 2: Left Navigation (Aligned with "た")
            KeyButton(
                text = "←",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special.copy(alpha = 0.8f),
                contentColor = KeyboardColors.Action,
                onClick = { onMoveCursor(CursorDirection.Left) }
            )
            // Row 3: Layout Toggle
            KeyButton(
                text = "QW",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special,
                contentColor = KeyboardColors.Text
            ) {
                viewModel.toggleLayout()
            }
            // Row 4-5: Mode Toggle (Large corner button)
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
        }

        // Center Column: Flick Grid & Vertical Navigation (Weight 3.2f)
        Column(
            modifier = Modifier.weight(3.2f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 0: Up Navigation (Wide)
            KeyButton(
                text = "↑",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special.copy(alpha = 0.8f),
                contentColor = KeyboardColors.Action,
                onClick = { onMoveCursor(CursorDirection.Up) }
            )

            // Rows 1-4: Flick Grid
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
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

            // Row 5: Down Navigation (Wide)
            KeyButton(
                text = "↓",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special.copy(alpha = 0.8f),
                contentColor = KeyboardColors.Action,
                onClick = { onMoveCursor(CursorDirection.Down) }
            )
        }

        // Right Column: Delete, Right Navigation, Space & Enter (Weight 1.8f)
        Column(
            modifier = Modifier.weight(1.8f).padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 0-1: Delete (Large corner button)
            KeyButton(
                text = "⌫",
                modifier = Modifier.fillMaxWidth().weight(2f),
                backgroundColor = KeyboardColors.Special,
                contentColor = KeyboardColors.Text
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onDeleteClick(onDelete, onUpdateComposing)
            }

            // Row 2: Right Navigation (Aligned with "は")
            KeyButton(
                text = "→",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special.copy(alpha = 0.8f),
                contentColor = KeyboardColors.Action,
                onClick = { onMoveCursor(CursorDirection.Right) }
            )

            // Row 3: Space
            KeyButton(
                text = "␣",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Surface,
                contentColor = KeyboardColors.Text
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onSpaceClick(onCommit, onUpdateComposing)
            }

            // Row 4-5: Enter (Large corner button)
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
