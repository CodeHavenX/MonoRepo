package com.cramsan.edifikana.client.lib.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.logE
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error

suspend fun Context.shareContent(tag: String, text: String, imageUri: CoreUri?): Boolean {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    if (imageUri != null) {
        shareIntent.setType("image/jpeg")
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri.getAndroidUri())
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    } else {
        shareIntent.setType("text/plain")
    }

    try {
        val chooserIntent = Intent.createChooser(shareIntent, null)
        startActivity(chooserIntent)
        return true
    } catch (ex: ActivityNotFoundException) {
        logE(tag, "No activity found to share content", ex)
        Toast.makeText(
            this,
            org.jetbrains.compose.resources.getString(Res.string.error_message_unexpected_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    return false
}
