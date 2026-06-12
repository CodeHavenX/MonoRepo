package com.cramsan.edifikana.lib.model.commonArea

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a common area ID.
 */
@JvmInline
@Serializable
value class CommonAreaId(val commonAreaId: String) : PathParam {
    override fun toString(): String = commonAreaId
}
