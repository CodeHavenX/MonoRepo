package com.cramsan.stranded.lib.client.controllers

import com.cramsan.stranded.lib.client.ClientEventHandler
import com.cramsan.stranded.lib.client.ui.mainmenu.CreateLobbyMenuEventHandler
import com.cramsan.stranded.lib.client.ui.mainmenu.LobbyListMenuEventHandler
import com.cramsan.stranded.lib.client.ui.mainmenu.LobbyMenuEventHandler
import com.cramsan.stranded.lib.client.ui.mainmenu.PlayerNameMenuEventHandler

interface MainMenuController :
    ClientEventHandler,
    CreateLobbyMenuEventHandler,
    LobbyListMenuEventHandler,
    LobbyMenuEventHandler,
    PlayerNameMenuEventHandler {

    fun onShow()

    fun onDispose()

    fun openPlayerNameMenu()

    fun openCreateLobbyMenu()

    fun openLobbyListMenu()

    fun closeApplication()
}