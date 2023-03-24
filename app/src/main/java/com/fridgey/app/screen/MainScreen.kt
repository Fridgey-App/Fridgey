package com.fridgey.app.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
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
import com.fridgey.app.MAIN_HOME
import com.fridgey.app.MAIN_MY_FRIDGE
import com.fridgey.app.ui.theme.DividerColor
import androidx.compose.material.Divider
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.fridgey.app.SCAN
import com.google.accompanist.systemuicontroller.rememberSystemUiController

sealed class BottomBarItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomBarItem(
        route = MAIN_HOME,
        title = "Home",
        icon = Icons.Default.Home
    )

    object Scan : BottomBarItem(
        route = SCAN,
        title = "Scan",
        icon = Icons.Outlined.QrCode2
    )

    object MyFridge : BottomBarItem(
        route = MAIN_MY_FRIDGE,
        title = "My Fridge",
        icon = Icons.Default.Face
    )
}

@Composable
fun MainScreen(globalNavController: NavController) {
    val navController = rememberNavController()
    val uiController = rememberSystemUiController()
    uiController.setSystemBarsColor(Color.White)
    Scaffold(bottomBar = { BottomBar(mainNavController = navController, globalNavController = globalNavController)}, modifier = Modifier.systemBarsPadding()) { padding ->
        NavHost(navController = navController, BottomBarItem.Home.route, modifier = Modifier.padding(padding)) {
            composable(route = BottomBarItem.Home.route) {
                HomeScreen(hiltViewModel())
            }
            composable(route = BottomBarItem.MyFridge.route) {
                MyFridgeScreen()
            }
        }
    }
}

@Composable
fun BottomBar(globalNavController: NavController, mainNavController: NavHostController) {
    val screens = listOf(
        BottomBarItem.Home,
        BottomBarItem.Scan,
        BottomBarItem.MyFridge,
    )
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    fun mainNavHandler(route: String) {
        if (route == SCAN) {
            globalNavController.navigate(SCAN)
        }
        else {
            mainNavController.navigate(route)
        }
    }

    Column {
        Divider(color = DividerColor, thickness = 3.dp)
        BottomNavigation(
            backgroundColor = Color.White
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    onPressHandler = { r -> mainNavHandler(r) }
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarItem,
    currentDestination: NavDestination?,
    onPressHandler: (String) -> Unit
) {
    BottomNavigationItem(
        label = { Text(text = screen.title)},
        icon = {Icon(imageVector = screen.icon, contentDescription = screen.route)},
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = { onPressHandler(screen.route) }
    )
}