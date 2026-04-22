package dev.marumasa.marumaime.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import dev.marumasa.marumaime.CursorDirection
import dev.marumasa.marumaime.KeyboardLayout
import dev.marumasa.marumaime.KeyboardMode
import dev.marumasa.marumaime.KeyboardViewModel
import dev.marumasa.marumaime.ui.components.KeyButton
import dev.marumasa.marumaime.ui.theme.KeyboardColors

@Composable
fun QwertyLayout(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit,
    onMoveCursor: (CursorDirection) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val rows = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
        listOf("Z", "X", "C", "V", "B", "N", "M", "Del"),
        listOf("Layout", "Mode", "←", "↓", "↑", "→", "Space", "Enter")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        rows.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { key ->
                    KeyButton(
                        text = when (key) {
                            "Mode" -> when (viewModel.mode) {
                                KeyboardMode.English -> "EN"
                                KeyboardMode.Japanese -> "あ"
                                KeyboardMode.Symbol -> "SYM"
                            }
                            "Layout" -> if (viewModel.layout == KeyboardLayout.Flick) "12" else "QW"
                            "Del" -> "⌫"
                            "Space" -> "␣"
                            "Enter" -> "⏎"
                            else -> key
                        },
                        modifier = Modifier
                            .weight(
                                when (key) {
                                    "Del" -> 1.5f
                                    "Mode", "Layout" -> 1.2f
                                    "Space", "Enter" -> if (rowIndex == 3) 2.5f else 2f
                                    "←", "↓", "↑", "→" -> 0.8f
                                    else -> 1f
                                }
                            )
                            .fillMaxHeight(),
                        backgroundColor = when (key) {
                            "Enter" -> KeyboardColors.Action
                            "Del", "Mode", "Layout" -> KeyboardColors.Special
                            "←", "↓", "↑", "→" -> KeyboardColors.Special
                            else -> KeyboardColors.Surface
                        },
                        contentColor = when (key) {
                            "Enter" -> Color.White
                            else -> KeyboardColors.Text
                        },
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            when (key) {
                                "Del" -> viewModel.onDeleteClick(onDelete, onUpdateComposing)
                                "Mode" -> viewModel.toggleMode()
                                "Layout" -> viewModel.toggleLayout()
                                "Space" -> viewModel.onSpaceClick(onCommit, onUpdateComposing)
                                "Enter" -> {
                                    viewModel.commitComposing(onCommit)
                                    onUpdateComposing("")
                                }
                                "←" -> onMoveCursor(CursorDirection.Left)
                                "↓" -> onMoveCursor(CursorDirection.Down)
                                "↑" -> onMoveCursor(CursorDirection.Up)
                                "→" -> onMoveCursor(CursorDirection.Right)
                                else -> viewModel.onKeyClick(key, onCommit, onUpdateComposing)
                            }
                        }
                    )
                }
            }
        }
    }
}
