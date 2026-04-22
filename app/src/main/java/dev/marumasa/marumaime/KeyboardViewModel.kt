package dev.marumasa.marumaime

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

enum class KeyboardMode {
    English, Japanese
}

enum class KeyboardLayout {
    Qwerty, Flick
}

enum class FlickDirection {
    Center, Up, Down, Left, Right
}

enum class CursorDirection {
    Up, Down, Left, Right
}

class KeyboardViewModel : ViewModel() {
    var mode by mutableStateOf(KeyboardMode.Japanese)
    var layout by mutableStateOf(KeyboardLayout.Flick)
    
    var composingText by mutableStateOf("") // Romaji or Kana being entered
    var kanaText by mutableStateOf("")      // Converted kana part
    var candidates by mutableStateOf(listOf<String>())
    var selectedCandidateIndex by mutableStateOf(-1)
    
    var clipboardHistory by mutableStateOf(listOf<String>())
    var isClipboardVisible by mutableStateOf(false)

    fun onKeyClick(key: String, commit: (String) -> Unit, setComposing: (String) -> Unit) {
        if (mode == KeyboardMode.English) {
            commit(key)
            return
        }

        // Japanese mode
        if (key.length == 1 && key[0].isLetter()) {
            composingText += key.lowercase()
            updateRomajiConversion(setComposing)
        } else {
            commitComposing(commit)
            commit(key)
        }
    }

    fun onFlick(baseKey: String, direction: FlickDirection, commit: (String) -> Unit, setComposing: (String) -> Unit) {
        if (mode == KeyboardMode.English) {
            // English doesn't usually use flick, but we could implement it
            return
        }

        val char = getFlickChar(baseKey, direction)
        if (char != null) {
            kanaText += char
            updateCandidates()
            setComposing(kanaText + composingText)
        }
    }

    private fun getFlickChar(baseKey: String, direction: FlickDirection): String? {
        return FlickMapping.getFlickChar(baseKey, direction)
    }

    fun onDeleteClick(delete: () -> Unit, setComposing: (String) -> Unit) {
        if (composingText.isNotEmpty()) {
            composingText = composingText.dropLast(1)
            updateRomajiConversion(setComposing)
        } else if (kanaText.isNotEmpty()) {
            kanaText = kanaText.dropLast(1)
            updateCandidates()
            setComposing(kanaText)
        } else {
            delete()
        }
    }

    private fun updateRomajiConversion(setComposing: (String) -> Unit) {
        val (converted, remaining) = RomajiConverter.convert(composingText)
        if (converted.isNotEmpty()) {
            kanaText += converted
            composingText = remaining
        }
        updateCandidates()
        setComposing(kanaText + composingText)
    }

    private fun updateCandidates() {
        val fullText = kanaText + composingText
        if (fullText.isEmpty()) {
            candidates = emptyList()
            selectedCandidateIndex = -1
            return
        }

        viewModelScope.launch {
            candidates = ConversionEngine.convert(fullText)
            selectedCandidateIndex = -1
        }
    }

    fun onSpaceClick(commit: (String) -> Unit, setComposing: (String) -> Unit) {
        if (candidates.isNotEmpty()) {
            selectedCandidateIndex = (selectedCandidateIndex + 1) % candidates.size
            setComposing(candidates[selectedCandidateIndex])
        } else {
            commit(" ")
        }
    }

    fun commitComposing(commit: (String) -> Unit) {
        val textToCommit = if (selectedCandidateIndex != -1) {
            candidates[selectedCandidateIndex]
        } else {
            kanaText + composingText
        }

        if (textToCommit.isNotEmpty()) {
            commit(textToCommit)
            kanaText = ""
            composingText = ""
            candidates = emptyList()
            selectedCandidateIndex = -1
        }
    }

    fun onCandidateClick(candidate: String, commit: (String) -> Unit) {
        commit(candidate)
        kanaText = ""
        composingText = ""
        candidates = emptyList()
        selectedCandidateIndex = -1
    }

    fun toggleMode() {
        mode = if (mode == KeyboardMode.English) KeyboardMode.Japanese else KeyboardMode.English
        resetState()
    }

    fun toggleLayout() {
        layout = if (layout == KeyboardLayout.Qwerty) KeyboardLayout.Flick else KeyboardLayout.Qwerty
        resetState()
    }

    private fun resetState() {
        kanaText = ""
        composingText = ""
        candidates = emptyList()
        selectedCandidateIndex = -1
        isClipboardVisible = false
    }

    fun toggleClipboard() {
        isClipboardVisible = !isClipboardVisible
        if (isClipboardVisible) {
            // In a real app, we might fetch from ClipboardManager here
        }
    }

    fun onClipboardItemClick(item: String, commit: (String) -> Unit) {
        commit(item)
        isClipboardVisible = false
    }

    fun addToClipboard(text: String) {
        if (text.isBlank()) return
        val currentHistory = clipboardHistory.toMutableList()
        currentHistory.remove(text) // Remove if already exists to move to top
        currentHistory.add(0, text)
        if (currentHistory.size > 255) {
            clipboardHistory = currentHistory.take(255)
        } else {
            clipboardHistory = currentHistory
        }
    }
}
