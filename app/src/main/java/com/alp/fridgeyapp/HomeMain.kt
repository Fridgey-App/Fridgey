package com.alp.fridgeyapp
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alp.fridgeyapp.ui.theme.FridgeyTheme
import com.alp.fridgeyapp.ui.theme.HomeCommunityBg
import com.alp.fridgeyapp.ui.theme.caviarFamily

@Composable
fun HomeText(t : String, isBold : Boolean = false, fSize : Int = 19, align: TextAlign = TextAlign.Center) {
    if (isBold)
        Text(text = t, fontSize = fSize.sp, fontFamily = caviarFamily, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = align)
    else
        Text(text = t, fontSize = fSize.sp, fontFamily = caviarFamily, color = Color.Black, textAlign = align)
}

@Composable
fun HomeTextInCircle(t : String = "hello Fridgey", progress : Float = 250f) {
    Text(text = t, fontSize = 10.sp, fontFamily = caviarFamily, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier
        .drawBehind {
            drawCircle(color = Color(0x796D6D6D), radius = 100f, style = Stroke(width = 2.dp.toPx()),
                center = Offset(-35f, 0f))
            drawArc(color = Color(0xC1558AC2), 0f, progress, false,
                style = Stroke(4.dp.toPx(), cap = StrokeCap.Round),
                size = Size(220f, 220f),
                topLeft = Offset(-145f, -110f)
            )
        })
}


@Composable
fun HomeTopCard() {
    Card(elevation = 10.dp, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(30.dp)) {
            // Spacer(modifier = Modifier.size(30.dp))
            Image(painter = painterResource(R.drawable.app_logo), contentDescription = "Main Logo")
            // row içinde el sembolü eklenecek
            HomeText(t = "welcome back, user12445", isBold = true, fSize = 17)
            Spacer(modifier = Modifier.size(30.dp))
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(30.dp)) {
                HomeTextInCircle(t = "hello\nrana", progress = 347f)
                Spacer(modifier = Modifier.size(70.dp))
                HomeTextInCircle(t = "hello\nceyda", progress = 240f)
                Spacer(modifier = Modifier.size(75.dp))
                HomeTextInCircle(t = "hello\nalp", progress = 347f)
            }
            Spacer(modifier = Modifier.size(0.dp))
        }
    }
}

@Composable
fun HomeBottomCard() {
    Card(elevation = 1.dp, modifier = Modifier.fillMaxWidth(), backgroundColor = HomeCommunityBg) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            CommunityColumn(tittle = true, arrayOf("alp is using fridgey!", "ceyda the aimaster", "rana is creating great ui!"))
            CommunityColumn(tittle = false, arrayOf("hi there, rana the coolest is here", "ensar is learning kotlin"))
        }
    }
}

@Composable
fun CommunityColumn(tittle: Boolean = false, content: Array<String>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
        .padding(5.dp)
        .height(2000.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        if (tittle) {
            HomeText(t = "from\nyour community", isBold = true, align = TextAlign.Start)
            Spacer(modifier = Modifier.height(8.dp))
        }
        for (text in content) {
            CommunityButton(text = text)
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun CommunityButton(text: String) {
    Button(onClick = {},
        Modifier
            .width(160.dp)
            .height(180.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(R.drawable.btn_google_light_normal__9), contentDescription = "Sign In Google Logo")
            HomeText(t = text, isBold = true)
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun HomeScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
        HomeTopCard()
        HomeBottomCard()
    }
}