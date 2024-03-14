@file:OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class)
package com.cramsan.minesweepers.common.ui

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 *
 */
@Suppress("TooManyFunctions")
object Assets {
    private lateinit var _lcdNumberDash: DrawableResource
    private lateinit var _lcdNumberNone: DrawableResource
    private lateinit var _lcdNumberOne: DrawableResource
    private lateinit var _lcdNumberTwo: DrawableResource
    private lateinit var _lcdNumberThree: DrawableResource
    private lateinit var _lcdNumberFour: DrawableResource
    private lateinit var _lcdNumberFive: DrawableResource
    private lateinit var _lcdNumberSix: DrawableResource
    private lateinit var _lcdNumberSeven: DrawableResource
    private lateinit var _lcdNumberEight: DrawableResource
    private lateinit var _lcdNumberNine: DrawableResource
    private lateinit var _lcdNumberZero: DrawableResource
    private lateinit var _tile: DrawableResource
    private lateinit var _tileFlagged: DrawableResource
    private lateinit var _pressedTile: DrawableResource
    private lateinit var _pressedTileBomb: DrawableResource
    private lateinit var _pressedTileBombRed: DrawableResource
    private lateinit var _pressedTileOne: DrawableResource
    private lateinit var _pressedTileTwo: DrawableResource
    private lateinit var _pressedTileThree: DrawableResource
    private lateinit var _pressedTileFour: DrawableResource
    private lateinit var _pressedTileFive: DrawableResource
    private lateinit var _pressedTileSix: DrawableResource
    private lateinit var _pressedTileSeven: DrawableResource
    private lateinit var _pressedTileEight: DrawableResource
    private lateinit var _buttonDead: DrawableResource
    private lateinit var _buttonNormal: DrawableResource
    private lateinit var _buttonPressed: DrawableResource
    private lateinit var _buttonWon: DrawableResource
    private lateinit var _buttonWorried: DrawableResource
    private lateinit var _arrowUp: DrawableResource
    private lateinit var _arrowDown: DrawableResource
    private lateinit var _arrowLeft: DrawableResource
    private lateinit var _arrowRight: DrawableResource

    /**
     *
     */
    @OptIn(ExperimentalResourceApi::class)
    fun loadAssets() {
        _lcdNumberDash = DrawableResource("dash.png")
        _lcdNumberNone = DrawableResource("none.png")
        _lcdNumberOne = DrawableResource("one.png")
        _lcdNumberTwo = DrawableResource("two.png")
        _lcdNumberThree = DrawableResource("three.png")
        _lcdNumberFour = DrawableResource("four.png")
        _lcdNumberFive = DrawableResource("five.png")
        _lcdNumberSix = DrawableResource("six.png")
        _lcdNumberSeven = DrawableResource("seven.png")
        _lcdNumberEight = DrawableResource("eight.png")
        _lcdNumberNine = DrawableResource("nine.png")
        _lcdNumberZero = DrawableResource("zero.png")
        _tile = DrawableResource("tile.png")
        _tileFlagged = DrawableResource("tile_flagged.png")
        _pressedTile = DrawableResource("tile_pressed.png")
        _pressedTileBomb = DrawableResource("tile_pressed_bomb.png")
        _pressedTileBombRed = DrawableResource("tile_pressed_bomb_exploded.png")
        _pressedTileOne = DrawableResource("tile_pressed_one.png")
        _pressedTileTwo = DrawableResource("tile_pressed_two.png")
        _pressedTileThree = DrawableResource("tile_pressed_three.png")
        _pressedTileFour = DrawableResource("tile_pressed_four.png")
        _pressedTileFive = DrawableResource("tile_pressed_five.png")
        _pressedTileSix = DrawableResource("tile_pressed_six.png")
        _pressedTileSeven = DrawableResource("tile_pressed_seven.png")
        _pressedTileEight = DrawableResource("tile_pressed_eight.png")
        _buttonDead = DrawableResource("button_dead.png")
        _buttonNormal = DrawableResource("button_normal.png")
        _buttonPressed = DrawableResource("button_pressed.png")
        _buttonWon = DrawableResource("button_won.png")
        _buttonWorried = DrawableResource("button_worried.png")
        _arrowUp = DrawableResource("arrow_up.png")
        _arrowDown = DrawableResource("arrow_down.png")
        _arrowLeft = DrawableResource("arrow_left.png")
        _arrowRight = DrawableResource("arrow_right.png")
    }

    internal fun lcdNumberDash() = _lcdNumberDash
    internal fun lcdNumberNone() = _lcdNumberNone
    internal fun lcdNumberOne() = _lcdNumberOne
    internal fun lcdNumberTwo() = _lcdNumberTwo
    internal fun lcdNumberThree() = _lcdNumberThree
    internal fun lcdNumberFour() = _lcdNumberFour
    internal fun lcdNumberFive() = _lcdNumberFive
    internal fun lcdNumberSix() = _lcdNumberSix
    internal fun lcdNumberSeven() = _lcdNumberSeven
    internal fun lcdNumberEight() = _lcdNumberEight
    internal fun lcdNumberNine() = _lcdNumberNine
    internal fun lcdNumberZero() = _lcdNumberZero
    internal fun tile() = _tile
    internal fun tileFlagged() = _tileFlagged
    internal fun pressedTile() = _pressedTile
    internal fun pressedTileBomb() = _pressedTileBomb
    internal fun pressedTileBombRed() = _pressedTileBombRed
    internal fun pressedTileOne() = _pressedTileOne
    internal fun pressedTileTwo() = _pressedTileTwo
    internal fun pressedTileThree() = _pressedTileThree
    internal fun pressedTileFour() = _pressedTileFour
    internal fun pressedTileFive() = _pressedTileFive
    internal fun pressedTileSix() = _pressedTileSix
    internal fun pressedTileSeven() = _pressedTileSeven
    internal fun pressedTileEight() = _pressedTileEight
    internal fun buttonDead() = _buttonDead
    internal fun buttonNormal() = _buttonNormal
    internal fun buttonPressed() = _buttonPressed
    internal fun buttonWon() = _buttonWon
    internal fun buttonWorried() = _buttonWorried
    internal fun arrowUp() = _arrowUp
    internal fun arrowDown() = _arrowDown
    internal fun arrowLeft() = _arrowLeft
    internal fun arrowRight() = _arrowRight
}
