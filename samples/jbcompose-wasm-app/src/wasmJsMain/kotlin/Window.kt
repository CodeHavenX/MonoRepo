
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.cramsan.minesweepers.common.game.Game
import com.cramsan.minesweepers.common.ui.MainView

/**
 * Main function for the web application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val game = Game()
    game.configure()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
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
