package com.cramsan.runasimi.mpplib.main

interface SoundManager {

    suspend fun playSound(path: String)

    fun playSoundAsync(path: String)
}
