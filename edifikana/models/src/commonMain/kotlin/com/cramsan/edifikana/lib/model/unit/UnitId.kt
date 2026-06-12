package com.cramsan.edifikana.lib.model.unit

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a unit ID.
 */
@JvmInline
@Serializable
value class UnitId(val unitId: String) : PathParam {
    override fun toString(): String = unitId
}
