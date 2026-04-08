package com.cramsan.edifikana.lib.model.rent

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a rent config ID.
 */
@JvmInline
@Serializable
value class RentConfigId(val rentConfigId: String) : PathParam {
    override fun toString(): String = rentConfigId
}
