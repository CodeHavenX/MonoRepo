package com.cramsan.agentic.vcs.github

import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logW
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "ShellRunner"

/** Captured output from a subprocess invocation by [ShellRunner.run]. */
data class ShellResult(
    val stdout: String,
    val exitCode: Int,
    val stderr: String,
)

/**
 * Executes shell commands as subprocesses on [kotlinx.coroutines.Dispatchers.IO].
 *
 * **Retry behavior**: failed commands (non-zero exit code) are automatically retried up to 3 times
 * with exponential backoff (1s, 2s, 4s). This is intentional for transient failures in `gh` CLI
 * calls (e.g. network blips). Callers should check [ShellResult.exitCode] on the returned value
 * and not assume success; a non-zero code after all retries is returned rather than thrown.
 *
 * **Blocking reads**: stdout and stderr are read to completion before `waitFor` is called.
 * Commands that produce very large output may buffer significant data in memory.
 * // TODO: consider streaming output for long-running commands to avoid memory pressure.
 *
 * **No shell expansion**: args are passed directly to [ProcessBuilder] without shell interpretation.
 * Glob patterns, pipes, and redirections will not work as expected.
 */
class ShellRunner(
    private val dispatcher: CoroutineDispatcher,
) {
    /**
     * Executes [args] as a subprocess, optionally under [workingDir]. Returns a [ShellResult]
     * with combined stdout, stderr, and exit code after all retry attempts are exhausted.
     * A non-zero exit code is always returned as a value — never thrown as an exception.
     */
    suspend fun run(args: List<String>, workingDir: String? = null): ShellResult = withContext(dispatcher) {
        val commandStr = args.joinToString(" ")
        logD(TAG, "Running command: $commandStr")
        if (workingDir != null) {
            logD(TAG, "Working directory: $workingDir")
        }

        var lastResult: ShellResult? = null
        val delays = listOf(1.seconds, 2.seconds, 4.seconds)

        repeat(MAX_ATTEMPTS) { attempt ->
            if (attempt > 0) {
                logD(TAG, "Retrying command (attempt ${attempt + 1}): $commandStr")
            }
            val process = ProcessBuilder(args)
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
                logD(TAG, "Command succeeded: $commandStr")
                return@withContext lastResult
            }

            if (attempt < (MAX_ATTEMPTS - 1)) {
                val delay = delays[attempt]
                logW(TAG, "Command failed with exitCode=$exitCode on attempt ${attempt + 1}, retrying in ${delay}: $commandStr")
                delay(delay)
            }
        }

        logW(TAG, "Command exhausted all retry attempts with exitCode=${lastResult?.exitCode}: $commandStr")
        requireNotNull(lastResult) { "No result captured after retry loop — this is a bug" }
    }

    /** Convenience overload for ad-hoc call sites with a known number of arguments. */
    suspend fun run(vararg args: String, workingDir: String? = null): ShellResult =
        run(args.toList(), workingDir)
}

const val MAX_ATTEMPTS = 4
