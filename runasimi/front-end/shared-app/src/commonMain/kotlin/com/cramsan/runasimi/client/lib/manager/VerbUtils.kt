@file:Suppress("LongMethod, CyclomaticComplexMethod, NestedBlockDepth")

package com.cramsan.runasimi.client.lib.manager

import kotlin.random.Random

/**
 * Enums that represent grammatical categories for verb conjugation.
 */
enum class Person {
    FIRST,

    SECOND,

    THIRD,
}

/**
 * Enums that represent grammatical categories for verb conjugation.
 */
enum class Plurality {
    SINGULAR,

    PLURAL,
}

/**
 * Enums that represent grammatical categories for verb conjugation.
 */
enum class Tense {
    PAST,

    PRESENT,

    FUTURE,
}

/**
 * Data class that represents a verb in quechua.
 */
data class Verb(val root: String, val meaning: String)

/**
 * Data class that represents a verb conjugation in quechua.
 */
data class Conjugation(
    val verb: Verb,
    val tense: Tense,
    val person: Person,
    val plurality: Plurality,
    val inclusive: Boolean,
)

private val quechuaVerbList = listOf(
    Verb("miku", "to eat"),
    Verb("yaku", "to drink"),
    Verb("rima", "to speak"),
    Verb("taki", "to sing"),
    Verb("puñu", "to sleep"),
    Verb("ranti", "to buy"),
    Verb("llacha", "to know"),
    Verb("llamka", "to work"),
    Verb("richkari", "despertar"),
    Verb("pawa", "to run"),
    Verb("qawa", "to see"),
    Verb("uyari", "to hear"),
    Verb("ni", "to say"),
    Verb("puklla", "to play"),
)

/**
 * Extension function to get the pronoun for a given conjugation.
 */
fun Conjugation.toPronoun(): String = when (person) {
    Person.FIRST -> if (plurality == Plurality.SINGULAR) {
        "ñuqa"
    } else {
        if (inclusive) "ñuqanchik" else "ñuqayku"
    }

    Person.SECOND -> if (plurality == Plurality.SINGULAR) {
        "qam"
    } else {
        "qamkuna"
    }

    Person.THIRD -> if (plurality == Plurality.SINGULAR) {
        "pay"
    } else {
        "paykuna"
    }
}

/**
 * Generate a random verb conjugation.
 */
fun generateConjugation(): Conjugation {
    val time = Tense.entries.random(Random)
    val person = Person.entries.random(Random)
    val plurality = Plurality.entries.random(Random)
    val inclusive = person == Person.FIRST && plurality == Plurality.PLURAL && listOf(true, false).random()
    val verb = quechuaVerbList.random()

    return Conjugation(
        verb = verb,
        tense = time,
        person = person,
        plurality = plurality,
        inclusive = inclusive,
    )
}

/**
 * Extension function to get the suffix for a given conjugation.
 */
@Suppress("CyclomaticComplexMethod")
fun Conjugation.toSuffix(): String {
    // helper for present tense person suffix
    fun presentPersonSuffix(): String = when (person) {
        Person.FIRST -> if (plurality == Plurality.SINGULAR) {
            "ni"
        } else if (inclusive) {
            ""
        } else {
            "ni"
        }

        Person.SECOND -> "nki"

        Person.THIRD -> "n"
    }

    // helper for past tense person suffix
    fun pastPersonSuffix(): String = when (person) {
        Person.FIRST -> if (plurality == Plurality.SINGULAR) {
            "rqani"
        } else if (inclusive) {
            "rqa"
        } else {
            "rqani"
        }

        Person.SECOND -> "rqanki"

        Person.THIRD -> "rqa"
    }

    // helper for future tense person suffix
    fun futurePersonSuffix(): String = when (person) {
        Person.FIRST -> if (plurality == Plurality.SINGULAR) {
            "saq"
        } else if (inclusive) {
            "su"
        } else {
            "saq"
        }

        Person.SECOND -> "nki"

        Person.THIRD -> "nqa"
    }

    val personSuffix = when (tense) {
        Tense.PRESENT -> presentPersonSuffix()
        Tense.PAST -> pastPersonSuffix()
        Tense.FUTURE -> futurePersonSuffix()
    }

    val pluralitySuffix = when {
        inclusive -> "nchik"

        plurality == Plurality.PLURAL -> when (person) {
            Person.FIRST, Person.THIRD -> "ku"
            Person.SECOND -> "chik"
        }

        else -> ""
    }

    return personSuffix + pluralitySuffix
}

/**
 * Convert a [Conjugation] to a quechua sentence.
 */
fun Conjugation.toSentence(): String = "${toPronoun()} ${this.verb.root}${toSuffix()}"

/**
 * Convert a [Conjugation] to a translation string.
 */
fun Conjugation.toTranslation(): String {
    val inclusiveText = if (plurality == Plurality.PLURAL) {
        if (inclusive) " (i)" else "(e)"
    } else {
        ""
    }
    return "\"${verb.meaning}\" in ${tense.name.lowercase()} tense, " +
        "${person.name.lowercase()} person, ${plurality.name.lowercase()}$inclusiveText"
}
