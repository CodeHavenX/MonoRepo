package com.cramsan.runasimi.mpplib.main

import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logW
import com.cramsan.framework.preferences.Preferences
import com.cramsan.runasimi.mpplib.StatementManager
import com.cramsan.runasimi.mpplib.ui.MainViewUIModel
import com.cramsan.runasimi.mpplib.ui.toSentenceString
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readBytes
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainViewModel(
    private val statementManager: StatementManager,
    private val httpClient: HttpClient,
    private val preferences: Preferences,
    private val soundManager: SoundManager,
    private val fileManager: FileManager,
    private val dispatcherProvider: DispatcherProvider,
) {

    private val _uiModel = MutableStateFlow(
        MainViewUIModel(
            listOf(),
            isLoading = false,
        ),
    )

    val uiModel = _uiModel.asStateFlow()

    private var initialSeed = 0

    suspend fun loadInitialData() {
        val startingSeed = preferences.loadInt("seed").let {
            if (it == null) {
                val newSeed = Random.nextInt()
                preferences.saveInt("seed", newSeed)
                newSeed
            } else {
                it
            }
        }
        loadSavedSeed(startingSeed)
    }
    suspend fun setStatementSeed(seed: Int? = null) {
        savedSeed(seed ?: Random.nextInt())
    }

    private suspend fun loadCards() {
        _uiModel.update {
            it.copy(
                cards = (0 until PAGE_COUNT).map {
                    val statement = statementManager.generateStatement(initialSeed + it)
                    statement.toUIModel()
                },
            )
        }
    }

    suspend fun playAudioStatement(selectedIndex: Int) = withContext(dispatcherProvider.ioDispatcher()) {
        if (_uiModel.value.isLoading) {
            return@withContext
        }

        _uiModel.update {
            it.copy(isLoading = true)
        }
        runCatching {
            val selectedModel = uiModel.value.cards[selectedIndex]
            val message = selectedModel.sentence.toSentenceString()

            val content = fetchAudioFile(message)
            if (content == null) {
                logW(TAG, "Could not get audio data")
            } else {
                val filename = "tmp.ogg"
                val fileUrl = fileManager.saveToFile(filename, content)

                soundManager.playSound(fileUrl)
            }
        }
        _uiModel.update {
            it.copy(isLoading = false)
        }
    }

    private suspend fun fetchAudioFile(statement: String): ByteArray? {
        val response = httpClient.post("https://runasimi.cramsan.com/tts?lang=quy") {
            setBody(statement)
        }

        if (response.status != HttpStatusCode.OK) {
            logW(TAG, "Audio API request was not successful. Response ${response.status}")
            return null
        }

        return response.readBytes()
    }

    private suspend fun setSeed(newSeed: Int) {
        initialSeed = newSeed
        loadCards()
    }

    private suspend fun loadSavedSeed(fallbackSeed: Int) {
        val newSeed = preferences.loadInt("seed") ?: fallbackSeed
        setSeed(newSeed)
    }

    private suspend fun savedSeed(newSeed: Int) {
        preferences.saveInt("seed", newSeed)
        setSeed(newSeed)
    }
    companion object {
        const val PAGE_COUNT = 25
        private const val TAG = "MainViewModel"
    }
}
