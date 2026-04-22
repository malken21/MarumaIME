package dev.marumasa.marumaime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.marumasa.marumaime.ui.theme.KeyboardColors

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
