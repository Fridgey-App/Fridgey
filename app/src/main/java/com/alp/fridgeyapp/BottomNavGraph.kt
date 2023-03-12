package com.alp.fridgeyapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun BottomNavGraph(navController: NavHostController, onSignOut: () -> Unit) {
    NavHost(navController = navController, BottomBarScreen.Home.route) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen()
        }
        composable(route = BottomBarScreen.Scan.route) {
            Scan()
        }
        composable(route = BottomBarScreen.MyFridge.route) {
            MyFridge(onSignOut)
        }
    }
}