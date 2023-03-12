package com.alp.fridgeyapp

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object AppBottomNavigation : Screen("app_bottom_navigation")
}
