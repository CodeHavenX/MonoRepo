package com.cramsan.runasimi.mpplib.ui.screen.trainer

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cramsan.runasimi.mpplib.Pronoun
import com.cramsan.runasimi.mpplib.PronounPastSuffix
import com.cramsan.runasimi.mpplib.TimeTense
import com.cramsan.runasimi.mpplib.main.MainView
import com.cramsan.runasimi.mpplib.ui.CardUiModel
import com.cramsan.runasimi.mpplib.ui.ComponentColor
import com.cramsan.runasimi.mpplib.ui.MainViewUIModel
import com.cramsan.runasimi.mpplib.ui.theme.RunasimiTheme

@Preview(
    widthDp = 250,
    heightDp = 550,
)
@Composable
fun PreviewMainViewLight() {
    RunasimiTheme(false) {
        RenderMainView()
    }
}

@Preview(
    widthDp = 250,
    heightDp = 550,
)
@Composable
fun PreviewMainViewDark() {
    RunasimiTheme(true) {
        RenderMainView()
    }
}

@Composable
private fun RenderMainView() {
    MainView(
        MainViewUIModel(
            cards = listOf(
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
                            color = ComponentColor.YELLOW,
                        ),
                    )
                ),
                CardUiModel(
                    listOf(
                        CardUiModel.Word(
                            listOf(
                                CardUiModel.Segment(
                                    segment = "Qamkuna",
                                    color = ComponentColor.BLUE,
                                )
                            )
                        ),
                    ),
                    listOf()
                ),
                CardUiModel(listOf(), listOf()),
                CardUiModel(listOf(), listOf()),
            ),
            isLoading = true,
        )
    )
}
