package com.cramsan.agentic.vcs.github

import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "ShellRunner"

data class ShellResult(
    val stdout: String,
    val exitCode: Int,
    val stderr: String,
)

class ShellRunner {
    suspend fun run(vararg args: String, workingDir: String? = null): ShellResult = withContext(Dispatchers.IO) {
        val commandStr = args.joinToString(" ")
        val commandStrLoggingLine = commandStr.split("\n").let {
            when (it.size) {
                0 -> ""
                1 -> it.first()
                else -> it.first() + "..."
            }
        }
        logI(TAG, "Running command: $commandStrLoggingLine")
        logD(TAG, "Running command: $commandStr")
        if (workingDir != null) {
            logD(TAG, "Working directory: $workingDir")
        }

        var lastResult: ShellResult? = null
        val delays = listOf(1000L, 2000L, 4000L)

        for (attempt in 0..3) {
            if (attempt > 0) {
                logI(TAG, "Retrying command (attempt ${attempt + 1}): $commandStrLoggingLine")
                logD(TAG, "Retrying command (attempt ${attempt + 1}): $commandStr")
            }
            val process = ProcessBuilder(*args)
                .also { pb ->
                    pb.redirectErrorStream(false)
                    if (workingDir != null) pb.directory(java.io.File(workingDir))
                }
                .start()

            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            logD(TAG, "Command exited with code=$exitCode: $commandStr")
            if (stderr.isNotEmpty()) {
                logW(TAG, "Command stderr (exitCode=$exitCode): $stderr")
            }

            lastResult = ShellResult(stdout = stdout, exitCode = exitCode, stderr = stderr)

            if (exitCode == 0) {
                logI(TAG, "Command succeeded: $commandStrLoggingLine")
                logD(TAG, "Command succeeded: $commandStr")
                return@withContext lastResult
            }

            if (attempt < 3) {
                val delayMs = delays[attempt]
                logW(TAG, "Command failed with exitCode=$exitCode on attempt ${attempt + 1}, retrying in ${delayMs}ms: $commandStr")
                delay(delayMs)
            }
        }

        logW(TAG, "Command exhausted all retry attempts with exitCode=${lastResult?.exitCode}: $commandStr")
        lastResult!!
    }
}
