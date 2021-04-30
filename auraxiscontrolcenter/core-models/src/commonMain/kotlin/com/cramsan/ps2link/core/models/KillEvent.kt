package com.cramsan.ps2link.core.models

import kotlinx.datetime.Instant

/**
 * @Author cramsan
 * @created 1/26/2021
 */
data class KillEvent(
    val characterId: String?,
    val namespace: Namespace,
    val killType: KillType = KillType.UNKNOWN,
    val faction: Faction = Faction.UNKNOWN,
    val attacker: String? = null,
    val time: Instant? = null,
    val weaponName: String? = null,
    val weaponImage: String? = null,
)
