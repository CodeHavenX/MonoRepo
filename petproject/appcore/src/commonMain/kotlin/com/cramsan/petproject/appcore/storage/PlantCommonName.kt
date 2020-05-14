package com.cramsan.petproject.appcore.storage

import kotlin.Long
import kotlin.String

@Suppress("VariableNaming", "ConstructorParameterNaming")
interface PlantCommonName {
    val id: Long

    val commonName: String

    val plantId: Long

    val locale: String
}
