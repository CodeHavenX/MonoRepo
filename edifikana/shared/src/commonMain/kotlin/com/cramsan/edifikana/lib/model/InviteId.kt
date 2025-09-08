package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

@JvmInline
value class InviteId(
    val id: String
) {
    override fun toString(): String = id
}
