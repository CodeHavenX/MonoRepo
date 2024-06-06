package com.cramsan.edifikana.client.lib.models

import com.cramsan.framework.assertlib.assert

@JvmInline
value class StorageRef(val ref: String) {

    constructor(filename: String, path: List<String>) : this((path + filename).joinToString("/"))

    init {
        assert(filename().isNotEmpty(), TAG, "Missing filename")
    }

    fun filename(): String {
        return ref.split("/").last()
    }

    companion object {
        private const val TAG = "StorageRef"
    }
}
