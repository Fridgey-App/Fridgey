package com.alp.fridgeyapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alp.fridgeyapp.R
import com.alp.fridgeyapp.ui.theme.FridgeyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

val caviarFamily = FontFamily(
    Font(R.font.caviardreams, FontWeight.Normal),
    Font(R.font.caviardreams_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.caviardreams_bold, FontWeight.Bold)
)

@Composable
fun FridgeyText(t : String, isBold : Boolean = false) {
    if (isBold)
        Text(text = t, fontSize = 22.sp, fontFamily = caviarFamily, fontWeight = FontWeight.Bold)
    else
        Text(text = t, fontSize = 22.sp, fontFamily = caviarFamily)
}


@Composable
fun LoginTopCard() {
    Card(elevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(30.dp)) {
            Image(painter = painterResource(R.drawable.app_logo), contentDescription = "Main Logo")
            FridgeyText(t = "your pocket's best friend", isBold = true)
        }
    }
}

@Composable
fun LoginBottomCard() {
    Card(elevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.padding(50.dp).height(175.dp)) {
            Button(onClick = {}, Modifier.width(210.dp)) {
                FridgeyText(t = "sign up", isBold = true)
            }
            Button(onClick = {}, Modifier.width(210.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.btn_google_light_normal__9), contentDescription = "Sign In Google Logo")
                    Text("Sign in with Google", fontSize = 14.sp)
                }
            }
            Row {
                Text("Already have an account?", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(2.dp))
                Text("sign in", textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Main() {
    FridgeyTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                LoginTopCard()
                Spacer(modifier = Modifier.height(20.dp))
                LoginBottomCard()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    Main()
}