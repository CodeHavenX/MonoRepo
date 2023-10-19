package com.cramsan.runasimi.mpplib

import android.content.Context
import com.cramsan.runasimi.mpplib.main.FileManager
import java.io.File

class AndroidFileManager(
    private val context: Context,
) : FileManager {
    override fun saveToFile(filename: String, content: ByteArray): String {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(content)
        }
        val file = File(context.filesDir, filename)
        return file.absolutePath
    }
}
