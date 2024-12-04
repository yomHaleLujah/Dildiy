
package com.yome.dildiy.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.yome.dildiy.R
import com.yome.dildiy.design.system.MyTextField
import com.yome.dildiy.remote.dto.User
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import com.yome.dildiy.ui.ecommerce.createProduct.LoginVm
import com.yome.dildiy.util.PreferencesHelper
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginVm = getKoin().get(), modifier: Modifier = Modifier) {
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

            // Save the user data (if available) in SharedPreferences
            user?.let { PreferencesHelper.saveUser(context, it) }

            // Navigate to Profile screen
//            if (PreferencesHelper.getJwtToken(context)!=null&&flag ==0){
//                println("Token " + PreferencesHelper.getJwtToken(context))
//                navController.navigate("Profile")
//            }
//            flag.value = 1
        }
        is LoginVm.LoginScreenState.Error -> {

            // Show error if login fails
//            Toast.makeText(context, "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
        }
        else -> {}
    }

    LaunchedEffect(flag.value) {
        if (flag.value == 1) {
            delay(2000)  // Wait for 2 seconds
            Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
            if (PreferencesHelper.getJwtToken(context) != null) {
                navController.navigate("Profile") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true // Avoid multiple instances
                }
            }
        } else {
            flag.value = 0  // Reset flag to 0 when login is not successful
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column {
            Image(
                painter = painterResource(R.drawable.login),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

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
                val user = User(username = usernameState.value.text, password = passwordState.value.text, email = "", name = "", isEnabled = true)
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
@Composable
fun checkLoginAndNavigate(context: Context, navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000)  // Wait for 2 seconds

        if (PreferencesHelper.getJwtToken(context) != null) {
            navController.navigate("Profile") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true // Avoid multiple instances
            }
        } else {
            Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show()
        }
    }
}
// Function to save User object to SharedPreferences
fun saveUser(context: Context, user: String) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preference", Context.MODE_PRIVATE)

    val userJson = Json.encodeToString(user)  // Convert User object to JSON string

    println("UserJson " +userJson)
    // Save the JSON string in SharedPreferences
    sharedPreferences.edit().putString("user_preference", userJson).apply()
}

/*
package com.yome.dildiy.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.yome.dildiy.R
import com.yome.dildiy.design.system.MyTextField

@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier) {

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Column {
                Image(
                    painter = painterResource(R.drawable.login),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxWidth(0.25f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Login",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp
                )
            }

            MyTextField(
                textFieldState = TextFieldValue(),
                hint = "Email",
                leadingIcon = Icons.Outlined.Email,
                trailingIcon = Icons.Outlined.Check,
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {}
            )

            MyTextField(
                textFieldState = TextFieldValue(),
                hint = "Password",
                leadingIcon = Icons.Outlined.Lock,
                trailingText = "Forgot?",
                isPassword = true,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {}
            )

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Login",
                    fontSize = 17.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Text(
                text = "",
                fontSize = 15.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .alpha(0.5f)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Spacer(modifier = Modifier.height(1.dp))
            Spacer(modifier = Modifier.height(1.dp))

            Spacer(modifier = Modifier.height(1.dp))
            Spacer(modifier = Modifier.height(1.dp))
            Spacer(modifier = Modifier.height(1.dp))

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
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                    }
                )
            }

            Spacer(modifier = Modifier.height(1.dp))
        }
    }


}*/


