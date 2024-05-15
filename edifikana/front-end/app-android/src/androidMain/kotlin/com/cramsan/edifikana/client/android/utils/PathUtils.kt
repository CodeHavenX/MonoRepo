package com.cramsan.edifikana.client.android.utils

import com.cramsan.edifikana.client.android.models.StorageRef
import java.net.URLEncoder

fun publicDownloadUrl(storageRef: StorageRef, storageBucket: String): String {
    return "$FIREBASE_PUBLIC_URL_ROOT$storageBucket/o/${urlEncode(storageRef.ref)}?$FIREBASE_PUBLIC_URL_MEDIA_SUFFIX"
}

private fun urlEncode(path: String): String {
    return URLEncoder.encode(path.trimStart { it == '/'}, "UTF-8")
}

private const val FIREBASE_PUBLIC_URL_MEDIA_SUFFIX = "alt=media"
private const val FIREBASE_PUBLIC_URL_ROOT = "https://firebasestorage.googleapis.com/v0/b/"
