package dev.marumasa.marumaime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.marumasa.marumaime.FlickDirection
import dev.marumasa.marumaime.ui.theme.KeyboardColors
import kotlin.math.abs

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
            .padding(1.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
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
                                FlickDirection.Center -> Offset.Unspecified
                                FlickDirection.Left -> Offset(0f, 0.5f)
                                FlickDirection.Up -> Offset(0.5f, 0f)
                                FlickDirection.Right -> Offset(1f, 0.5f)
                                FlickDirection.Down -> Offset(0.5f, 1f)
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
