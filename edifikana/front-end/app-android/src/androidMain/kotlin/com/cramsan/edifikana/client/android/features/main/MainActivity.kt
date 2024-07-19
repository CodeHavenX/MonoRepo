package com.cramsan.edifikana.client.android.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cramsan.edifikana.client.android.features.camera.CameraContract
import com.cramsan.edifikana.client.lib.features.main.EdifikanaApplicationScreen
import com.cramsan.edifikana.client.lib.features.main.EdifikanaMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.MainActivityViewModel
import com.cramsan.edifikana.client.lib.utils.shareContent
import com.cramsan.framework.core.CoreUri
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity(), EdifikanaMainScreenEventHandler {

    private val viewModel: MainActivityViewModel by inject()

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
            EdifikanaApplicationScreen(eventHandler = this)
        }
    }

    override fun openCamera(event: MainActivityEvent.OpenCamera) {
        cameraLauncher.launch(event.filename)
    }

    override fun openImageExternally(event: MainActivityEvent.OpenImageExternally) {
        ContextCompat.startActivity(
            this,
            Intent(
                Intent.ACTION_VIEW,
                event.imageUri.getAndroidUri(),
            ),
            null,
        )
    }

    override fun openPhotoPicker(event: MainActivityEvent.OpenPhotoPicker) {
        mediaAttachmentLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    override fun shareContent(event: MainActivityEvent.ShareContent) {
        lifecycleScope.launch {
            (this@MainActivity as Context).shareContent(TAG, event.text, event.imageUri)
        }
    }

    override fun showSnackbar(event: MainActivityEvent.ShowSnackbar) {
        Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
