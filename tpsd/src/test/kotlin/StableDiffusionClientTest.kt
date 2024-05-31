import org.junit.jupiter.api.Test
import sd.Term
import sd.toPrompt
import kotlin.test.assertEquals

class StableDiffusionClientTest {

    @Test
    fun `map term list to prompt`() {
        val terms = listOf(
            Term("test"),
            Term("example"),
            Term("with emphasis", 1),
            Term("even more emphasis", 2),
            Term("less emphasis", -1),
            Term("even less emphasis", -2),
            Term("another word"),
        )
        assertEquals(
            """
            test, example, (with emphasis), ((even more emphasis)), [less emphasis], [[even less emphasis]], another word
            """.trimIndent(),
            terms.toPrompt()
        )
    }
}
