package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an organization ID.
 */
@Serializable
@JvmInline
value class OrganizationId(val id: String) {
    override fun toString(): String = id
}
