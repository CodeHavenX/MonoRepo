package com.cramsan.edifikana.server

import com.cramsan.edifikana.lib.requireNotBlank

class FunctionLaunchParameters(
    val projectName: String,
) {

    init {
        requireNotBlank(projectName) {
            "Variable projectName variable"
        }
    }

    companion object {
        fun fromSystemEnvironment(): FunctionLaunchParameters {
            val loadedProjectName: String? = System.getenv(PROJECT_NAME)

            requireNotBlank(loadedProjectName) {
                "Missing $PROJECT_NAME environment variable"
            }

            return FunctionLaunchParameters(
                projectName = loadedProjectName,
            )
        }
    }
}

private const val PROJECT_NAME = "PROJECT_NAME"
