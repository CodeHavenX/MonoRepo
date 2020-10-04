package com.cramsan.awslib.entitymanager.implementation

import com.cramsan.awslib.ai.`interface`.AIRepo
import com.cramsan.awslib.entity.CharacterInterface
import com.cramsan.awslib.entity.ItemInterface
import com.cramsan.awslib.entity.implementation.ConsumableItem
import com.cramsan.awslib.entity.implementation.ConsumableType
import com.cramsan.awslib.entity.implementation.EquippableItem
import com.cramsan.awslib.entity.implementation.KeyItem
import com.cramsan.awslib.entity.implementation.Player
import com.cramsan.awslib.entitymanager.EntityManagerEventListener
import com.cramsan.awslib.entitymanager.EntityManagerInteractionReceiver
import com.cramsan.awslib.entitymanager.EntityManagerInterface
import com.cramsan.awslib.enums.Direction
import com.cramsan.awslib.enums.TerrainType
import com.cramsan.awslib.enums.TurnActionType
import com.cramsan.awslib.eventsystem.CharacterTrigger
import com.cramsan.awslib.eventsystem.events.BaseEvent
import com.cramsan.awslib.eventsystem.events.ChangeTriggerEvent
import com.cramsan.awslib.eventsystem.events.EventType
import com.cramsan.awslib.eventsystem.events.InteractiveEvent
import com.cramsan.awslib.eventsystem.events.InteractiveEventOption
import com.cramsan.awslib.eventsystem.events.NoopEvent
import com.cramsan.awslib.eventsystem.events.SwapCharacterEvent
import com.cramsan.awslib.eventsystem.triggers.CellTrigger
import com.cramsan.awslib.eventsystem.triggers.Trigger
import com.cramsan.awslib.map.GameMap
import com.cramsan.awslib.scene.SceneEventsCallback
import com.cramsan.awslib.utils.constants.InitialValues
import com.cramsan.framework.logging.EventLoggerInterface
import kotlinx.coroutines.channels.Channel

class EntityManager(
    private val map: GameMap,
    private val triggerList: List<Trigger>,
    private val eventList: List<BaseEvent>,
    private val itemList: List<ItemInterface>,
    private var eventListener: EntityManagerEventListener?,
    private val log: EventLoggerInterface,
    private val aiRepo: AIRepo
) : EntityManagerInterface, EntityManagerInteractionReceiver {

    val entityTriggerMap: HashMap<String, CharacterTrigger> = HashMap()

    var itemMap: Array<Array<ItemInterface?>> = Array(map.width) { arrayOfNulls<ItemInterface?>(map.height) }
    val itemSet = mutableSetOf<ItemInterface>()

    var triggerMap: Array<Array<CellTrigger?>> = Array(map.width) { arrayOfNulls<CellTrigger?>(map.height) }
    val triggerIdMap: HashMap<String, Trigger> = HashMap()

    val eventMap: HashMap<String, BaseEvent> = HashMap()
    var nextEvent: BaseEvent? = null
    val channel = Channel<BaseEvent>()
    private val tag = "EntityManager"

    init {
        triggerList.forEach {
            log.d(tag, "Trigger: $it")

            triggerIdMap.put(it.id, it)
            if (it is CharacterTrigger) {
                entityTriggerMap.put(it.targetId, it)
            } else if (it is CellTrigger) {
                triggerMap[it.posX][it.posY] = it
            }
        }
        eventList.forEach {
            log.d(tag, "Event: $it")
            eventMap.put(it.id, it)
        }
        itemList.forEach {
            log.d(tag, "Item: $it")
            registerItem(it)
        }
    }

    val characterSet = mutableSetOf<CharacterInterface>()
    val disabledEntitySet = mutableSetOf<CharacterInterface>()

    val queue = mutableListOf<CharacterInterface>()

    val removeSet = mutableSetOf<CharacterInterface>()
    val addSet = mutableSetOf<CharacterInterface>()

    var entityMap: Array<Array<CharacterInterface?>> = Array(map.width) { arrayOfNulls<CharacterInterface?>(map.height) }
    var entityIdMap: MutableMap<String, CharacterInterface> = mutableMapOf()

    internal fun register(entity: CharacterInterface): Boolean {
        if (!entity.enabled) {
            disabledEntitySet.add(entity)
            return true
        }

        if (isBlocked(entity.posX, entity.posY)) {
            throw RuntimeException("Location blocked. Cannot register")
        }

        if (characterSet.contains(entity)) {
            throw RuntimeException("Entity already registered")
        }

        if (entityIdMap.containsKey(entity.id)) {
            throw RuntimeException("Entity with Id already registered")
        }

        characterSet.add(entity)
        entityIdMap[entity.id] = entity
        queue.add(entity)
        setPosition(entity, entity.posX, entity.posY)
        return true
    }

    internal fun deregister(entity: CharacterInterface): Boolean {
        if (!characterSet.contains(entity)) {
            return false
        }
        entityIdMap.remove(entity.id)
        queue.remove(entity)
        entityMap[entity.posX][entity.posY] = null
        characterSet.remove(entity)
        disabledEntitySet.add(entity)
        entity.enabled = false
        return true
    }

    internal fun setEntityState(entity: CharacterInterface, enabled: Boolean) {
        if (enabled) {
            prepareForAdd(entity)
        } else {
            prepareForRemove(entity)
        }
    }

    internal fun registerItem(item: ItemInterface) {
        val overlappingItem = getItem(item.posX, item.posY)

        if (overlappingItem != null) {
            throw RuntimeException("Entity already registered at location: $item")
        }

        setItem(item)
    }

    override suspend fun runTurns(callback: SceneEventsCallback?) {
        queue.forEach {
            log.i(tag, "Entity: $it")
            if (it.group != InitialValues.GROUP_PLAYER) {
                if (it.shouldMove) {
                    it.nextTurnAction = aiRepo.getNextTurnAction(it, this@EntityManager, map)
                } else {
                    it.nextTurnAction = TurnAction.NOOP
                }
            }
            if (it.nextTurnAction.turnActionType == TurnActionType.MOVE) {
                move(it, callback)
            } else if (it.nextTurnAction.turnActionType == TurnActionType.ATTACK) {
                act(it, callback)
            } else if (it.nextTurnAction.turnActionType == TurnActionType.NONE) {
                log.i(tag, "Noop action")
            } else {
                TODO("Unexpected TUrnActionType")
            }
        }
    }

    private suspend fun move(entity: CharacterInterface, callback: SceneEventsCallback?): Boolean {
        log.i(tag, "Moving")
        var x = entity.posX
        var y = entity.posY

        entity.nextTurnAction.direction.apply {
            when (this) {
                Direction.NORTH -> {
                    y--
                }
                Direction.SOUTH -> {
                    y++
                }
                Direction.WEST -> {
                    x--
                }
                Direction.EAST -> {
                    x++
                }
                Direction.KEEP -> {
                }
            }
        }
        log.d(tag, "Trying to move from x:${entity.posX},y:${entity.posY} -> x:$x,y:$y")

        if (map.isBlocked(x, y)) {
            log.d(tag, "Map location is blocked with ${map.cellAt(x, y)}")
            return false
        }

        if (isBlocked(x, y)) {
            log.d(tag, "Map location is blocked with ${getEntity(x, y)}")
            return false
        }

        setPosition(entity, x, y)
        if (entity is Player) {
            pickUpItem(entity, x, y)
            doTileAction(x, y, callback)
        }
        entity.nextTurnAction = TurnAction.NOOP

        callback?.onEntityChanged(entity)

        // TODO: Game end should be done as a trigger
        if (map.cellAt(x, y).terrain == TerrainType.END) {
            callback?.onSceneEnded(true)
        }

        return true
    }

    private suspend fun act(entity: CharacterInterface, callback: SceneEventsCallback?): Boolean {
        log.i(tag, "Performing action")
        var x = entity.posX
        var y = entity.posY

        val direction = if (entity.nextTurnAction.direction == Direction.KEEP) {
            entity.heading
        } else {
            entity.nextTurnAction.direction
        }

        when (direction) {
            Direction.NORTH -> {
                y--
            }
            Direction.SOUTH -> {
                y++
            }
            Direction.WEST -> {
                x--
            }
            Direction.EAST -> {
                x++
            }
            else -> {
                TODO()
            }
        }
        log.d(tag, "Trying to target from x:${entity.posX},y:${entity.posY} -> x:$x,y:$y")

        val targetEntity = getEntity(x, y)
        if (targetEntity != null) {
            log.d(tag, "Target entity: $targetEntity")
            if (targetEntity.group != entity.group) {
                doDamage(entity, targetEntity, callback)
            } else {
                doAction(targetEntity, callback)
            }
        } else {
            val cell = map.cellAt(x, y)
            log.d(tag, "Target cell: $cell")
            cell.onActionTaken()
            callback?.onCellChanged(cell)
        }

        entity.nextTurnAction = TurnAction.NOOP
        entity.heading = direction
        return true
    }

    internal fun setPosition(entity: CharacterInterface, poxX: Int, posY: Int): Boolean {
        if (isBlocked(poxX, posY)) {
            return false
        }
        entityMap[entity.posX][entity.posY] = null
        entityMap[poxX][posY] = entity
        entity.posX = poxX
        entity.posY = posY
        return true
    }

    internal fun prepareForAdd(entity: CharacterInterface) {
        entity.enabled = true
        addSet.add(entity)
    }

    internal fun prepareForRemove(entity: CharacterInterface) {
        entity.enabled = false
        removeSet.add(entity)
        entityIdMap.remove(entity.id)
        entityMap[entity.posX][entity.posY] = null
        characterSet.remove(entity)
    }

    internal fun doDamage(attacker: CharacterInterface, defender: CharacterInterface, callback: SceneEventsCallback?) {
        log.i(tag, "Performing damage")
        defender.health -= attacker.attack
        if (defender.health <= 0) {
            log.i(tag, "Entity is dead: $defender")
            defender.health = -1
            prepareForRemove(defender)
            callback?.onEntityChanged(defender)
        }
    }

    private suspend fun doAction(receiver: CharacterInterface, callback: SceneEventsCallback?) {
        val trigger = entityTriggerMap.getValue(receiver.id)
        log.i(tag, "Performing action to trigger: $trigger")
        if (!trigger.enabled) {
            log.i(tag, "Trigger disabled")
            return
        }

        executeTrigger(trigger, callback)
    }

    private fun pickUpItem(player: Player, posX: Int, posY: Int) {
        val item = getItem(posX, posY) ?: return

        // TODO: Perform logic when getting item
        when (item) {
            is ConsumableItem -> {
                when (item.type) {
                    ConsumableType.HEALTH -> player.health += item.ammount
                    ConsumableType.ARMOR -> TODO()
                    ConsumableType.CREDIT -> TODO()
                }
            }
            is KeyItem -> {
                player.keyItemList.add(item)
            }
            is EquippableItem -> {
                player.equipableItemList.add(item)
            }
            else -> {
                TODO("Invalid item")
            }
        }
        clearItem(item)
    }

    private suspend fun doTileAction(posX: Int, posY: Int, callback: SceneEventsCallback?) {
        val trigger = triggerMap[posX][posY]
        if (trigger == null || !trigger.enabled) {
            return
        }

        log.i(tag, "Executing trigger: $trigger")
        executeTrigger(trigger, callback)
    }

    internal fun handleSwapEntityEvent(event: SwapCharacterEvent, callback: SceneEventsCallback?): BaseEvent {
        val disableId = event.disableId
        val enableId = event.enableId

        val toDisableEntity = entityIdMap[disableId]
        var toEnableEntity: CharacterInterface? = null
        toDisableEntity?.let {
            setEntityState(it, false)
        }
        disabledEntitySet.find { it.id == enableId }?.let {
            setEntityState(it, true)
            toEnableEntity = it
        }
        if (toEnableEntity != null && toDisableEntity != null) {
            toEnableEntity!!.posX = toDisableEntity.posX
            toEnableEntity!!.posY = toDisableEntity.posY
        }
        toDisableEntity?.let { callback?.onEntityChanged(it) }
        toEnableEntity?.let { callback?.onEntityChanged(it) }
        return if (event.nextEventId == InitialValues.NOOP_ID) {
            NoopEvent()
        } else {
            eventMap.getValue(event.nextEventId)
        }
    }

    private suspend fun handleInteractiveEntityEvent(event: InteractiveEvent): BaseEvent {
        log.i(tag, "Handling Interactive Event: $event")
        log.d(tag, "Text: ${event.text}")
        event.options.forEach {
            log.d(tag, "Option: $it")
        }

        // TODO: Shouldn't this be a non-nullable?
        log.i(tag, "Calling Callback: $eventListener")
        eventListener?.onInteractionRequired(event.text, event.options, this@EntityManager)

        log.d(tag, "Waiting for response from Event: $event")
        val receivedEvent = channel.receive()
        log.d(tag, "Got response from Event: $event")
        log.i(tag, "Received Event: $receivedEvent")
        return if (receivedEvent.type == EventType.NOOP) {
            NoopEvent()
        } else {
            receivedEvent
        }
    }

    internal fun handleChangeTriggerEvent(event: ChangeTriggerEvent): BaseEvent {
        val disableId = event.disableId
        val enableId = event.enableId

        triggerIdMap[enableId]?.enabled = true
        triggerIdMap[disableId]?.enabled = false

        return if (event.nextEventId == InitialValues.NOOP_ID) {
            NoopEvent()
        } else {
            eventMap.getValue(event.nextEventId)
        }
    }

    private suspend fun executeTrigger(trigger: Trigger, callback: SceneEventsCallback?) {
        if (nextEvent != null) {
            throw RuntimeException("Cannot overwrite event")
        }

        nextEvent = eventMap[trigger.eventId]
        while (nextEvent != null) {
            var localNextEvent = nextEvent
            log.i(tag, "Event: $localNextEvent")
            when (localNextEvent) {
                is SwapCharacterEvent -> {
                    log.d(tag, "Swap event")
                    nextEvent = handleSwapEntityEvent(localNextEvent, callback)
                }
                is InteractiveEvent -> {
                    log.d(tag, "Interactive event")
                    nextEvent = handleInteractiveEntityEvent(localNextEvent)
                }
                is ChangeTriggerEvent -> {
                    log.d(tag, "Change trigger event")
                    nextEvent = handleChangeTriggerEvent(localNextEvent)
                }
                is NoopEvent -> {
                    log.i(tag, "Noop event")
                    nextEvent = null
                }
                else -> {
                    TODO("Unexpected Event type")
                }
            }
        }
    }
    override suspend fun selectOption(option: InteractiveEventOption?) {
        log.i(tag, "Selection: $option")
        if (option == null) {
            channel.send(NoopEvent())
            return
        }

        if (option.eventId == InitialValues.INVALID_ID) {
            TODO()
        }

        if (option.eventId == InitialValues.NOOP_ID) {
            log.i(tag, "SelectIotuin INVALID")
            channel.send(NoopEvent())
            return
        }

        val eEvent = eventMap.getValue(option.eventId)
        log.i(tag, "Event: $eEvent")
        channel.send(eEvent)
    }

    internal fun processGameEntityState() {
        removeSet.forEach {
            deregister(it)
        }
        removeSet.clear()

        addSet.forEach {
            register(it)
        }
        addSet.clear()
    }

    internal fun getEntity(posX: Int, posY: Int): CharacterInterface? {
        return entityMap[posX][posY]
    }

    internal fun clearItem(item: ItemInterface) {
        if (getEntity(item.posX, item.posY) == null) {
            TODO("Trying to clear an already empty item")
        }
        itemSet.remove(item)
        itemMap[item.posX][item.posY] = null
    }

    internal fun setItem(item: ItemInterface) {
        if (getItem(item.posX, item.posY) != null) {
            TODO("Trying to overwrite an existing item")
        }
        itemSet.add(item)
        itemMap[item.posX][item.posY] = item
    }

    internal fun getItem(posX: Int, posY: Int): ItemInterface? {
        return itemMap[posX][posY]
    }

    internal fun isBlocked(posX: Int, posY: Int): Boolean {
        if (posX < 0 || posX > entityMap.lastIndex || posY < 0 || posY > entityMap.first().lastIndex)
            return true

        return entityMap[posX][posY] != null
    }
}
