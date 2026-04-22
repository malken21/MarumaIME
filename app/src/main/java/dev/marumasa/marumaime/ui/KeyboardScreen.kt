package dev.marumasa.marumaime.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.marumasa.marumaime.*
import dev.marumasa.marumaime.ui.theme.KeyboardColors
import kotlin.math.abs

@Composable
fun KeyboardScreen(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit,
    onMoveCursor: (CursorDirection) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = KeyboardColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                onClipClick = { viewModel.toggleClipboard() }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                if (viewModel.layout == KeyboardLayout.Flick && viewModel.mode == KeyboardMode.Japanese) {
                    FlickLayout(viewModel, onCommit, onDelete, onUpdateComposing, onMoveCursor)
                } else {
                    QwertyLayout(viewModel, onCommit, onDelete, onUpdateComposing, onMoveCursor)
                }
            }
        }
    }
}

@Composable
fun NumberRow(onCommit: (String) -> Unit, onClipClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        KeyButton(
            text = "📋",
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            backgroundColor = KeyboardColors.Special,
            contentColor = KeyboardColors.Text,
            onClick = onClipClick
        )
        (1..9).map { it.toString() }.plus("0").forEach { num ->
            KeyButton(
                text = num,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                backgroundColor = KeyboardColors.Surface,
                contentColor = KeyboardColors.Text,
                onClick = { onCommit(num) }
            )
        }
    }
}

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
                            "Mode" -> if (viewModel.mode == KeyboardMode.English) "EN" else "あ"
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

@Composable
fun FlickLayout(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit,
    onMoveCursor: (CursorDirection) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val keys = listOf(
        listOf("あ", "か", "さ"),
        listOf("た", "な", "は"),
        listOf("ま", "や", "ら"),
        listOf("゛゜", "わ", "小")
    )

    val mapping = FlickMapping.mapping

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Column: Configuration (Weight 0.8f)
        Column(
            modifier = Modifier.weight(0.8f).padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(modifier = Modifier.weight(1.5f))
            KeyButton(
                text = "QW",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special,
                contentColor = KeyboardColors.Text
            ) {
                viewModel.toggleLayout()
            }
            KeyButton(
                text = if (viewModel.mode == KeyboardMode.English) "EN" else "あ",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special,
                contentColor = KeyboardColors.Text
            ) {
                viewModel.toggleMode()
            }
        }

        // Center Column: Flick Grid (Weight 3f)
        Column(
            modifier = Modifier.weight(3f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
        }

        // Right Column: Navigation and Action (Weight 2.0f)
        Column(
            modifier = Modifier.weight(2.0f).padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: Delete (Primary Action)
            KeyButton(
                text = "⌫",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special,
                contentColor = KeyboardColors.Text
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onDeleteClick(onDelete, onUpdateComposing)
            }

            // Row 2: Up Navigation
            KeyButton(
                text = "↑",
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = KeyboardColors.Special,
                onClick = { onMoveCursor(CursorDirection.Up) }
            )

            // Row 3: Horizontal & Down Navigation
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                KeyButton(
                    text = "←",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Special,
                    onClick = { onMoveCursor(CursorDirection.Left) }
                )
                KeyButton(
                    text = "↓",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Special,
                    onClick = { onMoveCursor(CursorDirection.Down) }
                )
                KeyButton(
                    text = "→",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Special,
                    onClick = { onMoveCursor(CursorDirection.Right) }
                )
            }

            // Row 4: Space & Enter (Most used)
            Row(
                modifier = Modifier.fillMaxWidth().weight(1.2f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                KeyButton(
                    text = "␣",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Surface,
                    contentColor = KeyboardColors.Text
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.onSpaceClick(onCommit, onUpdateComposing)
                }
                KeyButton(
                    text = "⏎",
                    modifier = Modifier.weight(1.2f).fillMaxHeight(),
                    backgroundColor = KeyboardColors.Action,
                    contentColor = Color.White
                ) {
                    viewModel.commitComposing(onCommit)
                    onUpdateComposing("")
                }
            }
        }
    }
}

@Composable
fun FlickKeyButton(
    chars: List<String>,
    modifier: Modifier = Modifier,
    onFlick: (FlickDirection) -> Unit
) {
    var offset by remember { mutableStateOf(IntOffset.Zero) }
    val threshold = 40f
    
    val currentDirection = when {
        abs(offset.x) < threshold && abs(offset.y) < threshold -> FlickDirection.Center
        abs(offset.x) > abs(offset.y) -> if (offset.x > 0) FlickDirection.Right else FlickDirection.Left
        else -> if (offset.y > 0) FlickDirection.Down else FlickDirection.Up
    }

    val isDragging = offset != IntOffset.Zero

    Box(
        modifier = modifier
            .padding(2.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        if (isDragging) KeyboardColors.Special else KeyboardColors.Surface,
                        if (isDragging) KeyboardColors.Special.copy(alpha = 0.9f) else Color.White
                    )
                )
            )
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
                    },
                    onDragCancel = {
                        offset = IntOffset.Zero
                    }
                )
            }
            .clickable(enabled = !isDragging) { onFlick(FlickDirection.Center) },
        contentAlignment = Alignment.Center
    ) {
        // Center text
        Text(
            text = chars[0],
            fontSize = if (isDragging && currentDirection == FlickDirection.Center) 26.sp else 22.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDragging && currentDirection == FlickDirection.Center) KeyboardColors.Action else KeyboardColors.Text,
            modifier = Modifier.align(Alignment.Center)
        )

        if (isDragging) {
            // Background hint for direction
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(KeyboardColors.Action.copy(alpha = 0.1f), Color.Transparent),
                            center = when (currentDirection) {
                                FlickDirection.Center -> androidx.compose.ui.geometry.Offset.Unspecified
                                FlickDirection.Left -> androidx.compose.ui.geometry.Offset(0f, 0.5f)
                                FlickDirection.Up -> androidx.compose.ui.geometry.Offset(0.5f, 0f)
                                FlickDirection.Right -> androidx.compose.ui.geometry.Offset(1f, 0.5f)
                                FlickDirection.Down -> androidx.compose.ui.geometry.Offset(0.5f, 1f)
                            }
                        )
                    )
            )

            // Left
            if (chars.size > 1 && chars[1].isNotEmpty()) {
                Text(
                    text = chars[1],
                    fontSize = if (currentDirection == FlickDirection.Left) 24.sp else 16.sp,
                    fontWeight = if (currentDirection == FlickDirection.Left) FontWeight.Bold else FontWeight.Normal,
                    color = if (currentDirection == FlickDirection.Left) KeyboardColors.Action else Color.Gray,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                )
            }
            // Up
            if (chars.size > 2 && chars[2].isNotEmpty()) {
                Text(
                    text = chars[2],
                    fontSize = if (currentDirection == FlickDirection.Up) 24.sp else 16.sp,
                    fontWeight = if (currentDirection == FlickDirection.Up) FontWeight.Bold else FontWeight.Normal,
                    color = if (currentDirection == FlickDirection.Up) KeyboardColors.Action else Color.Gray,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
                )
            }
            // Right
            if (chars.size > 3 && chars[3].isNotEmpty()) {
                Text(
                    text = chars[3],
                    fontSize = if (currentDirection == FlickDirection.Right) 24.sp else 16.sp,
                    fontWeight = if (currentDirection == FlickDirection.Right) FontWeight.Bold else FontWeight.Normal,
                    color = if (currentDirection == FlickDirection.Right) KeyboardColors.Action else Color.Gray,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                )
            }
            // Down
            if (chars.size > 4 && chars[4].isNotEmpty()) {
                Text(
                    text = chars[4],
                    fontSize = if (currentDirection == FlickDirection.Down) 24.sp else 16.sp,
                    fontWeight = if (currentDirection == FlickDirection.Down) FontWeight.Bold else FontWeight.Normal,
                    color = if (currentDirection == FlickDirection.Down) KeyboardColors.Action else Color.Gray,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ClipboardBar(
    history: List<String>,
    onItemClick: (String) -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(KeyboardColors.Surface)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        KeyButton(
            text = "×",
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            backgroundColor = KeyboardColors.Special,
            contentColor = KeyboardColors.Text,
            onClick = onClose
        )
        if (history.isEmpty()) {
            Text(
                text = "No history",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                color = Color.Gray
            )
        } else {
            history.forEach { item ->
                Text(
                    text = item.replace("\n", " "),
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(KeyboardColors.Action.copy(alpha = 0.1f))
                        .clickable { onItemClick(item) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 18.sp,
                    maxLines = 1,
                    color = KeyboardColors.Text
                )
            }
        }
    }
}

@Composable
fun CandidateBar(
    candidates: List<String>,
    selectedIndex: Int,
    onCandidateClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    LaunchedEffect(candidates) {
        scrollState.scrollTo(0)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(KeyboardColors.Surface)
            .horizontalScroll(scrollState)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        candidates.forEachIndexed { index, candidate ->
            val isSelected = index == selectedIndex
            val backgroundColor by animateColorAsState(if (isSelected) KeyboardColors.Action.copy(alpha = 0.1f) else Color.Transparent)
            val textColor by animateColorAsState(if (isSelected) KeyboardColors.Action else KeyboardColors.Text)

            Text(
                text = candidate,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(backgroundColor)
                    .clickable { onCandidateClick(candidate) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 20.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
fun KeyButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = KeyboardColors.Surface,
    contentColor: Color = KeyboardColors.Text,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    Box(
        modifier = modifier
            .padding(2.dp)
            .scale(scale)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.9f)
                    )
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isPressed = true },
                    onDragEnd = {
                        isPressed = false
                        onClick()
                    },
                    onDragCancel = { isPressed = false },
                    onDrag = { change, _ -> change.consume() }
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}
