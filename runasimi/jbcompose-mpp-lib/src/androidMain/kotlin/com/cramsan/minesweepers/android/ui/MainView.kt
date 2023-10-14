package com.cramsan.minesweepers.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cramsan.minesweepers.common.Pronoun
import com.cramsan.minesweepers.common.PronounPastSuffix
import com.cramsan.minesweepers.common.TimeTense
import com.cramsan.minesweepers.common.ui.CardUiModel
import com.cramsan.minesweepers.common.ui.ComponentColor
import com.cramsan.minesweepers.common.main.MainView
import com.cramsan.minesweepers.common.ui.theme.RunasimiTheme

@Preview(
    widthDp = 600,
    heightDp = 300,
)
@Composable
fun PreviewMainView() {
    RunasimiTheme(false) {
        MainView(cardUiModels = listOf(
            CardUiModel(
                sentence = listOf(
                    CardUiModel.Word(listOf(
                        CardUiModel.Segment(
                            segment = "Qamkuna",
                            color = ComponentColor.BLUE,
                        )
                    )),
                    CardUiModel.Word(listOf(
                        CardUiModel.Segment(
                            segment = "tinku",
                            color = ComponentColor.RED,
                        ),
                        CardUiModel.Segment(
                            segment = "chka",
                            color = ComponentColor.YELLOW,
                        ),
                        CardUiModel.Segment(
                            segment = "rqa",
                            color = ComponentColor.YELLOW,
                        ),
                        CardUiModel.Segment(
                            segment = "nkichik",
                            color = ComponentColor.BLUE,
                        ),
                    )),
                ),
                components = listOf(
                    CardUiModel.Component(
                        type = "Pronombre",
                        meaning = Pronoun.QAMKUNA.translation,
                        quechua = Pronoun.QAMKUNA.pronoun,
                        color = ComponentColor.BLUE,
                    ),
                    CardUiModel.Component(
                        type = "Verbo",
                        meaning = "Encontrarse",
                        quechua = "Tinkuy",
                        color = ComponentColor.RED,
                    ),
                    CardUiModel.Component(
                        type = "Tiempo",
                        meaning = TimeTense.PAST.meaning,
                        quechua = TimeTense.PAST.suffix.joinToString("/"),
                        color = ComponentColor.YELLOW,
                    ),
                    CardUiModel.Component(
                        type = "Conjugacion",
                        meaning = Pronoun.QAMKUNA.translation,
                        quechua = PronounPastSuffix.QAMKUNA.suffix,
                        color = ComponentColor.YELLOW,
                    ),
                )
            ),
            CardUiModel(listOf(
                CardUiModel.Word(listOf(
                    CardUiModel.Segment(
                        segment = "Qamkuna",
                        color = ComponentColor.BLUE,
                    )
                )),
            ), listOf()),
            CardUiModel(listOf(), listOf()),
            CardUiModel(listOf(), listOf()),
        ))
    }
}
