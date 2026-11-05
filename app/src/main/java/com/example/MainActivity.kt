package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.repository.CasinoRepository
import com.example.ui.screens.GameTableDispatcher
import com.example.ui.screens.RoyalAuthScreen
import com.example.ui.screens.RoyalLobbyScreen
import com.example.ui.screens.signOutUser
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CasinoViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RoyalCasinoAppRoot(
                    onRequestOrientation = { landscape ->
                        requestedOrientation = if (landscape) {
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RoyalCasinoAppRoot(
    onRequestOrientation: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val firebaseAuth = remember {
        runCatching {
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                Firebase.auth
            } else {
                null
            }
        }.getOrNull()
    }

    val authFlow = remember(firebaseAuth) {
        firebaseAuth?.authStateFlow() ?: kotlinx.coroutines.flow.flowOf(null)
    }
    val currentUser by authFlow.collectAsStateWithLifecycle(initialValue = firebaseAuth?.currentUser)
    var isGuestSession by remember { androidx.compose.runtime.mutableStateOf(false) }
    val user = currentUser

    if (user == null && !isGuestSession) {
        RoyalAuthScreen(
            onAuthSuccess = { isGuestSession = true },
            onGuestLogin = { isGuestSession = true }
        )
    } else {
        val activeUserId = user?.uid ?: "guest_7481269"
        val activeDisplayName = user?.displayName ?: "Guest7481269"
        AuthenticatedCasinoShell(
            currentUserId = activeUserId,
            displayName = activeDisplayName,
            onRequestOrientation = onRequestOrientation,
            onSignedOut = { isGuestSession = false }
        )
    }
}

@Composable
private fun AuthenticatedCasinoShell(
    currentUserId: String,
    displayName: String,
    onRequestOrientation: (Boolean) -> Unit,
    onSignedOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val viewModel: CasinoViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = runCatching {
                    if (com.google.firebase.FirebaseApp.getApps(app).isNotEmpty()) {
                        FirebaseFirestore.getInstance(databaseId)
                    } else {
                        null
                    }
                }.getOrNull()
                CasinoViewModel(
                    repository = CasinoRepository(db),
                    currentUserId = currentUserId,
                    initialDisplayName = displayName
                )
            }
        }
    )

    val activeGame by viewModel.activeGame.collectAsStateWithLifecycle()
    val isLandscape by viewModel.isLandscapeMode.collectAsStateWithLifecycle()

    LaunchedEffect(isLandscape) {
        onRequestOrientation(isLandscape)
    }

    if (activeGame == null) {
        RoyalLobbyScreen(
            viewModel = viewModel,
            onSignOut = {
                signOutUser(
                    context = context,
                    onSignOutComplete = onSignedOut,
                    scope = scope
                )
            }
        )
    } else {
        GameTableDispatcher(
            game = activeGame!!,
            viewModel = viewModel
        )
    }
}
