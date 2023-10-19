package com.cramsan.runasimi.mpplib

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.runasimi.mpplib.ui.Card
import com.cramsan.runasimi.mpplib.ui.CardUiModel
import com.cramsan.runasimi.mpplib.ui.ComponentColor
import com.cramsan.runasimi.mpplib.ui.theme.RunasimiTheme

@Preview
@Composable
fun PreviewCardDark() {
    RunasimiTheme(true) {
        Card(
            CardUiModel(
                sentence = listOf(
                    CardUiModel.Word(
                        listOf(
                            CardUiModel.Segment(
                                segment = "Qamkuna",
                                color = ComponentColor.BLUE,
                            )
                        )
                    ),
                    CardUiModel.Word(
                        listOf(
                            CardUiModel.Segment(
                                segment = "tinku",
                                color = ComponentColor.RED,
                            ),
                            CardUiModel.Segment(
                                segment = "chka",
                                color = ComponentColor.BLUE,
                            ),
                            CardUiModel.Segment(
                                segment = "rqa",
                                color = ComponentColor.YELLOW,
                            ),
                            CardUiModel.Segment(
                                segment = "nkichik",
                                color = ComponentColor.RED,
                            ),
                        )
                    ),
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
                        color = ComponentColor.RED,
                    ),
                )
            ),
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
            CardUiModel(
                sentence = listOf(
                    CardUiModel.Word(
                        listOf(
                            CardUiModel.Segment(
                                segment = "Qamkuna",
                                color = ComponentColor.BLUE,
                            )
                        )
                    ),
                    CardUiModel.Word(
                        listOf(
                            CardUiModel.Segment(
                                segment = "tinku",
                                color = ComponentColor.RED,
                            ),
                            CardUiModel.Segment(
                                segment = "chka",
                                color = ComponentColor.BLUE,
                            ),
                            CardUiModel.Segment(
                                segment = "rqa",
                                color = ComponentColor.YELLOW,
                            ),
                            CardUiModel.Segment(
                                segment = "nkichik",
                                color = ComponentColor.RED,
                            ),
                        )
                    ),
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
                        color = ComponentColor.RED,
                    ),
                )
            ),
            true,
            Modifier.padding(10.dp)
        )
    }
}
