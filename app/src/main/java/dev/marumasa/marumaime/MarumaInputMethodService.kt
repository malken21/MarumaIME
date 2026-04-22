package dev.marumasa.marumaime

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.compose.ui.platform.ComposeView
import dev.marumasa.marumaime.ui.KeyboardScreen

import androidx.lifecycle.ViewModelProvider

class MarumaInputMethodService : BaseComposeInputMethodService() {

    private val viewModel by lazy {
        ViewModelProvider(this, KeyboardViewModel.Factory(application))[KeyboardViewModel::class.java]
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
                    },
                    onMoveCursor = { direction ->
                        val keyCode = when (direction) {
                            CursorDirection.Up -> android.view.KeyEvent.KEYCODE_DPAD_UP
                            CursorDirection.Down -> android.view.KeyEvent.KEYCODE_DPAD_DOWN
                            CursorDirection.Left -> android.view.KeyEvent.KEYCODE_DPAD_LEFT
                            CursorDirection.Right -> android.view.KeyEvent.KEYCODE_DPAD_RIGHT
                        }
                        currentInputConnection?.sendKeyEvent(
                            android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, keyCode)
                        )
                        currentInputConnection?.sendKeyEvent(
                            android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, keyCode)
                        )
                    },
                    onOpenSettings = {
                        val intent = Intent(this@MarumaInputMethodService, SettingsActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
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
        updateClipboardHistory()
    }

    private fun updateClipboardHistory() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()
            if (text != null) {
                viewModel.addToClipboard(text)
            }
        }
    }
}
