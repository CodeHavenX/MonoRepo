package com.cramsan.runasimi.mpplib

import kotlin.random.Random

class StatementManager(
    private val verbProvider: VerbProvider,
) {

    fun generateStatement(seed: Int = Random.nextInt()): Statement {
        val statementRandom = Random(seed)

        val pronouns = Pronoun.entries.toList()
        val timeTenses = TimeTense.entries.toList()
        val modalityTenses = ModalityTense.entries.toList()
        val verbs = verbProvider.loadResources()

        val pronoun = pronouns.random(statementRandom)
        val verb = verbs.random(statementRandom)
        val timeTense = timeTenses.random(statementRandom)
        val modalityTense = modalityTenses.random(statementRandom)

        return Statement(
            pronoun = pronoun,
            verb = verb,
            timeTense = timeTense,
            modalityTense = modalityTense,
        )
    }
}

data class Statement(
    val pronoun: Pronoun,
    val verb: Verb,
    val modalityTense: ModalityTense,
    val timeTense: TimeTense,
)
