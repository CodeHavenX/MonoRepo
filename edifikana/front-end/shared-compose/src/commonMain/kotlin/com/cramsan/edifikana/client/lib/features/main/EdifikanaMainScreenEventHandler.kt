package com.cramsan.edifikana.client.lib.features.main

/**
 * Events that can be triggered in the main activity.
 */
interface EdifikanaMainScreenEventHandler {

    /**
     * Open the camera.
     */
    fun openCamera(event: MainActivityEvent.OpenCamera)

    /**
     * Open the image externally.
     */
    fun openImageExternally(event: MainActivityEvent.OpenImageExternally)

    /**
     * Open the photo picker.
     */
    fun openPhotoPicker(event: MainActivityEvent.OpenPhotoPicker)

    /**
     * Share content.
     */
    fun shareContent(event: MainActivityEvent.ShareContent)

    /**
     * Show a snackbar.
     */
    fun showSnackbar(event: MainActivityEvent.ShowSnackbar)
}
