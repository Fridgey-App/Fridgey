package com.alp.fridgeyapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alp.fridgeyapp.screen.MainScreen
import com.alp.fridgeyapp.screen.SplashScreen
import com.alp.fridgeyapp.screen.SplashScreenViewModel
import com.alp.fridgeyapp.service.AuthService
import com.alp.fridgeyapp.ui.theme.FridgeyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
                    composable(SPLASH) {
                        val viewModel = hiltViewModel<SplashScreenViewModel>()
                        SplashScreen(viewModel)
                    }
                    composable(MAIN) { MainScreen() }
                }
            }
        }
    }

}



