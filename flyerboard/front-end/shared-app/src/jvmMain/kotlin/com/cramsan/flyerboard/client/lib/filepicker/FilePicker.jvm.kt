package com.cramsan.flyerboard.client.lib.filepicker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import kotlin.coroutines.resume

/** JVM Desktop implementation — shows a native Swing file chooser dialog. */
actual class FilePicker actual constructor(private val backgroundDispatcher: CoroutineDispatcher) {
    /**
     * Suspending function to open the file picker.
     */
    actual suspend fun pickFile(): PickedFile? {
        val file: File =
            suspendCancellableCoroutine { continuation ->
                SwingUtilities.invokeLater {
                    val chooser = JFileChooser()
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        continuation.resume(chooser.selectedFile)
                    } else {
                        continuation.resume(null)
                    }
                }
            } ?: return null

        return withContext(backgroundDispatcher) {
            PickedFile(
                bytes = file.readBytes(),
                name = file.name,
                mimeType =
                java.net.URLConnection.guessContentTypeFromName(file.name)
                    ?: "application/octet-stream",
            )
        }
    }
}
