package com.cramsan.edifikana.client.android.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface

fun downscaleImage(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap {
    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    val scaledBitmap = BitmapFactory.decodeFile(filePath, options)

    /*
    val origImage = BitmapFactory.decodeFile(filePath, BitmapFactory.Options())
    println("Scaled: ${scaledBitmap.height} - ${scaledBitmap.width}")
    println("Orig: ${origImage.height} - ${origImage.width}")
     */

    // Read the image's EXIF data to get the orientation
    val exif = ExifInterface(filePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    // Create a new matrix for the rotation
    val matrix = Matrix()

    // Rotate the bitmap to the correct orientation
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }

    return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val height: Int = options.outHeight
    val width: Int = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}