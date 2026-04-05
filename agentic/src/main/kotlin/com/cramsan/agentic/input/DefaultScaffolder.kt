package com.cramsan.agentic.input

import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import java.nio.file.Files
import java.nio.file.Path

class DefaultScaffolder : Scaffolder {

    override fun scaffold(outputDir: Path) {
        writeFile(
            outputDir.resolve("goals-scope.md"),
            """
            # Goals & Scope

            ## Goals & Scope

            Describe the high-level goals and scope of this project.

            ### Goals

            - Goal 1: ...
            - Goal 2: ...

            ### Out of Scope

            - Item 1: ...
            """.trimIndent(),
        )

        writeFile(
            outputDir.resolve("architecture-design.md"),
            """
            # Architecture & Design

            ## Architecture & Design

            Describe the system architecture and key design decisions.

            ### Components

            - Component 1: ...
            - Component 2: ...

            ### Data Flow

            Describe how data flows through the system.
            """.trimIndent(),
        )

        writeFile(
            outputDir.resolve("standards.md"),
            """
            # Standards

            ## Standards

            Define the coding standards, conventions, and best practices for this project.

            ### Coding Style

            - Use meaningful names
            - Write self-documenting code

            ### Testing

            - Unit tests required for all business logic
            - Integration tests for external dependencies
            """.trimIndent(),
        )

        writeFile(
            outputDir.resolve("reviewers/security.md"),
            """
            # Security Reviewer

            You are a security-focused code and design reviewer. Your role is to identify security vulnerabilities, risks, and areas for improvement.

            ## Review Focus

            - Authentication and authorization
            - Input validation and sanitization
            - Sensitive data handling
            - Dependency vulnerabilities
            - Injection risks (SQL, command, etc.)

            ## Output Format

            Provide clear, actionable feedback on security concerns. Rate severity as BLOCKING (must fix) or ADVISORY (recommended improvement).
            """.trimIndent(),
        )

        writeFile(
            outputDir.resolve("reviewers/design-patterns.md"),
            """
            # Design Patterns Reviewer

            You are a software design expert. Your role is to review code and architecture for adherence to good design principles and patterns.

            ## Review Focus

            - SOLID principles
            - Appropriate use of design patterns
            - Separation of concerns
            - Code maintainability and extensibility
            - Anti-pattern identification

            ## Output Format

            Provide clear, actionable feedback on design quality. Rate severity as BLOCKING (fundamental design flaw) or ADVISORY (improvement suggestion).
            """.trimIndent(),
        )
    }

    private fun writeFile(path: Path, content: String) {
        if (Files.exists(path)) {
            logW(TAG, "File already exists, skipping: $path")
            return
        }
        Files.createDirectories(path.parent)
        Files.writeString(path, content)
        logI(TAG, "Scaffolded: $path")
    }

    companion object {
        private const val TAG = "DefaultScaffolder"
    }
}
