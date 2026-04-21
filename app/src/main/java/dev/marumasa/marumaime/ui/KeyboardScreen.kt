package dev.marumasa.marumaime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.marumasa.marumaime.*
import kotlin.math.abs

@Composable
fun KeyboardScreen(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        color = Color(0xFFD1D5DB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Candidate Bar
            CandidateBar(
                candidates = viewModel.candidates,
                selectedIndex = viewModel.selectedCandidateIndex,
                onCandidateClick = { candidate ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onCandidateClick(candidate, onCommit)
                    onUpdateComposing("")
                }
            )

            if (viewModel.layout == KeyboardLayout.Flick && viewModel.mode == KeyboardMode.Japanese) {
                FlickLayout(viewModel, onCommit, onDelete, onUpdateComposing)
            } else {
                QwertyLayout(viewModel, onCommit, onDelete, onUpdateComposing)
            }
        }
    }
}

@Composable
fun QwertyLayout(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val rows = if (viewModel.mode == KeyboardMode.English) {
        listOf(
            listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
            listOf("Z", "X", "C", "V", "B", "N", "M", "Del"),
            listOf("Layout", "Mode", "Space", "Enter")
        )
    } else {
        listOf(
            listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
            listOf("Z", "X", "C", "V", "B", "N", "M", "Del"),
            listOf("Layout", "Mode", "Space", "Enter")
        )
    }

    rows.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            row.forEach { key ->
                KeyButton(
                    text = when (key) {
                        "Mode" -> if (viewModel.mode == KeyboardMode.English) "EN" else "あ"
                        "Layout" -> if (viewModel.layout == KeyboardLayout.Flick) "12" else "QW"
                        else -> key
                    },
                    modifier = Modifier
                        .weight(
                            when (key) {
                                "Del" -> 1.5f
                                "Mode", "Layout" -> 1.2f
                                "Space", "Enter" -> 2f
                                else -> 1f
                            }
                        )
                        .height(52.dp),
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
                            else -> viewModel.onKeyClick(key, onCommit, onUpdateComposing)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FlickLayout(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val keys = listOf(
        listOf("あ", "か", "さ"),
        listOf("た", "な", "は"),
        listOf("ま", "や", "ら"),
        listOf("゛゜", "わ", "小")
    )

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(3f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            keys.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { key ->
                        FlickKeyButton(
                            text = key,
                            modifier = Modifier.weight(1f).height(60.dp),
                            onFlick = { direction ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.onFlick(key, direction, onCommit, onUpdateComposing)
                            }
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.weight(1f).padding(start = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            KeyButton("Del", Modifier.fillMaxWidth().height(60.dp), color = Color(0xFFAFB4BD)) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onDeleteClick(onDelete, onUpdateComposing)
            }
            KeyButton(if (viewModel.mode == KeyboardMode.English) "EN" else "あ", Modifier.fillMaxWidth().height(60.dp), color = Color(0xFFAFB4BD)) {
                viewModel.toggleMode()
            }
            KeyButton("QW", Modifier.fillMaxWidth().height(60.dp), color = Color(0xFFAFB4BD)) {
                viewModel.toggleLayout()
            }
            KeyButton("Enter", Modifier.fillMaxWidth().weight(1f), color = Color(0xFF3B82F6)) {
                viewModel.commitComposing(onCommit)
                onUpdateComposing("")
            }
        }
    }
}

@Composable
fun FlickKeyButton(
    text: String,
    modifier: Modifier = Modifier,
    onFlick: (FlickDirection) -> Unit
) {
    var offset by remember { mutableStateOf(IntOffset.Zero) }
    val threshold = 40f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val direction = when {
                            abs(offset.x) < threshold && abs(offset.y) < threshold -> FlickDirection.Center
                            abs(offset.x) > abs(offset.y) -> if (offset.x > 0) FlickDirection.Right else FlickDirection.Left
                            else -> if (offset.y > 0) FlickDirection.Down else FlickDirection.Up
                        }
                        onFlick(direction)
                        offset = IntOffset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset = IntOffset(
                            (offset.x + dragAmount.x).toInt(),
                            (offset.y + dragAmount.y).toInt()
                        )
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 20.sp, color = Color.Black)
    }
}

@Composable
fun CandidateBar(
    candidates: List<String>,
    selectedIndex: Int,
    onCandidateClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(Color(0xFFF3F4F6))
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        candidates.forEachIndexed { index, candidate ->
            Text(
                text = candidate,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (index == selectedIndex) Color(0xFF3B82F6).copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onCandidateClick(candidate) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 17.sp,
                color = if (index == selectedIndex) Color(0xFF3B82F6) else Color.Black
            )
        }
    }
}

@Composable
fun KeyButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = if (color == Color.White) Color.Black else Color.White
        )
    }
}
