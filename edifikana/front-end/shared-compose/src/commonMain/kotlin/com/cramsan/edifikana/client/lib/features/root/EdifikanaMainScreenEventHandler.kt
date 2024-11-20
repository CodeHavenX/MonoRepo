package com.cramsan.edifikana.client.lib.features.root

/**
 * Events that can be triggered application-wide.
 */
interface EdifikanaMainScreenEventHandler {

    /**
     * Open the camera.
     */
    fun openCamera(event: EdifikanaApplicationEvent.OpenCamera)

    /**
     * Open the image externally.
     */
    fun openImageExternally(event: EdifikanaApplicationEvent.OpenImageExternally)

    /**
     * Open the photo picker.
     */
    fun openPhotoPicker(event: EdifikanaApplicationEvent.OpenPhotoPicker)

    /**
     * Share content.
     */
    fun shareContent(event: EdifikanaApplicationEvent.ShareContent)

    /**
     * Show a snackbar.
     */
    fun showSnackbar(event: EdifikanaApplicationEvent.ShowSnackbar)
}
