package com.cramsan.edifikana.server

import com.google.events.cloud.firestore.v1.DocumentEventData
import com.google.events.cloud.firestore.v1.Value
import com.google.protobuf.NullValue
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals

class CloudFireServiceTest {

    @Test
    fun `demo of serializing event log created`() {
        val payload = "CuYFCmVwcm9qZWN0cy9lZGlmaWthbmEvZGF0YWJhc2VzLyhkZWZhdWx0KS9kb2N1bWVudHMvZXZlbnRM" +
            "b2cvRE5JXzQ3NTI0ODExLTE3MTUxODk2MTktTUFJTlRFTkFOQ0VfU0VSVklDRRIlCgdzdW1tYXJ5EhqKARdzZX" +
            "J2aWNpbyBkZSBqYXJkaW5lcsOtYRIYChFmYWxsYmFja0V2ZW50VHlwZRIDigEAEg4KBHVuaXQSBooBAzEwORIW" +
            "Cgx0aW1lUmVjb3JkZWQSBhDz7u6xBhKOAwoLZGVzY3JpcHRpb24S/gKKAfoCaW5ncmVzYXJvbiBwZXJzb25hbC" +
            "BwYXJhIHNlcnZpY2lvIGRlIGphcmRpbmVyw61hIEZlcm5hbmRvIEVucmlxdWUgUGxhbm8gRE5JIDE2NTgyNTQs" +
            "IGxhIHNydGEuIGFkbWluaXN0cmFkb3JhIGxlcyBtb3N0csOzIGRldGFsbGFkYW1lbnRlIGxvcyBsdWdhcmVzIH" +
            "F1ZSBzZSBkZWJlIHJlYWxpemFyIGxvcyB0cmFiYWpvcy4gRWwgc3IuIGphcmRpbmVybyBpbmRpY8OzIHF1ZSBo" +
            "YXkgcGxhbnRhcyBlbmZlcm1hcyB5IHF1ZSBuZWNlc2l0YW4gZnVtaWdhci4gYSBwYXJ0ZSBkZSBsYSBzcnRhLi" +
            "BhZG1pbmlzdHJhZG9yYSBsYSBzcmEuIENsYXVkaWEgQm9uYW5pIHRhbWJpw6luIGVzdMOhIGVuc2XDsWFuZG8g" +
            "YWwgamFyZGluZXJvIGxvcyBsdWdhcmVzIGEgdHJhYmFqYXIuEiMKCWV2ZW50VHlwZRIWigETTUFJTlRFTkFOQ0" +
            "VfU0VSVklDRRIbChRmYWxsYmFja0VtcGxveWVlTmFtZRIDigEAEiUKEmVtcGxveWVlRG9jdW1lbnRJZBIPigEM" +
            "RE5JXzQ3NTI0ODExGgwI9+7usQYQmMXusQEiDAj37u6xBhCYxe6xAQ=="
        val data = Base64.getDecoder().decode(payload)
        val originalEventData: DocumentEventData = DocumentEventData.parseFrom(data)

        val eventData = DocumentEventData.newBuilder().apply {
            valueBuilder.apply {
                name = "projects/edifikana/databases/(default)/documents/eventLog/" +
                    "DNI_47524811-1715189619-MAINTENANCE_SERVICE"

                putFields(
                    "description",
                    Value.newBuilder().apply {
                        stringValue = "ingresaron personal para servicio de jardinería Fernando Enrique Plano" +
                            " DNI 1658254, la srta. administradora les mostró detalladamente los lugares " +
                            "que se debe realizar los trabajos. El sr. jardinero indicó que hay plantas " +
                            "enfermas y que necesitan fumigar. a parte de la srta. administradora la sra. " +
                            "Claudia Bonani también está enseñando al jardinero los lugares a trabajar."
                    }.build()
                )

                putFields(
                    "employeeDocumentId",
                    Value.newBuilder().apply {
                        stringValue = "DNI_47524811"
                    }.build()
                )

                putFields(
                    "eventType",
                    Value.newBuilder().apply {
                        stringValue = "MAINTENANCE_SERVICE"
                    }.build()
                )

                putFields(
                    "fallbackEmployeeName",
                    Value.newBuilder().apply {
                        stringValue = ""
                    }.build()
                )

                putFields(
                    "fallbackEventType",
                    Value.newBuilder().apply {
                        stringValue = ""
                    }.build()
                )

                putFields(
                    "summary",
                    Value.newBuilder().apply {
                        stringValue = "servicio de jardinería"
                    }.build()
                )

                putFields(
                    "employeeDocumentId",
                    Value.newBuilder().apply {
                        stringValue = "DNI_47524811"
                    }.build()
                )

                putFields(
                    "timeRecorded",
                    Value.newBuilder().apply {
                        integerValue = 1715189619
                    }.build()
                )

                putFields(
                    "unit",
                    Value.newBuilder().apply {
                        stringValue = "109"
                    }.build()
                )

                createTimeBuilder.apply {
                    seconds = 1715189623
                    nanos = 373007000
                }
                updateTimeBuilder.apply {
                    seconds = 1715189623
                    nanos = 373007000
                }
            }
        }.build()

        assertEquals(originalEventData.oldValue, eventData.oldValue)
        assertEquals(originalEventData.updateMask, eventData.updateMask)

        val value = eventData.value
        val originalValue = originalEventData.value
        assertEquals(originalValue.name, value.name)
        assertEquals(originalValue.createTime, value.createTime)
        assertEquals(originalValue.updateTime, value.updateTime)
        assertEquals(originalValue.fieldsMap, value.fieldsMap)
    }

    @Test
    fun `demo of serializing time card event created`() {
        val payload = "CokDCmFwcm9qZWN0cy9lZGlmaWthbmEvZGF0YWJhc2VzLyhkZWZhdWx0KS9kb2N1bWVudHMvdGltZUNhcmRSZWNvcmRz" +
            "L0ROSV83NzEzNTQxOC1DTE9DS19JTi0xNzE2MDc2MzA2EhwKFmZhbGxiYWNrRW1wbG95ZWVJZFR5cGUSAlgAEh4KGGZhbGxiYW" +
            "NrRW1wbG95ZWVJZFJlYXNvbhICWAASNQoIaW1hZ2VVcmwSKYoBJmNsb2NraW5vdXQvMjAyNC0wNS0xOC0xOC01MS00My00MTcu" +
            "anBnEhMKCWV2ZW50VGltZRIGEJL+pLIGEiEKG2ZhbGxiYWNrRW1wbG95ZWVJZFR5cGVPdGhlchICWAASGAoJZXZlbnRUeXBlEg" +
            "uKAQhDTE9DS19JThIaChRmYWxsYmFja0VtcGxveWVlTmFtZRICWAASJQoSZW1wbG95ZWVEb2N1bWVudElkEg+KAQxETklfNzcx" +
            "MzU0MTgaDAif/qSyBhCY0MSiASIMCJ/+pLIGEJjQxKIB"
        val data = Base64.getDecoder().decode(payload)
        val originalEventData: DocumentEventData = DocumentEventData.parseFrom(data)

        val eventData = DocumentEventData.newBuilder().apply {
            valueBuilder.apply {
                name = "projects/edifikana/databases/(default)/documents/" +
                    "timeCardRecords/DNI_77135418-CLOCK_IN-1716076306"

                putFields(
                    "employeeDocumentId",
                    Value.newBuilder().apply {
                        stringValue = "DNI_77135418"
                    }.build()
                )

                putFields(
                    "eventTime",
                    Value.newBuilder().apply {
                        integerValue = 1716076306
                    }.build()
                )

                putFields(
                    "eventType",
                    Value.newBuilder().apply {
                        stringValue = "CLOCK_IN"
                    }.build()
                )

                putFields("fallbackEmployeeIdReason", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())

                putFields("fallbackEmployeeIdType", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())

                putFields("fallbackEmployeeName", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())

                putFields("fallbackEmployeeIdTypeOther", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())

                putFields(
                    "imageUrl",
                    Value.newBuilder().apply {
                        stringValue = "clockinout/2024-05-18-18-51-43-417.jpg"
                    }.build()
                )

                createTimeBuilder.apply {
                    seconds = 1716076319
                    nanos = 340863000
                }
                updateTimeBuilder.apply {
                    seconds = 1716076319
                    nanos = 340863000
                }
            }
        }.build()

        assertEquals(originalEventData.oldValue, eventData.oldValue)
        assertEquals(originalEventData.updateMask, eventData.updateMask)

        val value = eventData.value
        val originalValue = originalEventData.value
        assertEquals(originalValue.name, value.name)
        assertEquals(originalValue.createTime, value.createTime)
        assertEquals(originalValue.updateTime, value.updateTime)
        assertEquals(originalValue.fieldsMap, value.fieldsMap)
    }
}
