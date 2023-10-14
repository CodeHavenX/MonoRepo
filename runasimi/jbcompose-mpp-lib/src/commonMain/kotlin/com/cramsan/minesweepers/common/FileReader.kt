
package com.cramsan.minesweepers.common

interface FileReader {
    fun readCsv(filename: String): List<String>
}
