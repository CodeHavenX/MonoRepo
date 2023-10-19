package com.cramsan.runasimi.mpplib

import kotlin.io.bufferedReader
import kotlin.io.readLines
import kotlin.jvm.java

class AndroidFileReader : FileReader {
    override fun readCsv(filename: String): List<String> {
        val stream = this::class.java.classLoader.getResourceAsStream(filename)
        val lines = stream?.bufferedReader()?.readLines()
        return lines ?: emptyList()
    }
}
