
package com.cramsan.runasimi.mpplib

interface FileReader {
    fun readCsv(filename: String): List<String>
}
