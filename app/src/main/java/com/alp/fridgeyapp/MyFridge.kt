package com.alp.fridgeyapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.alp.fridgeyapp.ui.theme.FridgeyTheme

@Composable
fun MyFridge(onSignOut: () -> Unit) {
    FridgeyTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
                Text(text = "MyFridge")
                Text(text = "LOG OUT BY CLICKING BUTTON BELOW")
                Button(onClick = onSignOut) {
                    
                }
            }
        }
    }
}