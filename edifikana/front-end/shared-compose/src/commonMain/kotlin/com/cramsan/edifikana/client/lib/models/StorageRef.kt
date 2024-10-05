package com.cramsan.edifikana.client.lib.models

import com.cramsan.framework.assertlib.assert

/**
 * Reference to a file in storage.
 */
@JvmInline
value class StorageRef(val ref: String) {

    constructor(filename: String, path: List<String>) : this((path + filename).joinToString("/"))

    init {
        assert(filename().isNotEmpty(), TAG, "Missing filename")
    }

    /**
     * Get the filename.
     */
    fun filename(): String {
        return ref.split("/").last()
    }

    /**
     * Get the path.
     */
    fun path(): List<String> {
        val array = ref.split("/")
        return array.subList(0, array.size - 1)
    }

    companion object {
        private const val TAG = "StorageRef"
    }
}
