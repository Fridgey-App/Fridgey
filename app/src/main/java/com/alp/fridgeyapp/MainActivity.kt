package com.alp.fridgeyapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.alp.fridgeyapp.ui.theme.FridgeyTheme
import com.ensar.fridgeyapp.Navigation
import com.ensar.fridgeyapp.getNav
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {

    // Auth variables
    companion object {
        const val RC_SIGN_IN = 100
    }
    lateinit var mAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Auth
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            Navigation({ SignIn() }, {SignOut()})
        }
    }

        fun SignIn() {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    fun SignOut() {
        val googleSignInClient: GoogleSignInClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth.signOut()
        googleSignInClient.signOut().addOnSuccessListener {
            Toast.makeText(this, "Sign Out Succesfull", Toast.LENGTH_SHORT).show()
            getNav().navigate(Screen.MainScreen.route)
        }
            .addOnFailureListener {
            Toast.makeText(this, "Sign Out Failed", Toast.LENGTH_SHORT).show()
        }
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val exception = task.exception
                if (task.isSuccessful) {
                    try {
                        val account = task.getResult(ApiException::class.java)!!
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: Exception) {
                        Log.d("SignIn", "Google SignIn Failed")
                    }
                } else {
                    Log.d("SignIn", exception.toString())
                }
            }


    }
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "SignIn Succesfull", Toast.LENGTH_SHORT).show()
                    getNav().navigate(Screen.AppBottomNavigation.route)
                    // for later usage ==>> val user: FirebaseUser = mAuth.currentUser!!
                } else {
                    Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
                }
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
fun LoginBottomCard(signInClicked: () -> Unit) {
    Card(elevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
            .padding(50.dp)
            .height(175.dp)) {
            Button(onClick = {}, Modifier.width(210.dp)) {
                FridgeyText(t = "sign up", isBold = true)
            }
            Button(onClick = signInClicked

                //navController.navigate(Screen.AppBottomNavigation.route)
            , Modifier.width(210.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
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
fun MainScreen(signInClicked: () -> Unit) {
    FridgeyTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                LoginTopCard()
                Spacer(modifier = Modifier.height(20.dp))
                LoginBottomCard(signInClicked)
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    MainScreen(navController = navController)
//}