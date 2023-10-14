package com.cramsan.minesweepers.common.main

import com.cramsan.minesweepers.common.ModalityTense
import com.cramsan.minesweepers.common.Pronoun
import com.cramsan.minesweepers.common.PronounPastSuffix
import com.cramsan.minesweepers.common.PronounPresentSuffix
import com.cramsan.minesweepers.common.Statement
import com.cramsan.minesweepers.common.StatementManager
import com.cramsan.minesweepers.common.TimeTense
import com.cramsan.minesweepers.common.ui.CardUiModel
import com.cramsan.minesweepers.common.ui.ComponentColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val statementManager: StatementManager,
)  {

    private val _uiModel = MutableStateFlow<List<CardUiModel>?>(null)
    val uiModel = _uiModel.asStateFlow()

    private var initialSeed = 0

    suspend fun setSeed(newSeed: Int) {
        initialSeed = newSeed
        loadCards()
    }

    private suspend fun loadCards() {
        _uiModel.update { null }
        _uiModel.update {
            (0 until PAGE_COUNT).map {
                val statement = statementManager.generateStatement(initialSeed + it)
                statement.toUIModel()
            }
        }
    }

    companion object {
        const val PAGE_COUNT = 25
    }
}

fun Statement.toUIModel(): CardUiModel {

    val conjugationPrefix = pronounConjugationPrefix(timeTense, pronoun)

    val modalityComponent = if (modalityTense == ModalityTense.SIMPLE) {
        null
    } else {
        CardUiModel.Component(
            type = "Modalidad: ",
            meaning = modalityTense.meaning,
            quechua = modalityTense.suffix,
            color = ComponentColor.RED,
        )
    }

    val timeString = timeTense.suffix.joinToString("/").let {
        if (it.isBlank()) {
            "-"
        } else {
            it
        }
    }
    val timeComponent = CardUiModel.Component(
        type = "Tiempo: ",
        meaning = timeTense.meaning,
        quechua = timeString,
        color = ComponentColor.YELLOW,
    )

    return CardUiModel(
        sentence = listOf(
            CardUiModel.Word(
                listOf(
                    CardUiModel.Segment(
                        segment = pronoun.pronoun,
                        color = ComponentColor.BLUE,
                    )
                )
            ),
            CardUiModel.Word(
                listOf(
                    CardUiModel.Segment(
                        segment = verb.root.first(),
                        color = null,
                    ),
                    CardUiModel.Segment(
                        segment = modalityTense.suffix.lowercase(),
                        color = ComponentColor.RED,
                    ),
                    CardUiModel.Segment(
                        segment = timeTense.suffix.first().lowercase(),
                        color = ComponentColor.YELLOW,
                    ),
                    CardUiModel.Segment(
                        segment = conjugationPrefix,
                        color = ComponentColor.BLUE,
                    ),
                )
            ),
        ),
        components = listOf(
            CardUiModel.Component(
                type = "Pronombre: ",
                meaning = pronoun.translation,
                quechua = pronoun.pronoun,
                color = ComponentColor.BLUE,
            ),
            CardUiModel.Component(
                type = "Verbo: ",
                meaning = verb.meaning.first(),
                quechua = verb.root.first() + "y",
                color = null,
            ),
            timeComponent,
            modalityComponent,
            CardUiModel.Component(
                type = "Sufijo: ",
                meaning = pronoun.translation,
                quechua = conjugationPrefix,
                color = ComponentColor.BLUE,
            ),
        ).filterNotNull()
    )
}

fun pronounConjugationPrefix(timeTense: TimeTense, pronoun: Pronoun): String {
    return when (timeTense) {
        TimeTense.PRESENT -> {
            PronounPresentSuffix.valueOf(pronoun.name).suffix
        }
        TimeTense.PAST -> {
            PronounPastSuffix.valueOf(pronoun.name).suffix
        }
    }
}
suspend fun <T> MutableStateFlow<T>.update(change: (T) -> T) {
    val newValue = change(value)
    emit(newValue)
}
