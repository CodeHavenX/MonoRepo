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
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.EdifikanaMainScreenEventHandler
import com.cramsan.edifikana.client.lib.utils.shareContent
import com.cramsan.framework.core.CoreUri
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity(), EdifikanaMainScreenEventHandler {

    private val viewModel: EdifikanaApplicationViewModel by inject()

    private val cameraLauncher = registerForActivityResult(CameraContract()) { filePath ->
        viewModel.handleReceivedImage(filePath?.let { CoreUri(it) })
    }

    @Suppress("MagicNumber")
    private val mediaAttachmentLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5),
    ) { uris ->
        // TODO: Move mapping logic to ViewModel
        viewModel.handleReceivedImages(uris.map { CoreUri(it) })
    }

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EdifikanaApplicationScreen(
                viewModel = viewModel,
                eventHandler = this,
            )
        }
    }

    override fun openCamera(event: EdifikanaApplicationEvent.OpenCamera) {
        cameraLauncher.launch(event.filename)
    }

    override fun openImageExternally(event: EdifikanaApplicationEvent.OpenImageExternally) {
        ContextCompat.startActivity(
            this,
            Intent(
                Intent.ACTION_VIEW,
                event.imageUri.getAndroidUri(),
            ),
            null,
        )
    }

    override fun openPhotoPicker(event: EdifikanaApplicationEvent.OpenPhotoPicker) {
        mediaAttachmentLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    override fun shareContent(event: EdifikanaApplicationEvent.ShareContent) {
        lifecycleScope.launch {
            (this@MainActivity as Context).shareContent(TAG, event.text, event.imageUri)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
