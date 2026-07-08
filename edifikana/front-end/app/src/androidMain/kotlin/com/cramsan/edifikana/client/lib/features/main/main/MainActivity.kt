package com.cramsan.edifikana.client.lib.features.main.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cramsan.edifikana.client.lib.features.main.camera.CameraContract
import com.cramsan.edifikana.client.lib.features.window.ComposableKoinContext
import com.cramsan.edifikana.client.lib.features.window.EdifikanaMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.navigation.EdifikanaPathNavigation
import com.cramsan.edifikana.client.lib.utils.shareContent
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Main activity for the application.
 */
class MainActivity :
    ComponentActivity(),
    EdifikanaMainScreenEventHandler {
        private val viewModel: EdifikanaWindowViewModel by inject()

    private val cameraLauncher =
        registerForActivityResult(CameraContract()) { filePath ->
        viewModel.handleReceivedImage(filePath?.let { CoreUri(it) })
    }

    @Suppress("MagicNumber")
    private val mediaAttachmentLauncher =
        registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5),
    ) { uris ->
        // TODO: Move mapping logic to ViewModel
        viewModel.handleReceivedImages(uris.map { CoreUri(it) })
    }

    @OptIn(KoinExperimentalAPI::class)
    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cold start: onNewIntent only fires for an already-running Activity instance, so a
        // deep link that launches the app fresh (the common case for an email link) must be
        // read from the launching intent here, before composition.
        val initialDestination = intent.toDestination()
        setContent {
            ComposableKoinContext {
                KoinScope<String>("root-window") {
                    EdifikanaWindowScreen(
                        eventHandler = this@MainActivity,
                        viewModel = viewModel,
                        initialDestination = initialDestination,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.toDestination()?.let { viewModel.handleDeepLink(it) }
    }

    /**
     * Resolves this intent's data URI to a [Destination] via the same
     * `EdifikanaPathNavigation`/`WebRoute` resolver used for browser-URL routing on wasmJs.
     * `WebRoute.fromWebPath` strips a leading `scheme://authority` generically, so the raw
     * `https://edifikana.com/...` URI string can be passed through as-is — any
     * `@WebPath`-annotated destination is reachable here with no app- or feature-specific
     * parsing.
     */
    private fun Intent.toDestination(): Destination? =
        data?.toString()?.let { EdifikanaPathNavigation.pathToDestination(it) }

    override fun openCamera(event: EdifikanaWindowsEvent.OpenCamera) {
        cameraLauncher.launch(event.filename)
    }

    override fun openImageExternally(event: EdifikanaWindowsEvent.OpenImageExternally) {
        ContextCompat.startActivity(
            this,
            Intent(
                Intent.ACTION_VIEW,
                event.imageUri.getAndroidUri(),
            ),
            null,
        )
    }

    override fun openPhotoPicker(event: EdifikanaWindowsEvent.OpenPhotoPicker) {
        mediaAttachmentLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
        )
    }

    override fun shareContent(event: EdifikanaWindowsEvent.ShareContent) {
        lifecycleScope.launch {
            (this@MainActivity as Context).shareContent(TAG, event.text, event.imageUri)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
