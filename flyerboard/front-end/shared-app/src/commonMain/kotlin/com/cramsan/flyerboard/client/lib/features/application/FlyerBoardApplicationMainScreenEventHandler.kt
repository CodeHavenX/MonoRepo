package com.cramsan.flyerboard.client.lib.features.application

import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent

/**
 * Events that can be triggered application-wide.
 */
interface FlyerBoardApplicationMainScreenEventHandler {

    /**
     * Share content.
     */
    fun shareContent(event: FlyerBoardWindowsEvent.ShareContent)
}
