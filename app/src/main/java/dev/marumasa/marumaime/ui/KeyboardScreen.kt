package dev.marumasa.marumaime.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import dev.marumasa.marumaime.*
import dev.marumasa.marumaime.ui.components.*
import dev.marumasa.marumaime.ui.layouts.*
import dev.marumasa.marumaime.ui.theme.KeyboardColors

@Composable
fun KeyboardScreen(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit,
    onMoveCursor: (CursorDirection) -> Unit,
    onOpenSettings: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = KeyboardColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Candidate or Clipboard Bar
            if (viewModel.isClipboardVisible) {
                ClipboardBar(
                    history = viewModel.clipboardHistory,
                    onItemClick = { item ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onClipboardItemClick(item, onCommit)
                    },
                    onClose = { viewModel.toggleClipboard() }
                )
            } else {
                CandidateBar(
                    candidates = viewModel.candidates,
                    selectedIndex = viewModel.selectedCandidateIndex,
                    onCandidateClick = { candidate ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onCandidateClick(candidate, onCommit)
                        onUpdateComposing("")
                    }
                )
            }

            // Number Row
            NumberRow(
                onCommit = { viewModel.onKeyClick(it, onCommit, onUpdateComposing) },
                onClipClick = { viewModel.toggleClipboard() },
                onSettingsClick = onOpenSettings
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .navigationBarsPadding()
            ) {
                if (viewModel.layout == KeyboardLayout.Flick) {
                    TenKeyLayout(viewModel, onCommit, onDelete, onUpdateComposing, onMoveCursor)
                } else {
                    QwertyLayout(viewModel, onCommit, onDelete, onUpdateComposing, onMoveCursor)
                }
            }
        }
    }
}
