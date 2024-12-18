package com.yome.dildiy.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yome.dildiy.MainActivity
import com.yome.dildiy.R
import com.yome.dildiy.design.system.MyTextField
import com.yome.dildiy.remote.dto.User
import com.yome.dildiy.ui.ecommerce.createProduct.LoginVm
import com.yome.dildiy.util.PreferencesHelper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginVm = getKoin().get(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val usernameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }

    val loginState by loginViewModel.loginState.collectAsState()

    val flag = remember { mutableStateOf(0) }

    // Handling the login state (success, loading, error)
    when (val state = loginState.toUiState()) {
        is LoginVm.LoginScreenState.Loading -> {
            CircularProgressIndicator()
        }

        is LoginVm.LoginScreenState.Success -> {
            // Example of the response data (you'll get this from the server)
            val jwtToken = state.responseData?.jwtToken ?: null // Get JWT token from response
            val user = state.responseData?.user // Get user data from response

            // Save the JWT token in SharedPreferences
            if (jwtToken != null) {
                PreferencesHelper.saveJwtToken(context, jwtToken)
            }


            val intent = Intent(context, MainActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(context, intent, null)


            // Save the user data (if available) in SharedPreferences
            user?.let { PreferencesHelper.saveUser(context, it) }

            flag.value = 1
        }

        is LoginVm.LoginScreenState.Error -> {
        }

        else -> {}
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column {
            LottieAnimationView3(R.raw.login2)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Login",
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp
            )
        }

        MyTextField(
            textFieldState = usernameState.value,
            hint = "Username",
            leadingIcon = Icons.Outlined.Email,
            keyboardType = KeyboardType.Email,
            onValueChange = { usernameState.value = it },
            modifier = Modifier.fillMaxWidth()
        )

        MyTextField(
            textFieldState = passwordState.value,
            hint = "Password",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            onValueChange = { passwordState.value = it },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                // Trigger login when button is clicked
                val user = User(
                    username = usernameState.value.text,
                    password = passwordState.value.text,
                    email = "",
                    name = "",
                    isEnabled = true
                )
                loginViewModel.login(user)
                // Navigate to profile if login is successful
                // Use LaunchedEffect to introduce a delay after the login attempt
                flag.value = 1
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = usernameState.value.text.isNotEmpty() && passwordState.value.text.isNotEmpty()
        ) {
            Text(
                text = "Login",
                fontSize = 17.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = 16.sp,
            )
            Text(
                text = "Register",
                fontSize = 16.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.clickable {
                    navController.navigate("register")
                }
            )
        }
    }
}

// Function to save User object to SharedPreferences
fun saveUser(context: Context, user: String) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preference", Context.MODE_PRIVATE)

    val userJson = Json.encodeToString(user)  // Convert User object to JSON string

    println("UserJson " + userJson)
    // Save the JSON string in SharedPreferences
    sharedPreferences.edit().putString("user_preference", userJson).apply()
}


@Composable
fun LottieAnimationView3(resources: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resources))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Keep playing the animation
    )

    // Center the animation in a constrained Box
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth() // Limit to parent width
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(300.dp) // Control size explicitly

        )


    }
}

