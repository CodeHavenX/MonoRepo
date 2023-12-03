package com.codehavenx.platform.bot.utils

fun Any.readResource(fileName: String) = this::class.java.classLoader.getResource(fileName)?.readText()
