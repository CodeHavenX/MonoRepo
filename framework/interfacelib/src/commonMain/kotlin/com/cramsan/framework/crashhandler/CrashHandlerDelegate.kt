package com.cramsan.framework.crashhandler

/**
 * Delegate to provide a specific implementation for the crash handler.
 * Extends [CrashHandler] so that platform implementations can be used directly
 * wherever [CrashHandler] is required, without a wrapping [CrashHandlerImpl].
 *
 * @see [CrashHandler]
 */
interface CrashHandlerDelegate : CrashHandler
