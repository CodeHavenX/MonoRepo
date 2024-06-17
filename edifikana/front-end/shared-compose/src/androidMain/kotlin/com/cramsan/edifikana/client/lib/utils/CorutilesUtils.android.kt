package com.cramsan.edifikana.client.lib.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import com.cramsan.framework.core.CoreUri
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

actual fun readBytes(uri: CoreUri, dependencies: IODependencies): Result<ByteArray> = runCatching {
    dependencies.contentResolver.openInputStream(uri.getAndroidUri()).use { inputStream ->
        requireNotNull(inputStream) { "Failed to open input stream for uri: $uri" }
        inputStream.readBytes()
    }
}

@Suppress("MagicNumber")
actual fun processImageData(data: ByteArray): Result<ByteArray> = runCatching {
    val exifInterface = ExifInterface(ByteArrayInputStream(data))
    val rotation = when (
        exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    ) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
    val matrix = Matrix().apply { postRotate(rotation) }
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

    val stream = ByteArrayOutputStream()
    stream.use {
        // TODO: Set the compression to be configurable
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 35, stream)
        val byteArray = stream.toByteArray()
        byteArray
    }
}

actual class IODependencies(
    val contentResolver: ContentResolver,
)
