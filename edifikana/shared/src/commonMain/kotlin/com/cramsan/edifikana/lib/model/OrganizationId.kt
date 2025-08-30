package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

/**
 * Domain model representing an organization ID.
 */
@JvmInline
value class OrganizationId(val id: String) {
    override fun toString(): String = id
}
