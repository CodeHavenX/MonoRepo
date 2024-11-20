package com.codehavenx.alpaca.backend.utils

/**
 * Helper function for reading a file from the resources directory.
 */
fun Any.readFileContent(fileName: String) = this::class.java.classLoader.getResource(fileName)?.readText()
