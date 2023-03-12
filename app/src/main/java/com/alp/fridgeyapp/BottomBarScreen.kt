package com.alp.fridgeyapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Scan : BottomBarScreen(
        route = "scan",
        title = "Scan",
        icon = Icons.Default.Search
    )

    object MyFridge : BottomBarScreen(
        route = "my_fridge",
        title = "my_fridge",
        icon = Icons.Default.Face
    )
}