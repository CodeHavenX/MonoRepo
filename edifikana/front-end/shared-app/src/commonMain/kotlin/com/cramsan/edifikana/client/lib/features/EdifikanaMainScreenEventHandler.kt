package com.cramsan.edifikana.client.lib.features

/**
 * Events that can be triggered application-wide.
 */
interface EdifikanaMainScreenEventHandler {

    /**
     * Open the camera.
     */
    fun openCamera(event: EdifikanaWindowsEvent.OpenCamera)

    /**
     * Open the image externally.
     */
    fun openImageExternally(event: EdifikanaWindowsEvent.OpenImageExternally)

    /**
     * Open the photo picker.
     */
    fun openPhotoPicker(event: EdifikanaWindowsEvent.OpenPhotoPicker)

    /**
     * Share content.
     */
    fun shareContent(event: EdifikanaWindowsEvent.ShareContent)
}
