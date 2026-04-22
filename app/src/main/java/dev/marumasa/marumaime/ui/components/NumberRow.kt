package dev.marumasa.marumaime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.marumasa.marumaime.ui.theme.KeyboardColors

@Composable
fun NumberRow(onCommit: (String) -> Unit, onClipClick: () -> Unit, onSettingsClick: () -> Unit) {
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
        KeyButton(
            text = "⚙️",
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            backgroundColor = KeyboardColors.Special,
            contentColor = KeyboardColors.Text,
            onClick = onSettingsClick
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
