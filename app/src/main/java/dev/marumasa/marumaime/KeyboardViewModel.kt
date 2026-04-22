package dev.marumasa.marumaime

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.marumasa.marumaime.data.AppDatabase
import dev.marumasa.marumaime.data.DictionaryDao
import dev.marumasa.marumaime.data.DictionaryEntity
import dev.marumasa.marumaime.data.PredictionDao
import dev.marumasa.marumaime.data.PredictionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class KeyboardViewModel(
    private val dictionaryDao: DictionaryDao,
    private val predictionDao: PredictionDao
) : ViewModel() {
    private var lastCommittedWord: String? = null
    var mode by mutableStateOf(KeyboardMode.Japanese)
    var layout by mutableStateOf(KeyboardLayout.Flick)
    
    var composingText by mutableStateOf("") // Romaji or Kana being entered
    var kanaText by mutableStateOf("")      // Converted kana part
    var candidates by mutableStateOf(listOf<String>())
    var selectedCandidateIndex by mutableStateOf(-1)
    
    var clipboardHistory by mutableStateOf(listOf<String>())
    var isClipboardVisible by mutableStateOf(false)
    var isShifted by mutableStateOf(false)

    private var conversionJob: Job? = null

    fun onKeyClick(key: String, commit: (String) -> Unit, setComposing: (String) -> Unit) {
        if (mode == KeyboardMode.English || mode == KeyboardMode.Symbol) {
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
            if (mode == KeyboardMode.Symbol) {
                commit(char)
            } else {
                if (char == "゛" || char == "゜" || char == "小") {
                    kanaText = KanaModifier.modify(kanaText, char)
                } else {
                    kanaText += char
                }
                updateCandidates()
                setComposing(kanaText + composingText)
            }
        }
    }

    private fun getFlickChar(baseKey: String, direction: FlickDirection): String? {
        return FlickMapping.getFlickChar(baseKey, direction, isShifted)
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
            val lastWord = lastCommittedWord
            if (lastWord != null) {
                viewModelScope.launch {
                    val predictions = predictionDao.getPredictions(lastWord)
                    candidates = predictions.map { it.nextWord }
                    selectedCandidateIndex = -1
                }
            } else {
                candidates = emptyList()
                selectedCandidateIndex = -1
            }
            return
        }

        conversionJob?.cancel()
        conversionJob = viewModelScope.launch {
            val apiCandidates = ConversionEngine.convert(fullText)
            val dictionaryEntries = dictionaryDao.findByReading(fullText)
            val dictionaryCandidates = dictionaryEntries.map { it.word }
            
            candidates = (dictionaryCandidates + apiCandidates).distinct()
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
            val prevWord = lastCommittedWord
            if (prevWord != null) {
                viewModelScope.launch {
                    predictionDao.learn(prevWord, textToCommit)
                }
            }
            lastCommittedWord = textToCommit

            commit(textToCommit)
            kanaText = ""
            composingText = ""
            updateCandidates()
        }
    }

    fun onCandidateClick(candidate: String, commit: (String) -> Unit) {
        val textToCommit = candidate
        val prevWord = lastCommittedWord
        if (prevWord != null) {
            viewModelScope.launch {
                predictionDao.learn(prevWord, textToCommit)
            }
        }
        lastCommittedWord = textToCommit

        commit(textToCommit)
        kanaText = ""
        composingText = ""
        updateCandidates()
    }

    fun toggleMode() {
        mode = when (mode) {
            KeyboardMode.Japanese -> KeyboardMode.English
            KeyboardMode.English -> KeyboardMode.Symbol
            KeyboardMode.Symbol -> KeyboardMode.Japanese
        }
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
        isShifted = false
    }

    fun toggleShift() {
        isShifted = !isShifted
    }

    fun toggleClipboard() {
        isClipboardVisible = !isClipboardVisible
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

    suspend fun importTsv(inputStream: InputStream) = withContext(Dispatchers.IO) {
        val reader = inputStream.bufferedReader()
        val entries = mutableListOf<DictionaryEntity>()
        reader.forEachLine { line ->
            val parts = line.split("\t")
            if (parts.size >= 2) {
                entries.add(
                    DictionaryEntity(
                        reading = parts[0],
                        word = parts[1],
                        category = parts.getOrElse(2) { "名詞" }
                    )
                )
            }
        }
        if (entries.isNotEmpty()) {
            dictionaryDao.insertAll(entries)
        }
    }

    suspend fun exportTsv(outputStream: OutputStream) = withContext(Dispatchers.IO) {
        val writer = outputStream.bufferedWriter()
        val entries = dictionaryDao.getAll()
        entries.forEach { entry ->
            writer.write("${entry.reading}\t${entry.word}\t${entry.category}\n")
        }
        writer.flush()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(KeyboardViewModel::class.java)) {
                val db = AppDatabase.getDatabase(application)
                @Suppress("UNCHECKED_CAST")
                return KeyboardViewModel(db.dictionaryDao(), db.predictionDao()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
