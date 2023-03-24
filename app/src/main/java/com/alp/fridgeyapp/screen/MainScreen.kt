package com.alp.fridgeyapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alp.fridgeyapp.MAIN_HOME
import com.alp.fridgeyapp.MAIN_MY_FRIDGE
import com.alp.fridgeyapp.MAIN_SCAN
import com.alp.fridgeyapp.ui.theme.DividerColor
import androidx.compose.material.Divider
import androidx.compose.ui.graphics.Color

sealed class BottomBarItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomBarItem(
        route = MAIN_HOME,
        title = "Home",
        icon = Icons.Default.Home
    )

    object Scan : BottomBarItem(
        route = MAIN_SCAN,
        title = "Scan",
        icon = Icons.Default.CameraEnhance
    )

    object MyFridge : BottomBarItem(
        route = MAIN_MY_FRIDGE,
        title = "My Fridge",
        icon = Icons.Default.Face
    )
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navController = navController)}) { padding ->
        NavHost(navController = navController, BottomBarItem.Home.route, modifier = Modifier.padding(padding)) {
            composable(route = BottomBarItem.Home.route) {
                HomeScreen(hiltViewModel())
            }
            composable(route = BottomBarItem.Scan.route) {
                ScanScreen()
            }
            composable(route = BottomBarItem.MyFridge.route) {
                MyFridgeScreen()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarItem.Home,
        BottomBarItem.Scan,
        BottomBarItem.MyFridge,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column {
        Divider(color = DividerColor, thickness = 3.dp)
        BottomNavigation(
            backgroundColor = Color.White
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarItem,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    BottomNavigationItem(
        label = { Text(text = screen.title)},
        icon = {Icon(imageVector = screen.icon, contentDescription = screen.route)},
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            navController.navigate(screen.route)
        }
    )
}