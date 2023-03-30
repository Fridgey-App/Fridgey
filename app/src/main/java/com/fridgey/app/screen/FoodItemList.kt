package com.fridgey.app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.fridgey.app.service.*
import com.fridgey.app.ui.theme.FridgeyText
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodItemListViewModel @Inject constructor(val user: FridgeService) : ViewModel()

@Composable
fun FoodItemList(itemList: List<FoodItem>, viewModel: FoodItemListViewModel, onRemovePressed: (String) -> Unit) {
    val foodItemData by viewModel.user.getFoodItemData(itemList).observeAsState(emptyList())

    if (itemList.isEmpty()) {
        Box(Modifier.fillMaxSize()) {
            FridgeyText("empty :(", isBold = true, modifier = Modifier.align(Alignment.Center))
        }
    }
    else {
        LazyColumn(content = {
            items(foodItemData) { i -> FoodItemCard(p = i, onRemovePressed) }
        })
    }

}

@Composable
fun FoodItemCard(p: Pair<LoadState, FoodItemData?>, onRemovePressed: (String) -> Unit) {
    var shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Card(modifier = Modifier
        .height(70.dp)
        .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
        .fillMaxWidth(), elevation = 5.dp) {

        when (p.first) {
            LoadState.LOADING -> Column(verticalArrangement = Arrangement.Center, modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .shimmer(shimmer)
                    .background(color = Color.LightGray))
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .shimmer(shimmer)
                    .background(color = Color.LightGray))
            }
            LoadState.ERROR ->  Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Icon(
                    Icons.Outlined.ErrorOutline,
                    contentDescription = "unknown item",
                    modifier = Modifier
                        .size(25.dp)
                        .aspectRatio(1f),
                    tint = Color.Black
                )
                Column(modifier = Modifier.weight(0.9f)) {
                    val brandName = "Item not recognised"
                    FridgeyText(brandName, isBold = true)
                    FridgeyText("Please scan again")
                }
                IconButton(onClick = { onRemovePressed(p.second!!.code) }, modifier = Modifier.weight(0.1f)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "remove item",
                        modifier = Modifier.size(25.dp),
                        tint = Color.Black
                    )
                }
            }
            LoadState.READY -> Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                AsyncImage(
                    model = p.second!!.product.image_front_thumb_url,
                    contentDescription = "Image of ${p.second!!.product.product_name}",
                    modifier = Modifier.aspectRatio(1f),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
                Column(modifier = Modifier.weight(0.9f)) {
                    val brandName = p.second!!.product.brands ?: "Unknown Brand"
                    FridgeyText(brandName, isBold = true)
                    FridgeyText(p.second!!.product.product_name)
                }
                IconButton(onClick = { onRemovePressed(p.second!!.code) }, modifier = Modifier.weight(0.1f)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "remove item",
                        modifier = Modifier.size(25.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}