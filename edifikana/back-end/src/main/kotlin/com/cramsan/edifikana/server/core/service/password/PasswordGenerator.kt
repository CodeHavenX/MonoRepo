package com.cramsan.edifikana.server.core.service.password

/**
 * Interface for password generators.
 *
 * This interface allows for having different implementations of password generators. A common use-case would be
 * having a production implementation that generates secure passwords and a test implementation that can be
 * mocked/stubbed.
 */
interface PasswordGenerator {

    /**
     * Generates a password.
     */
    fun generate(): String

}
