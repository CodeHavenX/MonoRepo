package com.cramsan.framework.thread

/**
 * Delegate that will need to implement the platform logic to manage threading.
 * Extends [ThreadUtilInterface] so that platform implementations can be used directly
 * wherever [ThreadUtilInterface] is required, without a wrapping [ThreadUtilImpl].
 */
interface ThreadUtilDelegate : ThreadUtilInterface
