package com.cramsan.agentic.vcs.github

class VcsProviderException(message: String, val exitCode: Int) : Exception(message)
