package com.fridgey.app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
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
fun FoodItemList(itemList: List<FoodItem>, viewModel: FoodItemListViewModel) {
    val foodItemData by viewModel.user.getFoodItemData(itemList).observeAsState(emptyList())

    LazyColumn(content = {
        items(foodItemData) { i -> FoodItemCard(p = i) }
    })
}

@Composable
fun FoodItemCard(p: Pair<LoadState, FoodItemData?>) {
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
            LoadState.ERROR -> Text("Failed to get product info :(")
            LoadState.READY -> Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                AsyncImage(
                    model = p.second!!.product.image_front_thumb_url,
                    contentDescription = "Image of ${p.second!!.product.product_name}",
                    modifier = Modifier.aspectRatio(1f),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
                Column {
                    val brandName = p.second!!.product.brands ?: "Unknown Brand"
                    FridgeyText(brandName, isBold = true)
                    FridgeyText(p.second!!.product.product_name)
                }
            }
        }
    }
}