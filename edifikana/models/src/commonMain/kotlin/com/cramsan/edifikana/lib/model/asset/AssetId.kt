package com.cramsan.edifikana.lib.model.asset

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a file ID.
 */
@Serializable
@JvmInline
@JsonSchema.Description("Unique identifier of a stored file asset.")
@JsonSchema.Example("\"ast_a1b2c3d4\"")
value class AssetId(val assetId: String) : PathParam {
    override fun toString(): String = assetId
}
