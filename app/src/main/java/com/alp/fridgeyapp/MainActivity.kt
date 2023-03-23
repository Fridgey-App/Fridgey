package com.alp.fridgeyapp
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alp.fridgeyapp.ui.theme.FridgeyTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.sign

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var firebaseAuth: FirebaseAuthManager
    lateinit var googleAuth : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAuth = GoogleSignIn.getClient(this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestId()
                .requestProfile()
                .build())
        setContent { FridgeyNavHost(googleAuth) }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        googleAuth.signOut().addOnSuccessListener {
            Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
            Toast.makeText(this, "Sign Out Failed", Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    fun FridgeyNavHost(googleSignInClient: GoogleSignInClient) {
        var navController = rememberNavController()

        FridgeyTheme(darkTheme = false) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                NavHost(navController = navController, startDestination = "splashscreen") {
                    composable("splashscreen") { SplashScreen(googleSignInClient, firebaseAuth = firebaseAuth.getFirebaseAuth(), navController = navController) }
                    composable("main") { AppBottomNavigation { signOut() } }
                }
            }
        }
    }
}



