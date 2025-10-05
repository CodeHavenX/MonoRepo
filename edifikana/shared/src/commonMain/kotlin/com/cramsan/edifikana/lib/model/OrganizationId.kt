package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an organization ID.
 */
@Serializable
@JvmInline
value class OrganizationId(val id: String) : PathParam {
    override fun toString(): String = id
}
