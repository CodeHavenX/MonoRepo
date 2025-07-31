package com.cramsan.framework.utils.hash

/**
 * Simple non-cryptographic hashing utilities suitable for map lookups and other general purposes.
 */
object HashUtils {

    private const val FNV_OFFSET_BASIS = 0x811c9dc5u
    private const val FNV_PRIME = 0x01000193u
    private const val HEX_STRING_LENGTH = 8
    private const val HEX_RADIX = 16

    /**
     * Computes a FNV-1a hash of the given byte array.
     * This is a fast, non-cryptographic hash function with good distribution properties
     * suitable for hash tables and general-purpose hashing.
     *
     * @param bytes the byte array to hash
     * @return a hexadecimal string representation of the hash
     */
    fun hash(bytes: ByteArray): String {
        // FNV-1a constants for 32-bit hash
        var hash = FNV_OFFSET_BASIS
        val prime = FNV_PRIME

        for (byte in bytes) {
            // XOR with byte, then multiply by prime
            hash = (hash xor (byte.toUByte().toUInt())) * prime
        }

        // Convert to hex string (8 characters for 32-bit hash)
        return hash.toString(HEX_RADIX).padStart(HEX_STRING_LENGTH, '0')
    }
}
