package com.cramsan.edifikana.server

import com.cramsan.edifikana.server.di.FrameworkModule
import com.cramsan.edifikana.server.json.jsonFormat
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import com.google.cloud.functions.CloudEventsFunction
import com.google.events.cloud.firestore.v1.DocumentEventData
import com.google.protobuf.InvalidProtocolBufferException
import io.cloudevents.CloudEvent
import kotlinx.datetime.Clock
import org.koin.core.context.startKoin
import java.util.Base64

class CloudFirebaseApp : CloudEventsFunction {
    companion object {
        private const val TAG = "CloudFirebaseApp"
    }

    var launchParametersProvider: () -> FunctionLaunchParameters = { FunctionLaunchParameters.fromSystemEnvironment() }

    var dependencies: FunctionDependencies = DependenciesLocalCredentials()

    var clock = Clock.System

    init {
        initializeDependencies()
    }

    @Throws(InvalidProtocolBufferException::class)
    override fun accept(event: CloudEvent) {
        logI(TAG, "Function triggered by event on: " + event.source)
        logI(TAG, "Event type: " + event.type)

        val payloadBytes = jsonFormat.serialize(event)
        val encodedString: String = Base64.getEncoder().encodeToString(payloadBytes)
        logW(TAG, "Encoded event: $encodedString")

        val functionLaunchParameters = launchParametersProvider()

        val eventData = DocumentEventData.parseFrom(event.data!!.toBytes())

        CloudFireService(
            functionLaunchParameters.projectName,
            dependencies.sheets,
            dependencies.drive,
            dependencies.firestore,
            clock,
        ).processEvent(eventData)
        logI(TAG, "Invocation complete")
    }

    private fun initializeDependencies() {
        startKoin {
            modules(
                FrameworkModule,
            )
        }
    }
}
