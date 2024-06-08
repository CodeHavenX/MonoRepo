package com.cramsan.framework.core

expect class CoreUri {

    fun getUri(): String

    override fun toString(): String

    companion object {
        fun createUri(uri: String): CoreUri
    }
}
