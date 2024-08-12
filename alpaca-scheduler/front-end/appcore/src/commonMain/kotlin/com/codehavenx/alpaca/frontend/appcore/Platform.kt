package com.codehavenx.alpaca.frontend.appcore

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
