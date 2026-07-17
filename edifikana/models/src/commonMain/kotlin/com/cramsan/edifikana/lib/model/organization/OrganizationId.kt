package com.cramsan.edifikana.lib.model.organization

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an organization ID.
 */
@Serializable
@JvmInline
@JsonSchema.Description("Unique identifier of an organization.")
@JsonSchema.Example("\"org_a1b2c3d4\"")
value class OrganizationId(val id: String) : PathParam {
    override fun toString(): String = id
}
