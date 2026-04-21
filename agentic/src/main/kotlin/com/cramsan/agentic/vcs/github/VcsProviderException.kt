package com.cramsan.agentic.vcs.github

/**
 * Thrown by [com.cramsan.agentic.vcs.github.GitHubVcsProvider] and
 * [com.cramsan.agentic.vcs.local.LocalVcsProvider] when a VCS operation fails with a non-zero
 * exit code or unexpected state. [exitCode] carries the `gh` CLI or `git` exit code for
 * diagnostic purposes.
 */
class VcsProviderException(message: String, val exitCode: Int) : Exception(message)
