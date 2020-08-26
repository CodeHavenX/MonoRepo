package com.cramsan.awslib.eventsystem.triggers

import com.cramsan.awslib.dsl.scene
import com.cramsan.awslib.entitymanager.implementation.EntityManager
import com.cramsan.awslib.entitymanager.implementation.TurnAction
import com.cramsan.awslib.enums.Direction
import com.cramsan.awslib.enums.EntityType
import com.cramsan.awslib.enums.TurnActionType
import com.cramsan.awslib.map.GameMap
import com.cramsan.awslib.platform.runTest
import com.cramsan.awslib.scene.Scene
import com.cramsan.awslib.utils.constants.InitialValues
import com.cramsan.awslib.utils.map.MapGenerator
import com.cramsan.framework.assert.AssertUtilInterface
import com.cramsan.framework.halt.HaltUtilInterface
import com.cramsan.framework.logging.EventLoggerInterface
import io.mockk.mockk
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CellTriggerTests {

    lateinit var kodein: DI

    @BeforeTest
    fun prepareTest() {
        val log = mockk<EventLoggerInterface>(relaxed = true)
        val assert = mockk<AssertUtilInterface>(relaxed = true)
        val halt = mockk<HaltUtilInterface>(relaxed = true)
        kodein = DI {
            bind() from singleton { log }
            bind() from singleton { assert }
            bind() from singleton { halt }
        }
    }

    /**
     * Test GameEntityTrigger
     */
    @Test
    fun gameEntityTriggerWithSwapEventTest() = runTest {
        val map = GameMap(MapGenerator.createMap100x100())

        val sceneConfig = scene {
            player {
                posX = 5
                posY = 5
            }
            entities {
                scientist {
                    id = 1
                    group = 0
                    posX = 5
                    posY = 6
                }
                dog {
                    id = 2
                    posX = 4
                    posY = 9
                    enabled = false
                }
            }
            triggers {
                cell {
                    id = 523
                    eventId = 352
                    enabled = true
                    posX = 5
                    posY = 4
                }
            }
            events {
                swapEntity {
                    id = 352
                    enableEntityId = 2
                    disableEntityId = 1
                    nextEventId = InitialValues.NOOP_ID
                }
            }
        }
        assertNotNull(sceneConfig)
        val entityManager = EntityManager(map, sceneConfig.triggerList, sceneConfig.eventList, sceneConfig.itemList, null, kodein)
        val player = sceneConfig.player

        val scene = Scene(entityManager, sceneConfig, kodein)
        scene.loadScene()

        scene.runTurn(TurnAction(TurnActionType.MOVE, Direction.NORTH))
        assertEquals(5, player.posX)
        assertEquals(4, player.posY)
        scene.runTurn(TurnAction(TurnActionType.ATTACK, Direction.KEEP))
        val enemy = entityManager.getEntity(5, 5)
        assertNotNull(enemy)
        assertEquals(enemy.type, EntityType.DOG)
    }
}
