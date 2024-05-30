package com.cramsan.edifikana.client.android.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.cramsan.edifikana.client.android.R

fun Context.shareContent(text: String, imageUri: Uri?): Boolean {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    if (imageUri != null) {
        shareIntent.setType("image/jpeg")
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    } else {
        shareIntent.setType("text/plain")
    }

    try {
        val chooserIntent = Intent.createChooser(shareIntent, null)
        startActivity(chooserIntent)
        return true
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.error_message_unexpected_error), Toast.LENGTH_SHORT).show()
    }

    return false
}
