package com.ensar.fridgeyapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alp.fridgeyapp.AppBottomNavigation
import com.alp.fridgeyapp.HomeScreen
import com.alp.fridgeyapp.MainScreen
import com.alp.fridgeyapp.Screen

lateinit var navController: NavHostController

@Composable
fun Navigation(signInClicked: () -> Unit, signOutClicked: () -> Unit) {
    navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(signInClicked)
        }
        composable(route = Screen.AppBottomNavigation.route) {
            AppBottomNavigation(signOutClicked)
        }
    }
}

fun getNav(): NavHostController {
    return navController
}
