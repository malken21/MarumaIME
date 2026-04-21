package dev.marumasa.marumaime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "MarumaIME Settings", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(text = "Keyboard Layout")
                        // Simplified settings UI
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Default Layout")
                            Text("Flick / QWERTY")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(text = "Haptic Feedback")
                        var hapticEnabled by remember { mutableStateOf(true) }
                        Switch(checked = hapticEnabled, onCheckedChange = { hapticEnabled = it })
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(text = "Conversion")
                        Text(text = "Using Google Japanese Input API", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
