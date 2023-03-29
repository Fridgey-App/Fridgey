package com.fridgey.app.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.lifecycle.ViewModel
import com.fridgey.app.service.AuthService
import com.fridgey.app.service.FoodItemInfoService
import com.fridgey.app.service.FridgeService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyFridgeViewModel @Inject constructor(val auth: AuthService, val user: FridgeService, val barcodeInfoService: FoodItemInfoService) : ViewModel()

@Composable
fun MyFridgeScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
        Text(text = "MyFridge")
    }
}