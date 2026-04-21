package dev.marumasa.marumaime

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class KeyboardMode {
    English, Japanese
}

class KeyboardViewModel : ViewModel() {
    var mode by mutableStateOf(KeyboardMode.Japanese)
    var composingText by mutableStateOf("") // Romaji input
    var kanaText by mutableStateOf("")      // Converted kana
    var candidates by mutableStateOf(listOf<String>())

    fun onKeyClick(key: String, commit: (String) -> Unit, setComposing: (String) -> Unit) {
        if (mode == KeyboardMode.English) {
            commit(key)
            return
        }

        // Japanese mode (Romaji)
        if (key.length == 1 && key[0].isLetter()) {
            composingText += key.lowercase()
            updateConversion(setComposing)
        } else {
            // Non-letter keys in Japanese mode
            commitComposing(commit)
            commit(key)
        }
    }

    fun onDeleteClick(delete: () -> Unit, setComposing: (String) -> Unit) {
        if (composingText.isNotEmpty()) {
            composingText = composingText.dropLast(1)
            updateConversion(setComposing)
        } else if (kanaText.isNotEmpty()) {
            kanaText = kanaText.dropLast(1)
            setComposing(kanaText)
        } else {
            delete()
        }
    }

    private fun updateConversion(setComposing: (String) -> Unit) {
        val (converted, remaining) = RomajiConverter.convert(composingText)
        if (converted.isNotEmpty()) {
            kanaText += converted
            composingText = remaining
        }
        setComposing(kanaText + composingText)
        
        // Mock candidates
        if (kanaText.isNotEmpty()) {
            candidates = listOf(kanaText, "漢字", "変換", "テスト")
        } else {
            candidates = emptyList()
        }
    }

    fun commitComposing(commit: (String) -> Unit) {
        if (kanaText.isNotEmpty() || composingText.isNotEmpty()) {
            commit(kanaText + composingText)
            kanaText = ""
            composingText = ""
            candidates = emptyList()
        }
    }

    fun onCandidateClick(candidate: String, commit: (String) -> Unit) {
        commit(candidate)
        kanaText = ""
        composingText = ""
        candidates = emptyList()
    }

    fun toggleMode() {
        mode = if (mode == KeyboardMode.English) KeyboardMode.Japanese else KeyboardMode.English
        kanaText = ""
        composingText = ""
        candidates = emptyList()
    }
}
