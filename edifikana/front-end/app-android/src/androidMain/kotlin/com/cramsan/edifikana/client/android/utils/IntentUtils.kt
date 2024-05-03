package com.cramsan.edifikana.client.android.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toFile


fun Context.shareToWhatsApp(text: String, imageUri: Uri?): Boolean {
    val whatsappIntent = Intent(Intent.ACTION_SEND)
    whatsappIntent.setType("text/plain")
    whatsappIntent.setPackage("com.whatsapp")
    whatsappIntent.putExtra(Intent.EXTRA_TEXT, text)
    imageUri?.let {
        val contentUri: Uri = FileProvider.getUriForFile(this, packageName+".fileprovider", imageUri.toFile())
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        whatsappIntent.setType("image/jpeg")
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }


    try {
        startActivity(whatsappIntent)
        return true
    } catch (ex: ActivityNotFoundException) {
        //Toast.makeText(this, "There was an error when trying to share.", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "$text\n$imageUri", Toast.LENGTH_SHORT).show()
    }

    return false
}