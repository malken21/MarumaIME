package dev.marumasa.marumaime

import android.view.View
import androidx.compose.ui.platform.ComposeView
import dev.marumasa.marumaime.ui.KeyboardScreen

import androidx.lifecycle.ViewModelProvider

class MarumaInputMethodService : BaseComposeInputMethodService() {

    private val viewModel by lazy {
        ViewModelProvider(this)[KeyboardViewModel::class.java]
    }

    override fun createComposeInputView(composeView: ComposeView): View {
        return composeView.apply {
            setContent {
                KeyboardScreen(
                    viewModel = viewModel,
                    onCommit = { text ->
                        currentInputConnection?.commitText(text, 1)
                    },
                    onDelete = {
                        currentInputConnection?.deleteSurroundingText(1, 0)
                    },
                    onUpdateComposing = { text ->
                        currentInputConnection?.setComposingText(text, 1)
                    }
                )
            }
        }
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        // Reset state on new input field
        viewModel.commitComposing { text ->
            currentInputConnection?.commitText(text, 1)
        }
    }
}
