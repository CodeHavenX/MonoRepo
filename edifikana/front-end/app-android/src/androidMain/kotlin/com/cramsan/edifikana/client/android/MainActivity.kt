package com.cramsan.edifikana.client.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.android.managers.FireStoreManager
import com.cramsan.edifikana.client.android.screens.ClockInOutScreen
import com.cramsan.edifikana.client.android.screens.ClockInOutSingleEmployeeScreen
import com.cramsan.edifikana.client.android.screens.EventLogAddItemScreen
import com.cramsan.edifikana.client.android.screens.EventLogSingleItemScreen
import com.cramsan.edifikana.client.android.screens.EventLotScreen
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        viewModel.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Content()
        }

        GlobalScope.launch {
            FireStoreManager().getEmployees()
        }
    }

    fun signIn() {
        // Choose authentication providers
        val providers = arrayListOf(
            //AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }
}

@Preview
@Composable
fun Content() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screens.EventLog.route) {
        composable(Screens.ClockInOut.route) { ClockInOutScreen() }
        composable(Screens.ClockInOutSingleEmployee.route) { ClockInOutSingleEmployeeScreen() }
        composable(Screens.EventLog.route) { EventLotScreen() }
        composable(Screens.EventLogAddItem.route) { EventLogAddItemScreen() }
        composable(Screens.EventLogSingleItem.route) { EventLogSingleItemScreen() }
    }
}
