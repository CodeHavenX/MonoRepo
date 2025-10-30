
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cramsan.sample.mpplib.compose.common.MainView

/**
 * Main function for the web application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("ComposeTarget") {
        MainView()
    }
}
