package com.cramsan.flyerboard.client.lib.filepicker

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Android stub — file picking requires an ActivityResultLauncher registered with the host Activity.
 * Returns null until wired up to ActivityResultContracts.GetContent.
 */
actual class FilePicker actual constructor(backgroundDispatcher: CoroutineDispatcher) {
    actual suspend fun pickFile(): PickedFile? = null
}
