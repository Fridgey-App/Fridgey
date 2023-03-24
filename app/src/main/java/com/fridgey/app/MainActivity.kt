package com.fridgey.app
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fridgey.app.screen.MainScreen
import com.fridgey.app.screen.ScanScreen
import com.fridgey.app.screen.SplashScreen
import com.fridgey.app.screen.SplashScreenViewModel
import com.fridgey.app.service.AuthService
import com.fridgey.app.ui.theme.FridgeyTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var auth : AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent { FridgeyNavHost() }
    }

    @Composable
    fun FridgeyNavHost() {
        val navController = rememberNavController()

        val startDestination = if (auth.hasUser)
            MAIN
        else
            SPLASH

        auth.addStateChangeListener {
            run {
                if (auth.hasUser)
                    navController.navigate(MAIN)
                else
                    navController.navigate(SPLASH)
            }
        }

        FridgeyTheme(darkTheme = false) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                NavHost(navController = navController, startDestination = startDestination) {
                    composable(SPLASH) { SplashScreen(hiltViewModel()) }
                    composable(MAIN) { MainScreen(navController) }
                    composable(SCAN) { ScanScreen() }
                }
            }
        }
    }

}



