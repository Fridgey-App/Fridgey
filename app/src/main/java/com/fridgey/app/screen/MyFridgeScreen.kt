package com.fridgey.app.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.fridgey.app.service.AuthService
import com.fridgey.app.service.FoodItem
import com.fridgey.app.service.FridgeService
import com.fridgey.app.ui.theme.FridgeyText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFridgeViewModel @Inject constructor(val auth: AuthService, val fridge: FridgeService) : ViewModel()

@Composable
fun MyFridgeScreen(viewModel: MyFridgeViewModel) {
   val foodItemList = remember { mutableStateListOf<FoodItem>() }
   val coroutineScope = rememberCoroutineScope()

   LaunchedEffect(Unit) {
      foodItemList.addAll(viewModel.fridge.getFridgeContents())
   }

   fun onRemoveHandler(barcode: String) {
      foodItemList.removeIf { fi -> fi.barcode == barcode }
      coroutineScope.launch {
         viewModel.fridge.removeFridgeItem(barcode)
      }
   }

   Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
      MyFridgeTopBar(itemCount = foodItemList.size)
      FoodItemList(itemList = foodItemList, viewModel = hiltViewModel(), onRemovePressed = { b -> onRemoveHandler(b) })
   }
}

@Composable
fun MyFridgeTopBar(itemCount: Int) {
   Card(modifier = Modifier.fillMaxWidth(), elevation = 5.dp, shape = RectangleShape) {
      Row(modifier = Modifier.padding(5.dp)) {
         IconButton(onClick = { /*TODO*/ }, enabled = false) {
            Icon(
               Icons.Filled.Kitchen,
               contentDescription = "fridge icon",
               modifier = Modifier.size(25.dp),
               tint = Color.Black
            )
         }
         Column {
            FridgeyText(t = "my fridge", isBold = true)
            FridgeyText("$itemCount items")
         }
      }
   }
}
