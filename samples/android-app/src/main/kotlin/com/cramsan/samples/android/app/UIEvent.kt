package com.cramsan.samples.android.app

/**
 *
 */
sealed class UIEvent {
    /**
     *
     */
    object Noop : UIEvent()

    /**
     *
     */
    class NextPage(val stockId: String) : UIEvent()
}
