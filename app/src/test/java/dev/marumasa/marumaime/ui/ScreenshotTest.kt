package dev.marumasa.marumaime.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import dev.marumasa.marumaime.KeyboardViewModel
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
        val viewModel = KeyboardViewModel().apply {
            candidates = listOf("MarumaIME")
            selectedCandidateIndex = 0
        }
        paparazzi.snapshot {
            KeyboardScreen(
                viewModel = viewModel,
                onCommit = {},
                onDelete = {},
                onUpdateComposing = {},
                onMoveCursor = {}
            )
        }
    }
}
