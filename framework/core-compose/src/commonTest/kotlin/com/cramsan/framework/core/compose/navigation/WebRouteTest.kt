package com.cramsan.framework.core.compose.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * [OneParam]/[TwoParams] use hand-written [KSerializer]s instead of `@Serializable` because
 * this module doesn't apply the kotlinx-serialization compiler plugin (callers always supply
 * their own generated serializer to [WebRoute]; this module only consumes [KSerializer]).
 */
@OptIn(ExperimentalSerializationApi::class)
class WebRouteTest {
    data class OneParam(val value: String) : Destination

    data class TwoParams(val a: String, val b: String) : Destination

    object OneParamSerializer : KSerializer<OneParam> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("OneParam") {
                element("value", serializer<String>().descriptor)
            }

        override fun serialize(encoder: Encoder, value: OneParam) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.value)
            }
        }

        override fun deserialize(decoder: Decoder): OneParam =
            decoder.decodeStructure(descriptor) {
                var value = ""
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> value = decodeStringElement(descriptor, 0)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index $index")
                    }
                }
                OneParam(value)
            }
    }

    object TwoParamsSerializer : KSerializer<TwoParams> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("TwoParams") {
                element("a", serializer<String>().descriptor)
                element("b", serializer<String>().descriptor)
            }

        override fun serialize(encoder: Encoder, value: TwoParams) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.a)
                encodeStringElement(descriptor, 1, value.b)
            }
        }

        override fun deserialize(decoder: Decoder): TwoParams =
            decoder.decodeStructure(descriptor) {
                var a = ""
                var b = ""
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> a = decodeStringElement(descriptor, 0)
                        1 -> b = decodeStringElement(descriptor, 1)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index $index")
                    }
                }
                TwoParams(a, b)
            }
    }

    private val oneParamRoute = WebRoute("/one", OneParamSerializer)
    private val twoParamsRoute = WebRoute("/two", TwoParamsSerializer)

    @Test
    fun `toWebPath percent-encodes special characters`() {
        val path = oneParamRoute.toWebPath(OneParam("a&b=c#d"))
        assertEquals("/one?value=a%26b%3Dc%23d", path)
    }

    @Test
    fun `toWebPath percent-encodes unicode`() {
        val path = oneParamRoute.toWebPath(OneParam("café"))
        assertEquals("/one?value=caf%C3%A9", path)
    }

    @Test
    fun `fromWebPath decodes percent-encoded query value`() {
        val destination = oneParamRoute.fromWebPath("/one?value=a%26b%3Dc%23d")
        assertEquals(OneParam("a&b=c#d"), destination)
    }

    @Test
    fun `fromWebPath decodes percent-encoded unicode`() {
        assertEquals(OneParam("café"), oneParamRoute.fromWebPath("/one?value=caf%C3%A9"))
    }

    @Test
    fun `toWebPath and fromWebPath round-trip special characters`() {
        val original = OneParam("a&b=c#d café")
        val encoded = oneParamRoute.toWebPath(original)
        assertEquals(original, oneParamRoute.fromWebPath(encoded))
    }

    @Test
    fun `fromWebPath reads params from fragment`() {
        val destination = twoParamsRoute.fromWebPath("/two#a=1&b=2")
        assertEquals(TwoParams("1", "2"), destination)
    }

    @Test
    fun `fromWebPath merges query and fragment params`() {
        val destination = twoParamsRoute.fromWebPath("/two?a=1#b=2")
        assertEquals(TwoParams("1", "2"), destination)
    }

    @Test
    fun `fromWebPath fragment value wins over query value on key collision`() {
        val destination = twoParamsRoute.fromWebPath("/two?a=1&b=fromQuery#b=fromFragment")
        assertEquals(TwoParams("1", "fromFragment"), destination)
    }

    @Test
    fun `fromWebPath decodes percent-encoded fragment value`() {
        val destination = oneParamRoute.fromWebPath("/one#value=a%26b")
        assertEquals(OneParam("a&b"), destination)
    }

    @Test
    fun `fromWebPath returns null when path does not match before fragment`() {
        assertNull(oneParamRoute.fromWebPath("/other#value=1"))
    }
}
