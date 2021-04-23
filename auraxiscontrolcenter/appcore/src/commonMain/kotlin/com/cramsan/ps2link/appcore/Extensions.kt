package com.cramsan.ps2link.appcore

import com.cramsan.ps2link.core.models.CensusLang
import com.cramsan.ps2link.core.models.Population
import com.cramsan.ps2link.core.models.RedditPost
import com.cramsan.ps2link.core.models.Server
import com.cramsan.ps2link.core.models.ServerStatus
import com.cramsan.ps2link.core.models.StatItem
import com.cramsan.ps2link.core.models.WeaponEventType
import com.cramsan.ps2link.db.Character
import com.cramsan.ps2link.db.Member
import com.cramsan.ps2link.db.Outfit
import com.cramsan.ps2link.db.models.Faction
import com.cramsan.ps2link.db.models.Namespace
import com.cramsan.ps2link.network.models.content.CharacterProfile
import com.cramsan.ps2link.network.models.content.OnlineStatus
import com.cramsan.ps2link.network.models.content.character.Stat
import com.cramsan.ps2link.network.models.content.item.StatNameType
import com.cramsan.ps2link.network.models.content.response.server.PopulationStatus
import com.cramsan.ps2link.network.models.content.world.Name_Multi
import com.cramsan.ps2link.network.models.reddit.Post
import com.cramsan.ps2link.network.models.twitter.PS2Tweet
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

/**
 * @Author cramsan
 * @created 1/17/2021
 */

fun CharacterProfile.toCharacter(namespace: Namespace, lastUpdated: Long): Character {
    return Character(
        id = character_id,
        name = name?.first,
        activeProfileId = profile_id?.toLong(),
        currentPoints = certs?.available_points?.toLong(),
        percentageToNextCert = certs?.percent_to_next?.toDouble(),
        percentageToNextRank = battle_rank?.percent_to_next?.toDouble(),
        rank = battle_rank?.value?.toLong(),
        lastLogin = times?.last_login?.toLong(),
        minutesPlayed = times?.minutes_played?.toLong(),
        factionId = Faction.fromString(faction_id),
        worldId = world_id,
        outfitName = outfitName,
        worldName = null,
        namespace = namespace,
        cached = false,
        lastUpdated = lastUpdated,
    )
}

fun Faction.toCoreModel() = when (this) {
    Faction.VS -> com.cramsan.ps2link.core.models.Faction.VS
    Faction.NC -> com.cramsan.ps2link.core.models.Faction.NC
    Faction.TR -> com.cramsan.ps2link.core.models.Faction.TR
    Faction.NS -> com.cramsan.ps2link.core.models.Faction.NS
    Faction.UNKNOWN -> com.cramsan.ps2link.core.models.Faction.UNKNOWN
}

fun com.cramsan.ps2link.core.models.Faction.toDBModel() = when (this) {
    com.cramsan.ps2link.core.models.Faction.VS -> Faction.VS
    com.cramsan.ps2link.core.models.Faction.NC -> Faction.NC
    com.cramsan.ps2link.core.models.Faction.TR -> Faction.TR
    com.cramsan.ps2link.core.models.Faction.NS -> Faction.NS
    com.cramsan.ps2link.core.models.Faction.UNKNOWN -> Faction.UNKNOWN
}

fun Namespace.toCoreModel() = when (this) {
    Namespace.PS2PC -> com.cramsan.ps2link.core.models.Namespace.PS2PC
    Namespace.PS2PS4US -> com.cramsan.ps2link.core.models.Namespace.PS2PS4US
    Namespace.PS2PS4EU -> com.cramsan.ps2link.core.models.Namespace.PS2PS4EU
    Namespace.UNDETERMINED -> com.cramsan.ps2link.core.models.Namespace.UNDETERMINED
}

fun com.cramsan.ps2link.core.models.Namespace.toDBModel() = when (this) {
    com.cramsan.ps2link.core.models.Namespace.PS2PC -> Namespace.PS2PC
    com.cramsan.ps2link.core.models.Namespace.PS2PS4US -> Namespace.PS2PS4US
    com.cramsan.ps2link.core.models.Namespace.PS2PS4EU -> Namespace.PS2PS4EU
    com.cramsan.ps2link.core.models.Namespace.UNDETERMINED -> Namespace.UNDETERMINED
}

fun com.cramsan.ps2link.core.models.Namespace.toNetworkModel() = when (this) {
    com.cramsan.ps2link.core.models.Namespace.PS2PC -> com.cramsan.ps2link.network.models.Namespace.PS2PC
    com.cramsan.ps2link.core.models.Namespace.PS2PS4US -> com.cramsan.ps2link.network.models.Namespace.PS2PS4US
    com.cramsan.ps2link.core.models.Namespace.PS2PS4EU -> com.cramsan.ps2link.network.models.Namespace.PS2PS4EU
    com.cramsan.ps2link.core.models.Namespace.UNDETERMINED -> com.cramsan.ps2link.network.models.Namespace.UNDETERMINED
}

fun com.cramsan.ps2link.network.models.content.Outfit.toDBModel(namespace: Namespace, lastUpdated: Long): Outfit {
    return Outfit(
        outfitId = outfit_id,
        name = name,
        alias = alias,
        leaderCharacterId = leader_character_id,
        memberCount = member_count.toLong(),
        timeCreated = time_created?.toLong(),
        worldId = world_id?.toLong(),
        factionId = Faction.NS,
        namespace = namespace,
        lastUpdated = lastUpdated,
    )
}

@OptIn(ExperimentalTime::class)
fun Character.toCoreModel(): com.cramsan.ps2link.core.models.Character {
    val server = if (worldId != null && worldName != null) {
        Server(worldId!!, worldName!!, null, namespace.toCoreModel())
    } else {
        null
    }

    return com.cramsan.ps2link.core.models.Character(
        characterId = id,
        name = name,
        activeProfileId = activeProfileId,
        certs = currentPoints,
        battleRank = rank,
        percentageToNextCert = percentageToNextCert,
        percentageToNextBattleRank = percentageToNextRank,
        lastLogin = lastLogin?.let { Instant.fromEpochMilliseconds(it) },
        timePlayed = minutesPlayed?.minutes,
        faction = factionId.toCoreModel(),
        server = server,
        outfit = null,
        namespace = namespace.toCoreModel(),
        cached = cached,
    )
}

@OptIn(ExperimentalTime::class)
fun com.cramsan.ps2link.core.models.Character.toDBModel(lastUpdated: Long): Character {
    return Character(
        id = characterId,
        name = name,
        activeProfileId = activeProfileId,
        currentPoints = certs,
        percentageToNextCert = percentageToNextCert,
        percentageToNextRank = percentageToNextBattleRank,
        rank = battleRank,
        lastLogin = lastLogin?.toEpochMilliseconds(),
        minutesPlayed = timePlayed?.inMinutes?.toLong(),
        factionId = faction.toDBModel(),
        worldId = server?.worldId,
        worldName = server?.serverName,
        outfitName = outfit?.name,
        namespace = namespace.toDBModel(),
        cached = true,
        lastUpdated = lastUpdated,
    )
}

fun Name_Multi.localizedName(currentLang: CensusLang): String? {
    return when (currentLang) {
        CensusLang.DE -> this.de
        CensusLang.ES -> this.es
        CensusLang.FR -> this.fr
        CensusLang.IT -> this.it
        CensusLang.TR -> this.tr
        CensusLang.EN -> this.en
    }
}

fun PopulationStatus?.toCoreModel(): Population = when (this) {
    PopulationStatus.HIGH -> Population.HIGH
    PopulationStatus.MEDIUM -> Population.MEDIUM
    PopulationStatus.LOW -> Population.LOW
    null -> Population.UNKNOWN
}

fun OnlineStatus?.toCoreModel(): ServerStatus = when (this) {
    OnlineStatus.ONLINE -> ServerStatus.ONLINE
    OnlineStatus.OFFLINE -> ServerStatus.OFFLINE
    OnlineStatus.LOCKED -> ServerStatus.LOCKED
    null -> ServerStatus.UNKNOWN
}

fun Stat.toStatItem(): StatItem {
    return StatItem(
        statName = stat_name,
        allTime = all_time?.toDouble(),
        today = getToday().toDouble(),
        thisWeek = getThisWeek().toDouble(),
        thisMonth = getThisMonth().toDouble(),
    )
}

fun com.cramsan.ps2link.network.models.content.Outfit.toCoreModel(namespace: Namespace, lastUpdated: Long): com.cramsan.ps2link.core.models.Outfit {
    return com.cramsan.ps2link.core.models.Outfit(
        id = outfit_id,
        name = name,
        tag = alias,
        faction = Faction.fromString(faction_id).toCoreModel(),
        worldId = world_id?.toLong() ?: 0,
        timeCreated = time_created?.let { Instant.fromEpochMilliseconds(it.toLong()) },
        leaderCharacterId = leader_character_id,
        memberCount = member_count,
        namespace = namespace.toCoreModel(),
    )
}

fun Member.toCoreModel(): com.cramsan.ps2link.core.models.Member {
    return com.cramsan.ps2link.core.models.Member(id)
}

@OptIn(ExperimentalTime::class)
fun Outfit.toCoreModel(): com.cramsan.ps2link.core.models.Outfit {
    return com.cramsan.ps2link.core.models.Outfit(
        id = outfitId,
        name = name,
        tag = alias,
        faction = factionId.toCoreModel(),
        worldId = worldId ?: 0,
        timeCreated = timeCreated?.let { Instant.fromEpochMilliseconds(it) },
        leaderCharacterId = leaderCharacterId,
        memberCount = memberCount?.toInt() ?: 0,
        namespace = namespace.toCoreModel(),
    )
}

fun com.cramsan.ps2link.core.models.Outfit.toDBModel(lastUpdated: Long): Outfit {
    return Outfit(
        outfitId = id,
        name = name,
        alias = tag,
        leaderCharacterId = leaderCharacterId,
        memberCount = memberCount.toLong(),
        timeCreated = timeCreated?.toEpochMilliseconds(),
        worldId = worldId,
        factionId = faction.toDBModel(),
        namespace = namespace.toDBModel(),
        lastUpdated = lastUpdated,
    )
}

fun Stat.getToday(): Float {
    return day?.d01?.toFloat() ?: 0f
}

fun Stat.setToday(value: Float) {
    day?.d01 = value.toString()
}

fun Stat.getThisWeek(): Float {
    return week?.w01?.toFloat() ?: 0f
}

fun Stat.setThisWeek(value: Float) {
    week?.w01 = value.toString()
}

fun Stat.getThisMonth(): Float {
    return month?.m01?.toFloat() ?: 0f
}

fun Stat.setThisMonth(value: Float) {
    month?.m01 = value.toString()
}

fun StatNameType.toWeaponEventType(): WeaponEventType {
    return when (this) {
        StatNameType.WEAPON_KILLS -> WeaponEventType.KILLS
        StatNameType.WEAPON_VEHICLE_KILLS -> WeaponEventType.VEHICLE_KILLS
        StatNameType.WEAPON_HEADSHOTS -> WeaponEventType.HEADSHOT_KILLS
        StatNameType.WEAPON_KILLED_BY -> WeaponEventType.KILLED_BY
        StatNameType.WEAPON_DAMAGE_TAKEN -> WeaponEventType.DAMAGE_TAKEN
        StatNameType.WEAPON_DAMAGE_GIVEN -> WeaponEventType.DAMAGE_GIVEN
    }
}

fun Post.toCoreModel(): RedditPost {
    return RedditPost(
        url = url ?: "",
        title = title ?: "",
        author = author ?: "",
        label = selftext,
        upvotes = ((ups ?: 0) - (downs ?: 0)),
        comments = num_comments ?: 0,
        createdTime = created_utc?.times(1000)?.toLong() ?: 0,
        imgUr = "",
    )
}

fun PS2Tweet.toCoreModel(): com.cramsan.ps2link.core.models.PS2Tweet {
    return com.cramsan.ps2link.core.models.PS2Tweet(
        user = user ?: "",
        content = content ?: "",
        tag = tag ?: "",
        date = date ?: 0,
        imgUrl = imgUrl ?: "",
        id = id ?: "",
    )
}
