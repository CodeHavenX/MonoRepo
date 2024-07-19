package com.cramsan.edifikana.client.lib.features.main

interface EdifikanaMainScreenEventHandler {
    fun openCamera(event: MainActivityEvent.OpenCamera)
    fun openImageExternally(event: MainActivityEvent.OpenImageExternally)
    fun openPhotoPicker(event: MainActivityEvent.OpenPhotoPicker)
    fun shareContent(event: MainActivityEvent.ShareContent)
    fun showSnackbar(event: MainActivityEvent.ShowSnackbar)
}
