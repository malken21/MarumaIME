package dev.marumasa.marumaime.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import dev.marumasa.marumaime.KeyboardViewModel
import dev.marumasa.marumaime.data.DictionaryDao
import dev.marumasa.marumaime.data.DictionaryEntity
import dev.marumasa.marumaime.data.PredictionDao
import dev.marumasa.marumaime.data.PredictionEntity
import org.junit.Rule
import org.junit.Test

class ScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(screenHeight = 900, softButtons = false),
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun captureKeyboard() {
        val mockDictionaryDao = object : DictionaryDao {
            override suspend fun findByReading(reading: String) = emptyList<DictionaryEntity>()
            override suspend fun getAll() = emptyList<DictionaryEntity>()
            override suspend fun insert(entry: DictionaryEntity) {}
            override suspend fun insertAll(entries: List<DictionaryEntity>) {}
            override suspend fun delete(entry: DictionaryEntity) {}
            override suspend fun deleteAll() {}
        }
        val mockPredictionDao = object : PredictionDao {
            override suspend fun getPredictions(prevWord: String) = emptyList<PredictionEntity>()
            override suspend fun insert(prediction: PredictionEntity) = 0L
            override suspend fun incrementCount(prevWord: String, nextWord: String) {}
            override suspend fun learn(prevWord: String, nextWord: String) {}
        }

        val viewModel = KeyboardViewModel(mockDictionaryDao, mockPredictionDao).apply {
            candidates = listOf("MarumaIME")
            selectedCandidateIndex = 0
        }
        paparazzi.snapshot {
            KeyboardScreen(
                viewModel = viewModel,
                onCommit = {},
                onDelete = {},
                onUpdateComposing = {},
                onMoveCursor = {},
                onOpenSettings = {}
            )
        }
    }
}
