package com.cramsan.runasimi.mpplib

class AndroidFileReader : FileReader {
    override fun readCsv(filename: String): List<String> {
        val stream = this::class.java.classLoader.getResourceAsStream(filename)
        val lines = stream?.bufferedReader()?.readLines()
        return lines ?: emptyList()
    }
}
