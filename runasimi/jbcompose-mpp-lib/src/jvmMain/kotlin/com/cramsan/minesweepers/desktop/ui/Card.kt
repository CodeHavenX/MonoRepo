package com.cramsan.minesweepers.desktop.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.minesweepers.common.ModalityTense
import com.cramsan.minesweepers.common.Pronoun
import com.cramsan.minesweepers.common.Statement
import com.cramsan.minesweepers.common.TimeTense
import com.cramsan.minesweepers.common.Verb
import com.cramsan.minesweepers.common.ui.Card
import com.cramsan.minesweepers.common.ui.CardEventHandler
import com.cramsan.minesweepers.common.ui.theme.RunasimiTheme

@Preview
@Composable
fun PreviewCardDark() {
    RunasimiTheme(true) {
        Card(
            Statement(
                Pronoun.QAMKUNA,
                Verb(listOf("tinku"), listOf("encontrarse")),
                ModalityTense.CONTINUOUS,
                TimeTense.PAST,
            ),
            object : CardEventHandler {
                override fun onNextPressed() = Unit
            },
            true,
            Modifier.padding(10.dp)
        )
    }
}

@Preview
@Composable
fun PreviewCardLight() {
    RunasimiTheme(false) {
        Card(
            Statement(
                Pronoun.QAMKUNA,
                Verb(listOf("tinku"), listOf("encontrarse")),
                ModalityTense.CONTINUOUS,
                TimeTense.PAST,
            ),
            object : CardEventHandler {
                override fun onNextPressed() = Unit
            },
            true,
            Modifier.padding(10.dp)
        )
    }
}