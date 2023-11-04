package com.cramsan.runasimi.mpplib

class PhraseProvider(
    private val fileReader: FileReader,
) {
    fun loadResources(): List<Phrase> {
        val csv = fileReader.readCsv("frases.txt")
        return csv.map {
            val segments = it.split(";")
            Phrase(
                segments[1],
                segments[0],
            )
        }
    }
}

data class Phrase(
    val phrase: String,
    val meaning: String,
)
