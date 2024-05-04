package com.cramsan.edifikana.server

import com.cramsan.edifikana.lib.firestore.Employee
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.cloud.functions.CloudEventsFunction
import com.google.events.cloud.firestore.v1.DocumentEventData
import com.google.protobuf.InvalidProtocolBufferException
import io.cloudevents.CloudEvent
import java.util.logging.Logger


class CloudFireController(private val db: Firestore) : CloudEventsFunction {
    companion object {
        private val logger: Logger = Logger.getLogger(CloudFireController::class.java.getName())
    }

    @Throws(InvalidProtocolBufferException::class)
    override fun accept(event: CloudEvent) {
        val firestoreEventData: DocumentEventData = DocumentEventData.parseFrom(event.data!!.toBytes())

        logger.info("Function triggered by event on: " + event.source)
        logger.info("Event type: " + event.type)

        logger.info("Old value:")
        logger.info(firestoreEventData.getOldValue().toString())

        logger.info("New value:")
        logger.info(firestoreEventData.getValue().toString())

        getEvent()
    }

    fun getEvent() {
        // Create a new user with a first and last name
        var docRef: DocumentReference = db.collection(Employee.COLLECTION).document("DNI_47202201-CLOCK_OUT-1714857338")

        // asynchronously retrieve the document
        val future = docRef.get()

        // future.get() blocks on response
        val document = future.get()
        if (document.exists()) {
            println("Document data: " + document.data)
        } else {
            println("No such document!")
        }
    }
}