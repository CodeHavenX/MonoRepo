package com.cramsan.agentic.vcs.github

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class ShellResult(
    val stdout: String,
    val exitCode: Int,
    val stderr: String,
)

class ShellRunner {
    suspend fun run(vararg args: String): ShellResult = withContext(Dispatchers.IO) {
        var lastResult: ShellResult? = null
        val delays = listOf(1000L, 2000L, 4000L)

        for (attempt in 0..3) {
            val process = ProcessBuilder(*args)
                .also { it.redirectErrorStream(false) }
                .start()

            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            lastResult = ShellResult(stdout = stdout, exitCode = exitCode, stderr = stderr)

            if (exitCode == 0) {
                return@withContext lastResult
            }

            if (attempt < 3) {
                delay(delays[attempt])
            }
        }

        lastResult!!
    }
}
