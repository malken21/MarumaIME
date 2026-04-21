package dev.marumasa.marumaime

import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

/**
 * A base class for [InputMethodService] that supports Jetpack Compose.
 */
abstract class BaseComposeInputMethodService : InputMethodService(),
    LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }
    private val store by lazy { ViewModelStore() }
    private val savedStateRegistryController by lazy { SavedStateRegistryController.create(this) }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onWindowShown() {
        super.onWindowShown()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }

    /**
     * Create the Compose view for the input method.
     */
    abstract fun createComposeInputView(composeView: ComposeView): View

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this).apply {
            // Set required providers for Compose BEFORE setContent is called
            setViewTreeLifecycleOwner(this@BaseComposeInputMethodService)
            setViewTreeViewModelStoreOwner(this@BaseComposeInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@BaseComposeInputMethodService)
            
            // Set composition strategy to dispose when detached from window
            setViewCompositionStrategy(
                androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnDetachedFromWindow
            )
        }
        
        // Also set owners on the window's decor view to ensure all child views can find them
        window?.window?.decorView?.let { decorView ->
            if (androidx.lifecycle.ViewTreeLifecycleOwner.get(decorView) == null) {
                androidx.lifecycle.ViewTreeLifecycleOwner.set(decorView, this)
            }
            if (androidx.lifecycle.ViewTreeViewModelStoreOwner.get(decorView) == null) {
                androidx.lifecycle.ViewTreeViewModelStoreOwner.set(decorView, this)
            }
            if (androidx.savedstate.ViewTreeSavedStateRegistryOwner.get(decorView) == null) {
                androidx.savedstate.ViewTreeSavedStateRegistryOwner.set(decorView, this)
            }
        }
        
        return createComposeInputView(composeView)
    }
}
