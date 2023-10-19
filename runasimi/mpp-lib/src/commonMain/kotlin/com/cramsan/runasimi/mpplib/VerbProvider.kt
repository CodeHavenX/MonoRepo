package com.cramsan.runasimi.mpplib

class VerbProvider(
    private val fileReader: FileReader,
) {
    fun loadResources(): List<Verb> {
        val csv = fileReader.readCsv("verbos.txt")
        return csv.map {
            val segments = it.split(";")
            Verb(
                segments[1].split(","),
                segments[0].split(","),
            )
        }
    }
}

data class Verb(
    val root: List<String>,
    val meaning: List<String>,
)
