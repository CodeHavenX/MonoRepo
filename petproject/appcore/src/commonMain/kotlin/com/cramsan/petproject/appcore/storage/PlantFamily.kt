package com.cramsan.petproject.appcore.storage

import kotlin.Long
import kotlin.String

@Suppress("VariableNaming", "ConstructorParameterNaming")
interface PlantFamily {
    val id: Long

    val family: String

    val plantId: Long

    val locale: String
}
