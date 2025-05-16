package com.cramsan.tokenmanager.lib

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.cramsan.tokenmanager.lib.components.board.Board
import com.cramsan.tokenmanager.lib.components.rememberTokenManager
import com.cramsan.tokenmanager.lib.components.token.Token

@Composable
fun TokenManager() {
    val manager = rememberTokenManager()
    val tokens = manager.tokenList.value

    Board {
        Button(onClick = {
            manager.createToken()
        }) {
            Text("Add")
        }
        tokens.forEach {
            Token(
                it,
                onCloneSelected = { original ->
                    manager.createToken(
                        name = original.name.value,
                        power = original.power.value,
                        toughness = original.toughness.value,
                        keywords = original.keywords.value,
                        text = original.text.value,
                    )
                }
            )
        }
    }
}
