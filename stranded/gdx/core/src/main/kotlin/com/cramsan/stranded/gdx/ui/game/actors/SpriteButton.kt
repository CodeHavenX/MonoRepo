package com.cramsan.stranded.gdx.ui.game.actors

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.cramsan.stranded.gdx.ui.Theme

class SpriteButton(
    textureRegion: TextureRegion,
    baseWidth: Float = 32F,
    baseHeight: Float = 32F,
    private val onClick: () -> Unit
) : SpriteActor(textureRegion) {

    init {
        addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                addAction(Actions.scaleTo(1.1F, 1.1F, Theme.Transtion.xxfast, Interpolation.linear))

                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                addAction(Actions.scaleTo(1F, 1F, Theme.Transtion.xxfast, Interpolation.linear))

                super.touchUp(event, x, y, pointer, button)
            }
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        width = baseWidth
        height = baseHeight
        setOrigin(Align.center)
    }
}