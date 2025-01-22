package com.cramsan.edifikana.server.core.utils

import com.cramsan.framework.utils.format.replaceWindowLEWithUnixLE

/**
 * Helper function for reading a file from the resources directory.
 */
fun Any.readFileContent(fileName: String) =
    this::class.java.classLoader.getResource(fileName)?.readText()?.replaceWindowLEWithUnixLE()
