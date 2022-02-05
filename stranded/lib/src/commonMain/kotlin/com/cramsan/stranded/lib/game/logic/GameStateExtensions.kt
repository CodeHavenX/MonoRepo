package com.cramsan.stranded.lib.game.logic

import com.cramsan.stranded.lib.game.models.GamePlayer
import com.cramsan.stranded.lib.game.models.common.Belongings
import com.cramsan.stranded.lib.game.models.common.Card
import com.cramsan.stranded.lib.game.models.common.Food
import com.cramsan.stranded.lib.game.models.common.UsableItem
import com.cramsan.stranded.lib.game.models.crafting.Basket
import com.cramsan.stranded.lib.game.models.crafting.Craftable
import com.cramsan.stranded.lib.game.models.crafting.Fire
import com.cramsan.stranded.lib.game.models.crafting.Shelter
import com.cramsan.stranded.lib.game.models.crafting.Spear
import com.cramsan.stranded.lib.game.models.night.NightEvent
import com.cramsan.stranded.lib.game.models.scavenge.Resource
import com.cramsan.stranded.lib.game.models.scavenge.ResourceType
import com.cramsan.stranded.lib.game.models.scavenge.ScavengeResult
import com.cramsan.stranded.lib.game.models.scavenge.Useless
import com.cramsan.stranded.lib.game.models.state.CancellableByFire
import com.cramsan.stranded.lib.game.models.state.CancellableByFood
import com.cramsan.stranded.lib.game.models.state.CancellableByWeapon
import com.cramsan.stranded.lib.game.models.state.Change
import com.cramsan.stranded.lib.game.models.state.CraftCard
import com.cramsan.stranded.lib.game.models.state.DamageToDo
import com.cramsan.stranded.lib.game.models.state.DestroyShelter
import com.cramsan.stranded.lib.game.models.state.DrawBelongingCard
import com.cramsan.stranded.lib.game.models.state.DrawNightCard
import com.cramsan.stranded.lib.game.models.state.DrawScavengeCard
import com.cramsan.stranded.lib.game.models.state.FiberLost
import com.cramsan.stranded.lib.game.models.state.FireModification
import com.cramsan.stranded.lib.game.models.state.FireUnavailableTomorrow
import com.cramsan.stranded.lib.game.models.state.ForageCardLost
import com.cramsan.stranded.lib.game.models.state.IncrementNight
import com.cramsan.stranded.lib.game.models.state.MultiHealthChange
import com.cramsan.stranded.lib.game.models.state.SelectTargetOnlyUnsheltered
import com.cramsan.stranded.lib.game.models.state.SelectTargetQuantity
import com.cramsan.stranded.lib.game.models.state.SelectTargetQuantityAll
import com.cramsan.stranded.lib.game.models.state.SetPhase
import com.cramsan.stranded.lib.game.models.state.SingleHealthChange
import com.cramsan.stranded.lib.game.models.state.Survived
import com.cramsan.stranded.lib.game.models.state.UserCard

/**
 * This is the only function to apply changes to [MutableGameState].
 */
internal fun MutableGameState.processEvent(change: Change, eventHandler: GameEventHandler? = null) {
    when (change) {
        CancellableByFire -> Unit
        DestroyShelter -> {
            shelters.forEach { shelter -> shelter.clearPlayers() }
            shelters.clear()
        }
        is CancellableByFood -> {
            TODO()
        }
        FireUnavailableTomorrow -> {
            isFireBlocked = true
        }
        SelectTargetQuantityAll -> {
            targetList = gamePlayers.getNext(gamePlayers.size)
        }
        is SelectTargetOnlyUnsheltered -> TODO()
        is SelectTargetQuantity -> {
            targetList = gamePlayers.getNext(change.affectedPlayers)
        }
        is CancellableByWeapon -> TODO()
        is ForageCardLost -> TODO()
        FiberLost -> TODO()
        is FireModification -> {
            if (hasFire) {
                fireDamageMod = change.change
            }
        }
        is SingleHealthChange -> {
            val damage = change.healthChange + fireDamageMod
            val player = getPlayer(change.playerId)
            player.changeHealth(damage, eventHandler)
        }
        is MultiHealthChange -> {
            TODO()
        }
        is DamageToDo -> TODO()
        /*
        is AllHealthChange -> {
            val damage = change.healthChange + fireDamageMod

            targetList?.forEach { player ->
                player.changeHealth(damage, eventHandler)
            }
        }
         */
        is DrawBelongingCard -> {
            val player = getPlayer(change.playerId)
            val card = drawBelongingCard()
            player.receiveCard(card, eventHandler)
        }
        IncrementNight -> {
            gamePlayers.forEach {
                it.getFood().forEach {
                    it.itemOnNightCompleted()
                }
            }
            night++
        }
        DrawNightCard -> {
            drawNightCard()
        }
        is DrawScavengeCard -> {
            val player = getPlayer(change.playerId)
            val card = drawScavengeCard()
            player.receiveCard(card, eventHandler)
        }
        Survived -> TODO()
        is SetPhase -> phase = change.gamePhase
        is UserCard -> {
            val player = getPlayer(change.playerId)

            val card = getCard(player, change.cardId)
            when (card) {
                is Food -> {
                    player.changeHealth(card.healthModifier, eventHandler)
                    card.itemUsed()
                    if (card.remainingUses <= 0) {
                        player.releaseCard(card, eventHandler)
                    }
                }
                is Useless -> {
                    player.releaseCard(card, eventHandler)
                }
            }
        }
        is CraftCard -> {
            val player = getPlayer(change.playerId)

            val cardsToLose = change.targetList.map { target ->
                getScavengeResultCard(player, target) as Resource
            }

            val requirementsMet = when (change.craftable) {
                is Basket -> TODO()
                is Fire -> TODO()
                is Shelter -> TODO()
                is Spear -> {
                    cardsToLose.getResourceCard(ResourceType.ROCK)
                    cardsToLose.getResourceCard(ResourceType.STICK)
                    true
                }
            }

            if (requirementsMet) {
                cardsToLose.forEach {
                    player.releaseCard(it, eventHandler)
                }
                player.receiveCard(change.craftable, eventHandler)
            }
        }
    }
    eventHandler?.onEventHandled(change)
}

/**
 * There are all the extension functions that can make modifications to the state. They are all private.
 */
private fun <T> List<T>.getNext(count: Int = 1): List<T> = this.subList(0, count)

private fun MutableGameState.drawNightCard(): NightEvent = drawCard(nightStack)

private fun MutableGameState.drawScavengeCard(): ScavengeResult = drawCard(scavengeStack)

private fun MutableGameState.drawBelongingCard(): Belongings = drawCard(belongingsStack)

private fun <T : Card> drawCard(stack: MutableList<T>): T {
    return stack.removeLast()
}

private fun Shelter.clearPlayers() {
    playerList.clear()
}

private fun Shelter.removePlayer(player: GamePlayer) {
    if (playerList.isEmpty()) return

    playerList.remove(player.id)
}

private fun Shelter.addPlayer(player: GamePlayer) {
    if (playerList.size >= Shelter.MAX_OCCUPANCY) return

    playerList.add(player.id)
}

private fun Food.itemOnNightCompleted() {
    remainingDays--
}

private fun UsableItem.itemUsed() {
    remainingUses--
}

private fun <T : Card> GamePlayer.releaseCard(card: T, eventHandler: GameEventHandler?): T {
    val result = when (card) {
        is Belongings -> belongings.remove(card)
        is ScavengeResult -> scavengeResults.remove(card)
        is Craftable -> craftables.remove(card)
        else -> throw IllegalArgumentException("Card type not supported")
    }
    require(result)
    eventHandler?.onCardRemoved(id, card)
    return card
}

private fun <T : Card> GamePlayer.receiveCard(card: T, eventHandler: GameEventHandler?) {
    when (card) {
        is Belongings -> belongings.add(card)
        is ScavengeResult -> scavengeResults.add(card)
        is Craftable -> craftables.add(card)
    }
    eventHandler?.onCardReceived(id, card)
}

private fun GamePlayer.changeHealth(damage: Int, eventHandler: GameEventHandler?) {
    health += damage
    eventHandler?.onPlayerHealthChange(id, health)
}

/**
 * These are the extensions functions to read from the [GameState]. These can be public as they do not make modifications.
 */

fun GameState.getPlayer(playerId: String): GamePlayer {
    return gamePlayers.find { it.id == playerId }!!
}

fun getCard(player: GamePlayer, cardId: String): Card {
    val card = player.scavengeResults.find { it.id == cardId }
        ?: player.belongings.find { it.id == cardId }
        ?: player.craftables.find { it.id == cardId }
    return requireNotNull(card)
}

fun getScavengeResultCard(player: GamePlayer, cardId: String): Card {
    return requireNotNull(player.scavengeResults.find { it.id == cardId })
}

fun List<ScavengeResult>.getResources(): List<Resource> {
    return filter { it is Resource }.map { it as Resource }
}

fun List<ScavengeResult>.getResourceCard(resourceType: ResourceType): Resource {
    return requireNotNull(getResources().find { it.resourceType == resourceType })
}

fun GamePlayer.getFood(): List<Food> {
    val allFood = mutableListOf<Food>()

    allFood += scavengeResults.filter { it is Food }.map { it as Food }
    allFood += belongings.filter { it is Food }.map { it as Food }
    allFood += craftables.filter { it is Food }.map { it as Food }

    return allFood
}
