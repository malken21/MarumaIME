package dev.marumasa.marumaime.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.marumasa.marumaime.ui.theme.KeyboardColors

@Composable
fun KeyButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = KeyboardColors.Surface,
    contentColor: Color = KeyboardColors.Text,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f)

    Box(
        modifier = modifier
            .padding(1.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 1.dp else 3.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.92f)
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
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}
