package com.cramsan.ps2link.network.ws.testgui.ui.screens.connection

import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.ps2link.appcore.census.DBGServiceClient
import com.cramsan.ps2link.core.models.CensusLang
import com.cramsan.ps2link.core.models.Character
import com.cramsan.ps2link.core.models.Namespace
import com.cramsan.ps2link.network.ws.testgui.application.ApplicationManager
import com.cramsan.ps2link.network.ws.testgui.ui.ApplicationUIModel
import com.cramsan.ps2link.network.ws.testgui.ui.navigation.ScreenType
import com.cramsan.ps2link.network.ws.testgui.ui.screens.BaseViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel to manage the Main screen.
 *
 * @author cramsan
 */
class MainViewModel(
    applicationManager: ApplicationManager,
    private val serviceClient: DBGServiceClient,
    private val dispatcherProvider: DispatcherProvider,
) : BaseViewModel(applicationManager) {

    private var scope: CoroutineScope? = null

    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow(
        MainUIState(
            isRunning = false,
            characterName = "",
            playerSuggestions = persistentListOf(),
            selectedPlayer = null,
        ),
    )
    val uiState = _uiState.asStateFlow()

    override fun onStart() {
        val newScope = CoroutineScope(SupervisorJob() + dispatcherProvider.ioDispatcher())

        newScope.launch {
            applicationManager.uiModel.collect {
                handleProgramUIState(it)
            }
        }

        scope = newScope
    }

    private fun handleProgramUIState(applicationUiState: ApplicationUIModel) {
        _uiState.value = _uiState.value.copy(
            selectedPlayer = applicationUiState.character,
        )
    }

    override fun onClose() {
        scope?.cancel()
    }

    /**
     * Update the character name to be searched.
     */
    fun updateCharacterName(characterName: String) {
        _uiState.value = _uiState.value.copy(
            characterName = characterName,
        )

        searchJob?.cancel()
        searchJob = scope?.launch(dispatcherProvider.ioDispatcher()) {
            val names = serviceClient.getProfiles(
                searchField = characterName,
                namespace = Namespace.PS2PC,
                CensusLang.EN,
            ).body ?: emptyList()
            _uiState.value = _uiState.value.copy(
                playerSuggestions = names.toImmutableList(),
            )
        }
    }

    /**
     * Select a [character] to be selected as the observed [Character] globally within the application.
     */
    fun selectCharacter(character: Character) {
        _uiState.value = _uiState.value.copy(
            playerSuggestions = emptyList<Character>().toImmutableList(),
        )
        applicationManager.setCharacter(character)
    }

    /**
     * Start listening for events.
     */
    fun startListening() {
        applicationManager.startListening()
    }

    /**
     * Stop listening for events.
     */
    fun stopListening() {
        applicationManager.pauseListening()
    }

    /**
     * Open the settings screen.
     */
    fun openSettings() {
        applicationManager.setCurrentScreen(ScreenType.SETTINGS)
    }
}
