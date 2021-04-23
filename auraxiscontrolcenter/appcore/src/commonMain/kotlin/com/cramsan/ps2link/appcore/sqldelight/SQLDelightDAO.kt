package com.cramsan.ps2link.appcore.sqldelight

import com.cramsan.framework.thread.assertIsBackgroundThread
import com.cramsan.ps2link.appcore.toCoreModel
import com.cramsan.ps2link.appcore.toDBModel
import com.cramsan.ps2link.core.models.Character
import com.cramsan.ps2link.core.models.Faction
import com.cramsan.ps2link.core.models.Member
import com.cramsan.ps2link.core.models.Namespace
import com.cramsan.ps2link.core.models.Outfit
import com.cramsan.ps2link.db.models.PS2LinkDB
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class SQLDelightDAO(
    sqlDriver: SqlDriver,
    private val clock: Clock,
) : DbgDAO {

    private var database: PS2LinkDB = PS2LinkDB(
        driver = sqlDriver,
        CharacterAdapter = com.cramsan.ps2link.db.Character.Adapter(
            factionIdAdapter = factionAdapter,
            namespaceAdapter = namespaceAdapter,
        ),
        OutfitAdapter = com.cramsan.ps2link.db.Outfit.Adapter(
            factionIdAdapter = factionAdapter,
            namespaceAdapter = namespaceAdapter,
        ),
        MemberAdapter = com.cramsan.ps2link.db.Member.Adapter(
            namespaceAdapter = namespaceAdapter,
        )
    )

    override fun insertCharacter(
        characterId: String,
        name: String?,
        activeProfileId: Long?,
        currentPoints: Long?,
        percentageToNextCert: Double?,
        percentageToNextRank: Double?,
        rank: Long?,
        lastLogin: Long?,
        minutesPlayed: Long?,
        factionId: Faction,
        worldId: String?,
        outfitName: String?,
        worldName: String?,
        namespace: Namespace,
        cached: Boolean,
        lastUpdated: Long,
    ) {
        assertIsBackgroundThread()
        return database.characterQueries.insertCharacter(
            characterId,
            name,
            activeProfileId,
            currentPoints,
            percentageToNextCert,
            percentageToNextRank,
            rank,
            lastLogin,
            minutesPlayed,
            factionId.toDBModel(),
            worldId,
            outfitName,
            worldName,
            namespace.toDBModel(),
            cached,
            lastUpdated,
        )
    }

    override fun insertCharacter(character: Character) {
        assertIsBackgroundThread()
        return database.characterQueries.insertCharacterInstance(character.toDBModel(clock.now().toEpochMilliseconds()))
    }

    override fun getAllCharactersAsFlow(): Flow<List<Character>> {
        return database.characterQueries.getAllCharacters().asFlow().mapToList().map { list -> list.map { character -> character.toCoreModel() } }
    }

    override fun removeCharacter(
        characterId: String,
        namespace: Namespace,
    ) {
        assertIsBackgroundThread()
        return database.characterQueries.deleteCharacter(
            characterId,
            namespace.toDBModel(),
        )
    }

    override fun getCharacter(
        characterId: String,
        namespace: Namespace,
    ): Character? {
        assertIsBackgroundThread()
        return database.characterQueries.getCharacter(characterId, namespace.toDBModel())
            .executeAsOneOrNull()?.toCoreModel()
    }

    override fun getCharacterAsFlow(
        characterId: String,
        namespace: Namespace
    ): Flow<Character?> {
        return database.characterQueries.getCharacter(characterId, namespace.toDBModel()).asFlow().mapToOneOrNull().map { it?.toCoreModel() }
    }

    override fun insertMember(
        id: String,
        rank: String?,
        outfitId: String?,
        onlineStatus: String?,
        name: String?,
        namespace: Namespace,
        lastUpdated: Long,
    ) {
        assertIsBackgroundThread()
        database.memberQueries.insertMember(
            id,
            rank,
            outfitId,
            onlineStatus,
            name,
            namespace.toDBModel(),
            lastUpdated
        )
    }

    override fun insertMemberList(
        memberList: List<Member>,
        namespace: Namespace,
        lastUpdated: Long,
    ) {
        assertIsBackgroundThread()
        database.memberQueries.transaction {
            memberList.forEach {
                database.memberQueries.insertMember(
                    it.id,
                    it.rank,
                    it.outfitId,
                    it.online,
                    it.name,
                    namespace.toDBModel(),
                    lastUpdated
                )
            }
        }
    }

    override fun removeMembersFromOutfit(
        outfitId: String,
        namespace: Namespace,
    ) {
        assertIsBackgroundThread()
        database.memberQueries.deleteOutfit(outfitId, namespace.toDBModel())
    }

    override fun getCharacters(): List<Character> {
        assertIsBackgroundThread()
        return database.characterQueries.getAllCharacters().executeAsList().map { it.toCoreModel() }
    }

    override fun getAllMembers(
        outfitId: String,
        namespace: Namespace,
    ): List<Member> {
        assertIsBackgroundThread()
        return database.memberQueries.getAllMembers(outfitId, namespace.toDBModel()).executeAsList().map { it.toCoreModel() }
    }

    override fun getOutfit(
        outfitId: String,
        namespace: Namespace,
    ): Outfit? {
        assertIsBackgroundThread()
        return database.outfitQueries.getOutfit(outfitId, namespace.toDBModel()).executeAsOneOrNull()?.toCoreModel()
    }

    override fun getOutfitAsFlow(outfitId: String, namespace: Namespace): Flow<Outfit?> {
        return database.outfitQueries.getOutfit(outfitId, namespace.toDBModel()).asFlow().mapToOneOrNull().map { it?.toCoreModel() }
    }

    override fun getAllOutfits(): List<Outfit> {
        assertIsBackgroundThread()
        return database.outfitQueries.getAllOutfits().executeAsList().map { it.toCoreModel() }
    }

    override fun getAllOutfitsAsFlow(): Flow<List<Outfit>> {
        return database.outfitQueries.getAllOutfits().asFlow().mapToList().map { list -> list.map { outfit -> outfit.toCoreModel() } }
    }

    override fun insertOutfit(
        outfitId: String,
        name: String?,
        alias: String?,
        leaderCharacterId: String?,
        memberCount: Long?,
        timeCreated: Long?,
        worldId: Long?,
        factionId: Faction,
        namespace: Namespace,
        lastUpdated: Long,
    ) {
        assertIsBackgroundThread()
        database.outfitQueries.insertOutfit(
            outfitId,
            name,
            alias,
            leaderCharacterId,
            memberCount,
            timeCreated,
            worldId,
            factionId.toDBModel(),
            namespace.toDBModel(),
            lastUpdated
        )
    }

    override fun insertOutfit(outfit: Outfit) {
        assertIsBackgroundThread()
        return database.outfitQueries.insertOutfitInstance(outfit.toDBModel(clock.now().toEpochMilliseconds()))
    }

    override fun removeOutfit(
        outfitId: String,
        namespace: Namespace,
    ) {
        assertIsBackgroundThread()
        database.outfitQueries.deleteOutfit(outfitId, namespace.toDBModel())
    }

    override fun deleteAll() {
        assertIsBackgroundThread()
        database.characterQueries.deleteAll()
        database.memberQueries.deleteAll()
        database.outfitQueries.deleteAll()
    }
}
