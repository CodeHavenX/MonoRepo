package com.cramsan.runasimi.mpplib.ui

data class MainViewUIModel(
    val cards: List<CardUiModel>,
    val isLoading: Boolean,
)
data class CardUiModel(
    val sentence: List<Word>,
    val components: List<Component>,
) {
    data class Word(
        val segments: List<Segment>
    )

    data class Segment(
        val segment: String,
        val color: ComponentColor?,
    )
    data class Component(
        val type: String,
        val meaning: String,
        val quechua: String,
        val color: ComponentColor?,
    )
}
