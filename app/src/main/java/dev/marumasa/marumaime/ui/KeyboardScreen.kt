package dev.marumasa.marumaime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.marumasa.marumaime.KeyboardMode
import dev.marumasa.marumaime.KeyboardViewModel

@Composable
fun KeyboardScreen(
    viewModel: KeyboardViewModel,
    onCommit: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdateComposing: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        color = Color(0xFFEEEEEE)
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
                onCandidateClick = { candidate ->
                    viewModel.onCandidateClick(candidate, onCommit)
                    onUpdateComposing("")
                }
            )

            val rows = if (viewModel.mode == KeyboardMode.English) {
                listOf(
                    listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
                    listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
                    listOf("Z", "X", "C", "V", "B", "N", "M", "Del"),
                    listOf("Mode", "Space", "Enter")
                )
            } else {
                // Japanese Romaji (QWERTY)
                listOf(
                    listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
                    listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
                    listOf("Z", "X", "C", "V", "B", "N", "M", "Del"),
                    listOf("Mode", "Space", "Enter")
                )
            }

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    row.forEach { key ->
                        KeyButton(
                            text = if (key == "Mode") (if (viewModel.mode == KeyboardMode.English) "EN" else "あ") else key,
                            modifier = Modifier
                                .weight(
                                    when (key) {
                                        "Del", "Mode" -> 1.5f
                                        "Space", "Enter" -> 2f
                                        else -> 1f
                                    }
                                )
                                .height(52.dp),
                            onClick = {
                                when (key) {
                                    "Del" -> viewModel.onDeleteClick(onDelete, onUpdateComposing)
                                    "Mode" -> viewModel.toggleMode()
                                    "Space" -> {
                                        if (viewModel.composingText.isNotEmpty() || viewModel.kanaText.isNotEmpty()) {
                                            // Conversion logic could go here
                                        } else {
                                            onCommit(" ")
                                        }
                                    }
                                    "Enter" -> {
                                        if (viewModel.composingText.isNotEmpty() || viewModel.kanaText.isNotEmpty()) {
                                            viewModel.commitComposing(onCommit)
                                            onUpdateComposing("")
                                        } else {
                                            onCommit("\n")
                                        }
                                    }
                                    else -> viewModel.onKeyClick(key, onCommit, onUpdateComposing)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CandidateBar(
    candidates: List<String>,
    onCandidateClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        candidates.forEach { candidate ->
            Text(
                text = candidate,
                modifier = Modifier
                    .clickable { onCandidateClick(candidate) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun KeyButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}
