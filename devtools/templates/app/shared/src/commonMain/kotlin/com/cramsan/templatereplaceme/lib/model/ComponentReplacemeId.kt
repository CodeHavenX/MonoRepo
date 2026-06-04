package com.cramsan.templatereplaceme.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Strongly-typed ID wrapper for [ComponentReplaceme] entities.
 *
 * Using a value class instead of a raw [String] prevents accidentally mixing IDs from
 * different entity types at compile time.
 */
@JvmInline
@Serializable
value class ComponentReplacemeId(val id: String) {
    override fun toString(): String = id
}
