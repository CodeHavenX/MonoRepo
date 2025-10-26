package com.cramsan.runasimi.client.lib.manager

import kotlin.test.Test
import kotlin.test.assertEquals

class QuechuaManagerTest {

    @Test
    fun testGeneratingNumbers() {
        // Test base numbers
        assertEquals("chusaq", 0.toYupay())
        assertEquals("iskay", 2.toYupay())
        assertEquals("kimsa", 3.toYupay())
        assertEquals("tawa", 4.toYupay())
        assertEquals("pichqa", 5.toYupay())
        assertEquals("suqta", 6.toYupay())
        assertEquals("qanchis", 7.toYupay())
        assertEquals("pusaq", 8.toYupay())
        assertEquals("isqun", 9.toYupay())
        assertEquals("chunka", 10.toYupay())
        assertEquals("pachak", 100.toYupay())
        assertEquals("waranqa", 1000.toYupay())

        // Test composite numbers
        assertEquals("iskay chunka", 20.toYupay())
        assertEquals("kimsa pachak", 300.toYupay())
        assertEquals("tawa waranqa", 4000.toYupay())

        // Test complex numbers
        assertEquals("chunka iskayniyuq", 12.toYupay())
        assertEquals("iskay pachak pichqa chunka suqtayuq", 256.toYupay())
        assertEquals("kimsa waranqa tawa pachak pichqa chunka suqtayuq", 3456.toYupay())

        // Test numbers with zeros in between
        assertEquals("kimsa waranqa suqtayuq", 3006.toYupay())
        assertEquals("kimsa waranqa isqun chunkayuq", 3090.toYupay())
    }


    @Test
    fun testToPronoun() {
        val v = Verb("miku", "to eat")

        // First person
        assertEquals("ñuqa", Conjugation(v, Tense.PRESENT, Person.FIRST, Plurality.SINGULAR, false).toPronoun())
        assertEquals("ñuqanchik", Conjugation(v, Tense.PRESENT, Person.FIRST, Plurality.PLURAL, true).toPronoun())
        assertEquals("ñuqayku", Conjugation(v, Tense.PRESENT, Person.FIRST, Plurality.PLURAL, false).toPronoun())

        // Second person
        assertEquals("qam", Conjugation(v, Tense.PRESENT, Person.SECOND, Plurality.SINGULAR, false).toPronoun())
        assertEquals("qamkuna", Conjugation(v, Tense.PRESENT, Person.SECOND, Plurality.PLURAL, false).toPronoun())

        // Third person
        assertEquals("pay", Conjugation(v, Tense.PRESENT, Person.THIRD, Plurality.SINGULAR, false).toPronoun())
        assertEquals("paykuna", Conjugation(v, Tense.PRESENT, Person.THIRD, Plurality.PLURAL, false).toPronoun())
    }

    @Test
    fun testToSuffix() {
        val v = Verb("miku", "to eat")

        // PRESENT
        assertEquals("ni", Conjugation(v, Tense.PRESENT, Person.FIRST, Plurality.SINGULAR, false).toSuffix())
        assertEquals("nchik", Conjugation(v, Tense.PRESENT, Person.FIRST, Plurality.PLURAL, true).toSuffix())
        assertEquals("niku", Conjugation(v, Tense.PRESENT, Person.FIRST, Plurality.PLURAL, false).toSuffix())
        assertEquals("nki", Conjugation(v, Tense.PRESENT, Person.SECOND, Plurality.SINGULAR, false).toSuffix())
        assertEquals("nkichik", Conjugation(v, Tense.PRESENT, Person.SECOND, Plurality.PLURAL, false).toSuffix())
        assertEquals("n", Conjugation(v, Tense.PRESENT, Person.THIRD, Plurality.SINGULAR, false).toSuffix())
        assertEquals("nku", Conjugation(v, Tense.PRESENT, Person.THIRD, Plurality.PLURAL, false).toSuffix())

        // PAST
        assertEquals("rqani", Conjugation(v, Tense.PAST, Person.FIRST, Plurality.SINGULAR, false).toSuffix())
        assertEquals("rqanchik", Conjugation(v, Tense.PAST, Person.FIRST, Plurality.PLURAL, true).toSuffix())
        assertEquals("rqaniku", Conjugation(v, Tense.PAST, Person.FIRST, Plurality.PLURAL, false).toSuffix())
        assertEquals("rqanki", Conjugation(v, Tense.PAST, Person.SECOND, Plurality.SINGULAR, false).toSuffix())
        assertEquals("rqankichik", Conjugation(v, Tense.PAST, Person.SECOND, Plurality.PLURAL, false).toSuffix())
        assertEquals("rqa", Conjugation(v, Tense.PAST, Person.THIRD, Plurality.SINGULAR, false).toSuffix())
        assertEquals("rqaku".replace(" ", ""), Conjugation(v, Tense.PAST, Person.THIRD, Plurality.PLURAL, false).toSuffix()) // "rqa" + "ku" => "rqaku"

        // FUTURE
        assertEquals("saq", Conjugation(v, Tense.FUTURE, Person.FIRST, Plurality.SINGULAR, false).toSuffix())
        assertEquals("sunchik", Conjugation(v, Tense.FUTURE, Person.FIRST, Plurality.PLURAL, true).toSuffix())
        assertEquals("saqku", Conjugation(v, Tense.FUTURE, Person.FIRST, Plurality.PLURAL, false).toSuffix())
        assertEquals("nki", Conjugation(v, Tense.FUTURE, Person.SECOND, Plurality.SINGULAR, false).toSuffix())
        assertEquals("nkichik", Conjugation(v, Tense.FUTURE, Person.SECOND, Plurality.PLURAL, false).toSuffix())
        assertEquals("nqa", Conjugation(v, Tense.FUTURE, Person.THIRD, Plurality.SINGULAR, false).toSuffix())
        assertEquals("nqaku", Conjugation(v, Tense.FUTURE, Person.THIRD, Plurality.PLURAL, false).toSuffix())
    }
}