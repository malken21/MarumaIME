package dev.marumasa.marumaime

import android.view.View
import androidx.compose.ui.platform.ComposeView
import dev.marumasa.marumaime.ui.KeyboardScreen

class MarumaInputMethodService : BaseComposeInputMethodService() {

    override fun createComposeInputView(): View {
        return ComposeView(this).apply {
            setContent {
                KeyboardScreen(
                    onKeyClick = { text ->
                        currentInputConnection.commitText(text, 1)
                    },
                    onDeleteClick = {
                        currentInputConnection.deleteSurroundingText(1, 0)
                    }
                )
            }
        }
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        // Handle input type changes if needed
    }
}
