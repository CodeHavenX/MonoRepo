package com.cramsan.tokenmanager.lib.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.cramsan.tokenmanager.lib.state.Keyword
import com.cramsan.tokenmanager.lib.state.TokenModel

class TokenManager {
    private val _tokenList: MutableState<List<TokenModel>> = mutableStateOf(emptyList())

    val tokenList: State<List<TokenModel>>
        get() = _tokenList

    fun createToken(
        name: String = "",
        power: Int = 1,
        toughness: Int = 1,
        keywords: List<Keyword> = emptyList(),
        text: List<String> = emptyList(),
    ) {
        _tokenList.value = _tokenList.value + TokenModel(
            name = name,
            power = power,
            toughness = toughness,
            keywords = keywords,
            text = text,
        )
    }
}

@Composable
fun rememberTokenManager(): TokenManager {
    val tokenManager = remember { TokenManager() }
    return tokenManager
}
