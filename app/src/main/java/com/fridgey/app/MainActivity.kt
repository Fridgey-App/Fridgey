package com.fridgey.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.fridgey.app.screen.MainScreen
import com.fridgey.app.screen.ScanScreen
import com.fridgey.app.screen.SplashScreen
import com.fridgey.app.service.AuthService
import com.fridgey.app.ui.theme.FridgeyTheme
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var auth : AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FridgeyMainApp() }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun FridgeyMainApp() {
        val navController = rememberAnimatedNavController()

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
                AnimatedNavHost(navController = navController, startDestination = startDestination,
                    enterTransition = {
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                    },
                    exitTransition = {
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                    },
                    popEnterTransition = {
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                    },
                    popExitTransition = {
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                    }) {
                    composable(SPLASH) { SplashScreen(hiltViewModel()) }
                    composable(MAIN) { MainScreen(onScanPressed = { navController.navigate(SCAN) { launchSingleTop = true } }) }
                    composable(SCAN) { ScanScreen(onBackPressed = { navController.popBackStack() }) }
                }
            }
        }
    }

}



