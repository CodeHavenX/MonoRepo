package com.cramsan.edifikana.server.operations

import com.cramsan.edifikana.lib.firestore.PropertyConfigPK
import com.cramsan.edifikana.server.CloudFireService
import com.cramsan.edifikana.server.DependenciesLocalCredentials
import com.cramsan.edifikana.server.FunctionDependencies
import com.cramsan.framework.logging.logI
import kotlinx.datetime.Clock

private const val TAG = "ConfigureDrive"

suspend fun main() {
    logI(TAG, "Starting configuration of Google Drive")

    logI(TAG, "Loading dependencies")
    val dependencies: FunctionDependencies = DependenciesLocalCredentials()

    logI(TAG, "Loading function launch parameters")

    val service = CloudFireService(
        "edifikana-stage",
        dependencies.sheets,
        dependencies.drive,
        dependencies.firestore,
        Clock.System,
    )
    logI(TAG, "Service created")

    service.configureDrive(PropertyConfigPK("cenit_01"))
}
