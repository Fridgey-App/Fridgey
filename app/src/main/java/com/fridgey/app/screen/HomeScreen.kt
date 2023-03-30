package com.fridgey.app.screen
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fridgey.app.R
import com.fridgey.app.service.AuthService
import com.fridgey.app.service.FoodItemData
import com.fridgey.app.service.LoadState
import com.fridgey.app.ui.theme.*
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val auth: AuthService) : ViewModel()

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(15.dp)) {
            Image(painter = painterResource(R.drawable.app_logo), contentDescription = "Main Logo")
            HomeText(t = "welcome back \uD83D\uDC4B, ${viewModel.auth.userDisplayName}", isBold = true, fSize = 20)
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                HomeTextInCircle("2 items are spoiling", 0.6f, FirstCircleColor)
                Spacer(modifier = Modifier.size(25.dp))
                HomeTextInCircle("3 days of      food left", 0.75f, SecondCircleColor)
                Spacer(modifier = Modifier.size(25.dp))
                HomeTextInCircle("5 day streak", 0.4f, ThirdCircleColor)
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(color = HomeCommunityBg), horizontalArrangement = Arrangement.Center) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(0.5f)) {
                CommunityColumn(title = "near spoilage⚠️", content = arrayOf(
                    FakeFoodItemData("12 organic eggs", "Carrefour", "https://images.openfoodfacts.org/images/products/800/135/002/0375/front_it.22.200.jpg"),
                    FakeFoodItemData("Poulet rôti mayonnaise", "Daunat", "https://images.openfoodfacts.org/images/products/336/765/100/0054/front_fr.50.200.jpg")
                ))
            }
            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(0.5f)) {
                CommunityColumn(title = "recommended for your diet", content = arrayOf(
                    FakeFoodItemData("Concombres au Fromage Blanc et Ciboulette", "Bonduelle", "https://images.openfoodfacts.org/images/products/308/368/114/9975/front_en.13.400.jpg"),
                    FakeFoodItemData("Organic almonds", "Woodstock", "https://images.openfoodfacts.org/images/products/004/256/300/8253/front_en.4.400.jpg")
                ))
            }
        }
    }
}

@Composable
fun HomeTextInCircle(t : String, progress : Float, color: Color) {
    Box(modifier = Modifier.size(85.dp)){
        CircularProgressIndicator(progress = progress, color = color, modifier = Modifier.fillMaxSize(), strokeWidth = 4.dp)
        Text(t, modifier = Modifier.align(Alignment.Center), fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CommunityColumn(title: String, content: Array<FakeFoodItemData>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(10.dp).fillMaxWidth()
    ) {
        HomeText(t = title, isBold = true, align = TextAlign.Center)
        for (data in content) {
            FakeFoodItemCard(data = data)
        }
    }
}

data class FakeFoodItemData(val name: String, val brand: String, val imageUrl: String)

@Composable
fun FakeFoodItemCard(data: FakeFoodItemData) {
    Card(modifier = Modifier
        .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
        .fillMaxWidth(), elevation = 5.dp) {

        Column(modifier = Modifier.padding(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            AsyncImage(
                model = data.imageUrl,
                contentDescription = "Image of ${data.name}",
                modifier = Modifier.aspectRatio(1.2f),
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
            FridgeyText(data.brand, isBold = true, scale = 0.8f)
            FridgeyText(data.name, scale = 0.8f)
        }
    }
}

