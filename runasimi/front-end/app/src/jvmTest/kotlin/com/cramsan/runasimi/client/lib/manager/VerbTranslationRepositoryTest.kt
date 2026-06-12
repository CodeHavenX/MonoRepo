package com.cramsan.runasimi.client.lib.manager

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class VerbTranslationRepositoryTest {

    @Test
    fun testEnglishTranslationFirstPersonSingularPresent() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("miku", "to eat")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PRESENT,
            person = Person.FIRST,
            plurality = Plurality.SINGULAR,
            inclusive = false,
        )

        val translation = repository.getEnglishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("I eat", translation)
    }

    @Test
    fun testSpanishTranslationFirstPersonSingularPresent() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("miku", "to eat")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PRESENT,
            person = Person.FIRST,
            plurality = Plurality.SINGULAR,
            inclusive = false,
        )

        val translation = repository.getSpanishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("yo como", translation)
    }

    @Test
    fun testEnglishTranslationThirdPersonSingularPast() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("miku", "to eat")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PAST,
            person = Person.THIRD,
            plurality = Plurality.SINGULAR,
            inclusive = false,
        )

        val translation = repository.getEnglishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("he/she ate", translation)
    }

    @Test
    fun testSpanishTranslationThirdPersonSingularPast() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("miku", "to eat")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PAST,
            person = Person.THIRD,
            plurality = Plurality.SINGULAR,
            inclusive = false,
        )

        val translation = repository.getSpanishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("él/ella comió", translation)
    }

    @Test
    fun testEnglishTranslationFirstPersonPluralInclusive() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("miku", "to eat")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PRESENT,
            person = Person.FIRST,
            plurality = Plurality.PLURAL,
            inclusive = true,
        )

        val translation = repository.getEnglishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("we (inclusive) eat", translation)
    }

    @Test
    fun testEnglishTranslationFirstPersonPluralExclusive() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("miku", "to eat")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PRESENT,
            person = Person.FIRST,
            plurality = Plurality.PLURAL,
            inclusive = false,
        )

        val translation = repository.getEnglishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("we (exclusive) eat", translation)
    }

    @Test
    fun testSpanishTranslationFirstPersonPluralInclusive() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("miku", "to eat")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PRESENT,
            person = Person.FIRST,
            plurality = Plurality.PLURAL,
            inclusive = true,
        )

        val translation = repository.getSpanishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("nosotros (inclusivo) comemos", translation)
    }

    @Test
    fun testEnglishTranslationFutureTense() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("rima", "to speak")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.FUTURE,
            person = Person.SECOND,
            plurality = Plurality.SINGULAR,
            inclusive = false,
        )

        val translation = repository.getEnglishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("you will speak", translation)
    }

    @Test
    fun testSpanishTranslationFutureTense() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("rima", "to speak")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.FUTURE,
            person = Person.SECOND,
            plurality = Plurality.SINGULAR,
            inclusive = false,
        )

        val translation = repository.getSpanishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("tú hablarás", translation)
    }

    @Test
    fun testSpanishTranslationSecondPersonPluralUsesUstedes() = runTest {
        val repository = VerbTranslationRepository()
        repository.initialize()

        val verb = Verb("taki", "to sing")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PRESENT,
            person = Person.SECOND,
            plurality = Plurality.PLURAL,
            inclusive = false,
        )

        val translation = repository.getSpanishTranslation(conjugation)
        assertNotNull(translation)
        assertEquals("ustedes cantan", translation)
    }

    @Test
    fun testToTranslationKey() {
        val verb = Verb("miku", "to eat")
        val conjugation = Conjugation(
            verb = verb,
            tense = Tense.PRESENT,
            person = Person.FIRST,
            plurality = Plurality.SINGULAR,
            inclusive = false,
        )

        val key = conjugation.toTranslationKey()
        assertEquals("miku", key.verbRoot)
        assertEquals(Tense.PRESENT, key.tense)
        assertEquals(Person.FIRST, key.person)
        assertEquals(Plurality.SINGULAR, key.plurality)
        assertEquals(false, key.inclusive)
    }
}
