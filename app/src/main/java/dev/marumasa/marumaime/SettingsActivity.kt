package dev.marumasa.marumaime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel = ViewModelProvider(this, KeyboardViewModel.Factory(application))[KeyboardViewModel::class.java]
        
        setContent {
            val scope = rememberCoroutineScope()
            
            val importLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    contentResolver.openInputStream(it)?.use { stream ->
                        scope.launch {
                            viewModel.importTsv(stream)
                        }
                    }
                }
            }
            
            val exportLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument("text/tab-separated-values")
            ) { uri ->
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { stream ->
                        scope.launch {
                            viewModel.exportTsv(stream)
                        }
                    }
                }
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "MarumaIME Settings", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(text = "Keyboard Layout")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Default Layout")
                            Text("Flick / QWERTY")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(text = "Haptic Feedback")
                        var hapticEnabled by remember { mutableStateOf(true) }
                        Switch(checked = hapticEnabled, onCheckedChange = { hapticEnabled = it })
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(text = "User Dictionary", style = MaterialTheme.typography.titleMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { importLauncher.launch("*/*") }) {
                                Text("Import TSV")
                            }
                            Button(onClick = { exportLauncher.launch("user_dictionary.txt") }) {
                                Text("Export TSV")
                            }
                        }
                        Text(
                            text = "Format: reading [tab] word [tab] category",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(text = "Conversion")
                        Text(text = "Using Google Japanese Input API + Next Word Prediction", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
