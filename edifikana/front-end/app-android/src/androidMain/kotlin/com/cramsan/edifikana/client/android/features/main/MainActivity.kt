package com.cramsan.edifikana.client.android.features.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.android.db.clearOldFiles
import com.cramsan.edifikana.client.android.features.camera.CameraContract
import com.cramsan.edifikana.client.android.features.signin.SignInActivity
import com.cramsan.edifikana.client.android.utils.shareContent
import com.cramsan.edifikana.client.lib.features.main.MainActivityDelegatedEvent
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.ui.theme.AppTheme
import com.cramsan.framework.core.CoreUri
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by inject()

    private val cameraLauncher = registerForActivityResult(CameraContract()) { filePath ->
        viewModel.handleReceivedImage(filePath?.let { CoreUri(it) })
    }

    @Suppress("MagicNumber")
    private val mediaAttachmentLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5),
    ) { uris ->
        // TODO: Move mapping logic to ViewModel
        viewModel.handleReceivedImages(uris.map { CoreUri(it) })
    }

    @OptIn(KoinExperimentalAPI::class)
    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.enforceAuth()
            }
        }

        setContent {
            val event by viewModel.events.collectAsState(MainActivityEvent.Noop)
            val delegatedEvent by viewModel.delegatedEvents.collectAsState(MainActivityDelegatedEvent.Noop)
            val context = LocalContext.current
            val navController = rememberNavController()

            LaunchedEffect(event) {
                when (val mainActivityEvent = event) {
                    MainActivityEvent.Noop -> Unit
                    is MainActivityEvent.LaunchSignIn -> {
                        val intent = Intent(this@MainActivity, SignInActivity::class.java)
                        startActivity(intent)
                    }
                    is MainActivityEvent.OpenCamera -> {
                        cameraLauncher.launch(mainActivityEvent.filename)
                    }
                    is MainActivityEvent.OpenImageExternally -> {
                        ContextCompat.startActivity(
                            context,
                            Intent(
                                Intent.ACTION_VIEW,
                                mainActivityEvent.imageUri.getAndroidUri(),
                            ),
                            null,
                        )
                    }
                    is MainActivityEvent.OpenPhotoPicker -> {
                        mediaAttachmentLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    is MainActivityEvent.ShareContent -> {
                        context.shareContent(TAG, mainActivityEvent.text, mainActivityEvent.imageUri)
                    }
                    is MainActivityEvent.ShowSnackbar -> {
                        Toast.makeText(context, mainActivityEvent.message, Toast.LENGTH_SHORT).show()
                    }
                    is MainActivityEvent.Navigate -> {
                        navController.navigate(mainActivityEvent.route) {
                            launchSingleTop = true
                        }
                    }
                    is MainActivityEvent.NavigateBack -> {
                        navController.popBackStack()
                    }
                    is MainActivityEvent.NavigateToRootPage -> {
                        navController.navigate(mainActivityEvent.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            }
            KoinAndroidContext {
                AppTheme {
                    MainActivityScreen(
                        navController = navController,
                        mainActivityDelegatedEvent = delegatedEvent,
                        onMainActivityEventInvoke = { viewModel.executeMainActivityEvent(it) },
                        formTabFeatureEnabled = false,
                    )
                }
            }
        }

        clearOldFiles()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
