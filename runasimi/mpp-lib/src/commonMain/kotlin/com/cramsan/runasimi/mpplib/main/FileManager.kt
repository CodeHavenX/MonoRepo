package com.cramsan.runasimi.mpplib.main

interface FileManager {

    fun saveToFile(filename: String, content: ByteArray): String
}
