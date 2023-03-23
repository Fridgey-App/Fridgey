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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alp.fridgeyapp.ui.theme.FridgeyTheme
import com.google.android.gms.auth.api.Auth
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

    @Inject lateinit var auth : AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { FridgeyNavHost() }
    }

    @Composable
    fun FridgeyNavHost() {
        val navController = rememberNavController()

        val startDestination = if (auth.hasUser)
            MAIN_SCREEN
        else
            SPLASH_SCREEN

        auth.addStateChangeListener {
            run {
                if (auth.hasUser)
                    navController.navigate(MAIN_SCREEN)
                else
                    navController.navigate(SPLASH_SCREEN)
            }
        }

        FridgeyTheme(darkTheme = false) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                NavHost(navController = navController, startDestination = startDestination) {
                    composable(SPLASH_SCREEN) {
                        val viewModel = hiltViewModel<SplashScreenViewModel>()
                        SplashScreen(viewModel)
                    }
                    composable(MAIN_SCREEN) { AppBottomNavigation() }
                }
            }
        }
    }

}



