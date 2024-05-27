package com.cramsan.minesweepers.android.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.cramsan.minesweepers.common.MainView
import com.cramsan.minesweepers.common.game.Game
import com.cramsan.minesweepers.common.game.Status

/**
 * Main entry point for the JB Compose Android sample app.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val game = Game()
        game.configure(
            columns = 10,
            rows = 15,
            mines = 20,
        )

        setContent {
            val map by game.gameStateHolder.map.collectAsState()
            val time by game.gameStateHolder.time.collectAsState()
            val minesRemaining by game.gameStateHolder.minesRemaining.collectAsState()
            val gameState by game.gameStateHolder.status.collectAsState()

            MainView(
                time,
                minesRemaining,
                map,
                gameState,
                { column, row -> game.primaryAction(column, row) },
                { column, row -> game.secondaryAction(column, row) },
                { game.configure() },
            )
        }
    }
}

@Preview
@Composable
fun MainActivityPreview() {
    MainView(
        10,
        12,
        listOf(listOf()),
        Status.NORMAL,
        { _, _ -> },
        { _, _ -> },
        { },
    )
}


// TODO: Currently there is an issue with the new resources API that causes previews not to render.
// This function is a temporary alternative to be able to verify that @Previews work.
@Preview(
    showBackground = true
)
@Composable
fun TempComposePreview() {
    Text(text = "Hello World!")
}