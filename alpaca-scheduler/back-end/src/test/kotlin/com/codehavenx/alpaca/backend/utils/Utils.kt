package com.codehavenx.alpaca.backend.utils

import com.cramsan.framework.utils.format.replaceWindowLEWithUnixLE

/**
 * Helper function for reading a file from the resources directory.
 * TODO: merge with [edifikana/back-end/src/test/kotlin/com/cramsan/edifikana/server/core/utils/Utils.kt]
 */
fun Any.readFileContent(fileName: String) =
    this::class.java.classLoader.getResource(fileName)?.readText()?.replaceWindowLEWithUnixLE()
