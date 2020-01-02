package com.cramsan.awslib

import com.cramsan.awslib.entity.GameEntityInterface
import com.cramsan.awslib.entitymanager.implementation.EntityManager
import com.cramsan.awslib.entitymanager.EntityManagerEventListener
import com.cramsan.awslib.entitymanager.EntityManagerInteractionReceiver
import com.cramsan.awslib.entitymanager.implementation.TurnAction
import com.cramsan.awslib.enums.Direction
import com.cramsan.awslib.enums.EntityType
import com.cramsan.awslib.enums.TerrainType
import com.cramsan.awslib.enums.TurnActionType
import com.cramsan.awslib.eventsystem.events.InteractiveEventOption
import com.cramsan.awslib.map.Cell
import com.cramsan.awslib.map.DoorCell
import com.cramsan.awslib.map.GameMap
import com.cramsan.awslib.scene.Scene
import com.cramsan.awslib.scene.SceneConfig
import com.cramsan.awslib.scene.SceneEventsCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import kotlin.system.exitProcess
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JOptionPane.showInputDialog
import javax.swing.JPanel

class AWTRenderer : JFrame(), EntityManagerEventListener {

    init {
        setSize(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        isAlwaysOnTop = true
    }

    fun startScene(manager: EntityManager, sceneConfig: SceneConfig, map: GameMap) {
        add(RendererCanvas(manager, map))
        val mainPlayer = sceneConfig.player
        runBlocking {
            val scene = Scene(manager, sceneConfig)
            scene.setListener(object : SceneEventsCallback {
                override fun onEntityChanged(entity: GameEntityInterface) {
                    repaint()
                }

                override fun onCellChanged(cell: Cell) {
                    repaint()
                }

                override fun onSceneEnded(completed: Boolean) {
                    exitProcess(0)
                }

            })

            this@AWTRenderer.addKeyListener(object : KeyAdapter() {
                override fun keyTyped(e: KeyEvent?) {
                    val action = when (e?.keyChar) {
                        'w' -> {
                            mainPlayer.heading = Direction.NORTH
                            TurnAction(TurnActionType.MOVE, Direction.NORTH)
                        }
                        's' -> {
                            mainPlayer.heading = Direction.SOUTH
                            TurnAction(TurnActionType.MOVE, Direction.SOUTH)
                        }
                        'a' -> {
                            mainPlayer.heading = Direction.WEST
                            TurnAction(TurnActionType.MOVE, Direction.WEST)
                        }
                        'd' -> {
                            mainPlayer.heading = Direction.EAST
                            TurnAction(TurnActionType.MOVE, Direction.EAST)
                        }
                        ' ' -> TurnAction(TurnActionType.ATTACK, Direction.KEEP)
                        else -> TurnAction(TurnActionType.NONE, Direction.KEEP)
                    }
                    GlobalScope.launch {
                        scene.runTurn(action)
                    }
                }
            })
            scene.loadScene()
        }
    }

    override fun onGameReady(eventReceiver: EntityManagerInteractionReceiver) {

    }
    override fun onTurnCompleted(eventReceiver: EntityManagerInteractionReceiver) {

    }
    override fun onInteractionRequired(text: String?, options: List<InteractiveEventOption>, eventReceiver: EntityManagerInteractionReceiver) {
        System.out.println("Options: ")
        options.forEachIndexed { index, interactiveEventOption ->
            System.out.println("$index) $interactiveEventOption")
        }

        val possibilities = Array(options.size) { i -> options[i].label }

        if (possibilities.size > 0) {
            val resultOption = showInputDialog(
                    this@AWTRenderer,
                    text,
                    "Sample interactive event",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    possibilities.first()) as String

            var selection = -1
            var selectedOption: InteractiveEventOption?

            options.forEachIndexed { index, interactiveEventOption ->
                if (interactiveEventOption.label == resultOption) {
                    selection = index
                }
            }

            selectedOption = options[selection]

            System.out.println("You selected $selectedOption")
            GlobalScope.launch {
                eventReceiver.selectOption(selectedOption)
            }
        } else {
            showInputDialog(text)
            System.out.println("Continuing")
            GlobalScope.launch {
                eventReceiver.selectOption(null)
            }
        }
    }

    internal inner class RendererCanvas(val manager: EntityManager,
                                        val map: GameMap) : JPanel() {

        override fun paint(graphics: Graphics?) {
            super.paint(graphics)
            val g = graphics as Graphics2D
            val width = map.width
            val height = map.height
            val windowsHeight = getHeight()
            val windowsWidth = getWidth()

            val cellWidth = Math.round(windowsWidth.toFloat() / width)
            val cellHeight = Math.round(windowsHeight.toFloat() / height)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    val cell = map.cellAt(x, y)

                    when (cell.terrain) {
                        TerrainType.OPEN -> g.color = Color.WHITE
                        TerrainType.WALL -> g.color = Color.BLACK
                        TerrainType.DOOR -> {
                            val doorCell = cell as DoorCell
                            if (doorCell.opened)
                                g.color = Color.CYAN
                            else
                                g.color = Color.LIGHT_GRAY
                        }
                        TerrainType.END -> g.color = Color.GREEN
                    }
                    g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight)
                }
            }

            for (entity in manager.entitySet) {
                when (entity.type) {
                    EntityType.PLAYER -> g.color = Color.BLUE
                    EntityType.DOG -> g.color = Color.RED
                    EntityType.SCIENTIST -> g.color = Color.GREEN
                }
                g.fillRect(
                    entity.posX * cellWidth,
                    entity.posY * cellHeight,
                    cellWidth,
                    cellHeight
                )
            }
        }
    }
}
