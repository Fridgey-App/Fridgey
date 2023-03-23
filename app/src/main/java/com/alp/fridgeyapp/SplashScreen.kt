package com.alp.fridgeyapp

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alp.fridgeyapp.ui.theme.FridgeyText
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(val auth: AuthService) : ViewModel()

enum class SignInScreenState {
    MAIN, SIGN_UP, SIGN_IN
}

@Composable
fun SplashScreen(viewModel: SplashScreenViewModel = viewModel()) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
        Card(elevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(30.dp)) {
                Image(painter = painterResource(R.drawable.app_logo), contentDescription = "Main Logo")
                FridgeyText(t = "your pocket's best friend", isBold = true)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        LoginBottomCard(viewModel)
    }
}

@Composable
fun LoginBottomCard(viewModel: SplashScreenViewModel) {
    val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val intent = result.data ?: return@rememberLauncherForActivityResult

        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
        val credential = GoogleAuthProvider.getCredential(task.result.idToken, null)
        runBlocking { viewModel.auth.authenticateWithGoogle(credential) }
    }

    var screenState by remember { mutableStateOf(SignInScreenState.MAIN) }
    val coroutineScope = rememberCoroutineScope()

    Card(elevation = 1.dp, modifier = Modifier
        .fillMaxWidth()
        .height(315.dp)) {
        Crossfade(targetState = screenState) { a ->
            when (a) {
                SignInScreenState.MAIN -> SignInMain(
                    onSignUpPressed = { screenState = SignInScreenState.SIGN_UP },
                    onGoogleSignInPressed = { startForResult.launch(viewModel.auth.googleAuth.signInIntent) },
                    onSignInPressed = { screenState = SignInScreenState.SIGN_IN }
                )
                SignInScreenState.SIGN_UP -> SignUp(
                    onBackPressed = { screenState = SignInScreenState.MAIN},
                    onSignUpPressed = { u, e, p -> coroutineScope.launch { viewModel.auth.signUp(u, e, p)} },
                )
                SignInScreenState.SIGN_IN -> SignInEmailPassword(
                    onBackPressed = { screenState = SignInScreenState.MAIN },
                    onSignInPressed = { e, p -> coroutineScope.launch { viewModel.auth.authenticateWithEmail(e, p) } },
                )
            }
        }
    }
}

@Composable
fun SignInMain(onSignUpPressed: () -> Unit, onGoogleSignInPressed: () -> Unit, onSignInPressed: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(80.dp)
            .fillMaxSize(),
    )
    {
        Button(onClick = onSignUpPressed, Modifier.width(210.dp)) {
            FridgeyText(t = "sign up", isBold = true)
        }
        Button(onClick = onGoogleSignInPressed, Modifier.width(210.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.btn_google_light_normal__9), contentDescription = "Sign In Google Logo")
                Text("Sign in with Google", fontSize = 14.sp)
            }
        }
        Row {
            Text("Already have an account?", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(2.dp))
            ClickableText(text = AnnotatedString("sign in"), onClick = { onSignInPressed() })
        }
    }
}

@Composable
fun SignUp(onBackPressed : () -> Unit, onSignUpPressed: (String, String, String) -> Unit) {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
//    var passwordConfirmation by remember { mutableStateOf(TextFieldValue("")) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "go back"
                )
            }
            FridgeyText("sign up", isBold = true)
        }
        OutlinedTextField(
            value = username,
            onValueChange = { text : TextFieldValue -> username = text },
            label = {Text("Username")},
            singleLine = true,
        )
        OutlinedTextField(
            value = email,
            onValueChange = { text : TextFieldValue -> email = text },
            label = {Text("E-mail")},
            singleLine = true,
        )
        OutlinedTextField(
            value = password,
            onValueChange = { text : TextFieldValue -> password = text },
            label = {Text("Password")},
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
//        OutlinedTextField(
//            value = passwordConfirmation,
//            onValueChange = { text : TextFieldValue -> passwordConfirmation = text },
//            label = {Text("Confirm password")},
//            isError = (password != passwordConfirmation),
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation()
//        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { onSignUpPressed(username.text, email.text, password.text) }) {
            FridgeyText(t = "start your journey", isBold = true)
        }
    }
}

@Composable
fun SignInEmailPassword(onBackPressed : () -> Unit, onSignInPressed: (String, String) -> Unit) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "go back"
                )
            }
            FridgeyText("enter your details", isBold = true)
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { text : TextFieldValue -> email = text },
            label = {Text("E-mail")},
            singleLine = true,
        )
        OutlinedTextField(
            value = password,
            onValueChange = { text : TextFieldValue -> password = text },
            label = {Text("Password")},
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { onSignInPressed(email.text, password.text) }) {
            FridgeyText(t = "sign in", isBold = true)
        }
    }
}
