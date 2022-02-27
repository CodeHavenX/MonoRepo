package com.cramsan.stranded.testgui.game

import androidx.compose.runtime.Composable
import com.cramsan.stranded.lib.game.models.common.Belongings
import com.cramsan.stranded.lib.game.models.common.Phase
import com.cramsan.stranded.lib.game.models.crafting.Craftable
import com.cramsan.stranded.lib.game.models.crafting.Shelter
import com.cramsan.stranded.lib.game.models.scavenge.ScavengeResult
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Suppress("LongMethod", "FunctionNaming", "UNUSED_PARAMETER")
@Composable
fun GameScreen(
    name: String,
    health: Int,
    phase: Phase,
    day: Int,
    viewModel: GameViewModel? = null,
) {
    Div {
        Header(name, health)

        Phase(phase)

        Day(day)
    }
}