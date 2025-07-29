package com.cramsan.framework.core

/**
 * Utility object for hashing operations.
 */
object Hashing {

    /**
     * Computes a hash for the given data using a placeholder implementation.
     *
     * @param data The byte array to hash.
     * @return An integer representing the hash of the input data.
     */
    @Suppress("MagicNumber")
    fun murmurhash(
        data: ByteArray,
    ): Int {
        // https://github.com/CodeHavenX/MonoRepo/issues/215
        // THIS IS NOT A MURMURHASH IMPLEMENTATION
        // This is placeholder code to simulate a hash function until a proper implementation is available.
        var hash = data.size
        for (byte in data) {
            hash = (hash * 31) + byte.toInt()
        }
        return hash
    }
}
