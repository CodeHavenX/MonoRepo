package com.codehavenx.alpaca.frontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
