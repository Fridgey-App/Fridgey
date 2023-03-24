package com.alp.fridgeyapp.screen
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alp.fridgeyapp.R
import com.alp.fridgeyapp.service.AuthService
import com.alp.fridgeyapp.ui.theme.FridgeyText
import com.alp.fridgeyapp.ui.theme.HomeCommunityBg
import com.alp.fridgeyapp.ui.theme.HomeText
import com.alp.fridgeyapp.ui.theme.caviarFamily
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val auth: AuthService) : ViewModel()

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(20.dp)) {
            Image(painter = painterResource(R.drawable.app_logo), contentDescription = "Main Logo")
            HomeText(t = "welcome back \uD83D\uDC4B, ${viewModel.auth.userDisplayName}", isBold = true, fSize = 20)
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(25.dp)) {
                HomeTextInCircle(t = "hello\nrana", progress = 347f)
                Spacer(modifier = Modifier.size(70.dp))
                HomeTextInCircle(t = "hello\nceyda", progress = 240f)
                Spacer(modifier = Modifier.size(75.dp))
               HomeTextInCircle(t = "hello\nalp", progress = 347f)
            }
            Button(onClick = { viewModel.auth.signOut() }) {
                FridgeyText("Temp. Sign Out")
            }
        }
        Row(modifier = Modifier.fillMaxWidth().background(color = HomeCommunityBg), horizontalArrangement = Arrangement.SpaceEvenly) {
            CommunityColumn(
                title = true,
                arrayOf(
                    "alp is using fridgey!",
                    "ceyda the ai-master",
                    "rana is creating great ui!"
                )
            )
            CommunityColumn(
                title = false,
                arrayOf("hi there, rana the coolest is here", "ensar is learning kotlin")
            )
        }
    }
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
        }
    )
}

@Composable
fun CommunityColumn(title: Boolean = false, content: Array<String>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
        .padding(5.dp)
        .height(2000.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        if (title) {
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
