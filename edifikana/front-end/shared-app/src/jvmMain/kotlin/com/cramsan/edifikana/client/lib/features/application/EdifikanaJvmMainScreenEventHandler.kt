package com.cramsan.edifikana.client.lib.features.application

import com.cramsan.edifikana.client.lib.features.window.EdifikanaMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame

/**
 * Main screen event handler for JVM.
 *
 * TODO: JVM/Desktop platform support is partially implemented:
 * - Photo picker: IMPLEMENTED (uses AWT FileDialog in background thread)
 * - Image processing: SIMPLIFIED (no rotation/compression, see CorutilesUtils.jvm.kt)
 * - Camera: NOT SUPPORTED (no webcam integration)
 * - External image viewing: NOT SUPPORTED (could use Desktop.getDesktop().open())
 * - Content sharing: NOT SUPPORTED
 *
 * @param scope CoroutineScope for launching background operations
 * @param onPhotoPickerResult Callback invoked when files are selected from the photo picker
 */
class EdifikanaJvmMainScreenEventHandler(
    private val scope: CoroutineScope,
    private val onPhotoPickerResult: (List<CoreUri>) -> Unit = {},
) : EdifikanaMainScreenEventHandler {

    override fun openCamera(event: EdifikanaWindowsEvent.OpenCamera) {
        logE(TAG, "Opening camera is not supported on JVM")
    }

    override fun openImageExternally(event: EdifikanaWindowsEvent.OpenImageExternally) {
        // TODO: Implement using Desktop.getDesktop().open() or similar
        logE(TAG, "Opening image externally is not supported on JVM")
    }

    override fun openPhotoPicker(event: EdifikanaWindowsEvent.OpenPhotoPicker) {
        logI(TAG, "Opening photo picker for JVM")

        // Launch file dialog in IO dispatcher to avoid blocking the main UI thread
        // AWT FileDialog is synchronous and will block, so we need to run it off the main thread
        scope.launch(Dispatchers.IO) {
            // Use AWT FileDialog for native file picker
            val fileDialog = FileDialog(null as Frame?, "Select Image Files", FileDialog.LOAD).apply {
                // Set filter for image files
                setFilenameFilter { _, name ->
                    val lowercaseName = name.lowercase()
                    lowercaseName.endsWith(".jpg") ||
                        lowercaseName.endsWith(".jpeg") ||
                        lowercaseName.endsWith(".png") ||
                        lowercaseName.endsWith(".gif") ||
                        lowercaseName.endsWith(".webp")
                }
                // Enable multiple file selection
                isMultipleMode = true
            }

            // Show dialog (blocks this coroutine until user selects or cancels)
            fileDialog.isVisible = true

            // Get selected files
            val selectedFiles = fileDialog.files
            if (selectedFiles != null && selectedFiles.isNotEmpty()) {
                logI(TAG, "User selected ${selectedFiles.size} file(s)")
                val uris = selectedFiles.map { file ->
                    CoreUri(file.toURI().toString())
                }
                onPhotoPickerResult(uris)
            } else {
                logI(TAG, "User cancelled file selection")
            }
        }
    }

    override fun shareContent(event: EdifikanaWindowsEvent.ShareContent) {
        // TODO: Implement using system clipboard or external sharing utilities
        logE(TAG, "Sharing content is not supported on JVM")
    }

    companion object {
        private const val TAG = "EdifikanaJvmMainScreenEventHandler"
    }
}
