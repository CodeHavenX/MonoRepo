package com.codehavenx.alpaca.frontend.appcore.features.main

/**
 * UIState representing the main menu.
 */
data class MainMenuUIState(
    val content: List<UserUIModel>,
    val isLoading: Boolean,
)
