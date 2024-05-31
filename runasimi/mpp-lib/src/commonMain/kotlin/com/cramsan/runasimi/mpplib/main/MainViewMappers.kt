package com.cramsan.runasimi.mpplib.main

import com.cramsan.runasimi.mpplib.ModalityTense
import com.cramsan.runasimi.mpplib.Pronoun
import com.cramsan.runasimi.mpplib.PronounFutureSuffix
import com.cramsan.runasimi.mpplib.PronounPastSuffix
import com.cramsan.runasimi.mpplib.PronounPresentSuffix
import com.cramsan.runasimi.mpplib.Statement
import com.cramsan.runasimi.mpplib.TimeTense
import com.cramsan.runasimi.mpplib.ui.CardUiModel
import com.cramsan.runasimi.mpplib.ui.ComponentColor

@Suppress("LongMethod")
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
                    ),
                ),
            ),
            CardUiModel.Word(
                listOfNotNull(
                    CardUiModel.Segment(
                        segment = verb.root.first(),
                        color = null,
                    ),
                    CardUiModel.Segment(
                        segment = modalityTense.suffix.lowercase(),
                        color = ComponentColor.RED,
                    ),
                    timeTense.suffix.firstOrNull()?.let {
                        CardUiModel.Segment(
                            segment = it.lowercase(),
                            color = ComponentColor.YELLOW,
                        )
                    },
                    CardUiModel.Segment(
                        segment = conjugationPrefix,
                        color = ComponentColor.BLUE,
                    ),
                ),
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
        ).filterNotNull(),
    )
}

private fun pronounConjugationPrefix(timeTense: TimeTense, pronoun: Pronoun): String {
    return when (timeTense) {
        TimeTense.PRESENT -> {
            PronounPresentSuffix.valueOf(pronoun.name).suffix
        }
        TimeTense.PAST -> {
            PronounPastSuffix.valueOf(pronoun.name).suffix
        }
        TimeTense.FUTURE -> {
            PronounFutureSuffix.valueOf(pronoun.name).suffix
        }
    }
}
