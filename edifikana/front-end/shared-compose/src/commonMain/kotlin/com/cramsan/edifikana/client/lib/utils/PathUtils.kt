package com.cramsan.edifikana.client.lib.utils

import com.cramsan.edifikana.client.lib.models.StorageRef
import io.github.jan.supabase.storage.Storage
import java.net.URLEncoder

fun publicDownloadUrl(storageRef: StorageRef, storageBucket: String): String {
    return "$FIREBASE_PUBLIC_URL_ROOT$storageBucket/o/${urlEncode(storageRef.ref)}?$FIREBASE_PUBLIC_URL_MEDIA_SUFFIX"
}

private fun urlEncode(path: String): String {
    return URLEncoder.encode(path.trimStart { it == '/' }, "UTF-8")
}

private const val FIREBASE_PUBLIC_URL_MEDIA_SUFFIX = "alt=media"
private const val FIREBASE_PUBLIC_URL_ROOT = "https://firebasestorage.googleapis.com/v0/b/"

fun publicSupabaseDownloadUrl(
    storageRef: StorageRef,
    storage: Storage,
): String {
    return storage.from("test").publicUrl(storageRef.ref)
}
