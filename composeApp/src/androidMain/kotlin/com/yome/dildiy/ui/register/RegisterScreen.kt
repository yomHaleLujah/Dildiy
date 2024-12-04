package com.yome.dildiy.ui.register

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yome.dildiy.R
import com.yome.dildiy.design.system.MyTextField
import com.yome.dildiy.remote.dto.User
import com.yome.dildiy.networking.DildiyClient
import com.yome.dildiy.ui.login.saveUser
import org.koin.mp.KoinPlatform.getKoin


/**
 * @author Ahmed Guedmioui
 */
//registerViewModel: RegisterViewModel = viewModel()
@OptIn(ExperimentalFoundationApi::class)
//registerViewModel: RegisterViewModel = viewModel(),
@Composable
fun RegisterScreen(navController: NavController, registerViewModel: RegisterVm = getKoin().get(), client: DildiyClient, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val confirmPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val userName = remember { mutableStateOf(TextFieldValue()) }

    val registerState by registerViewModel.loginState.collectAsState()

    // Handling the login state (success, loading, error)
    when (val state = registerState.toUiState()) {
        is RegisterVm.RegisterScreenState.Loading -> {
            CircularProgressIndicator()
        }
        is RegisterVm.RegisterScreenState.Success -> {
            // Login was successful, save user and navigate to profile
            Toast.makeText(context, "Registered Successfully, Please check your email for verification.", Toast.LENGTH_LONG).show()
            state.responseData?.let { saveUser(context, it) }  // Save the user data to SharedPreferences
            navController.navigate("login") {
                popUpTo("register") {
                    inclusive = true
                }
            }
        }
        is RegisterVm.RegisterScreenState.Error -> {
            // Show error if login fails
//            Toast.makeText(context, "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
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

            Image(
                painter = painterResource(R.drawable.register),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth(0.25f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Register",
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp
            )
        }



        MyTextField(
            textFieldState = emailState.value,
            hint = "Email",
            leadingIcon = Icons.Outlined.Email,
            trailingIcon = Icons.Outlined.Check,
            keyboardType = KeyboardType.Email,
            onValueChange = {  emailState.value = it },
            modifier = Modifier.fillMaxWidth()
        )



        MyTextField(
            textFieldState = nameState.value,
            hint = "Name",
            leadingIcon = Icons.Outlined.AccountCircle,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {nameState.value = it}
        )
        MyTextField(
            textFieldState = userName.value,
            hint = "User name",
            leadingIcon = Icons.Outlined.AccountCircle,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {userName.value = it}
        )

        MyTextField(
            textFieldState = passwordState.value,
            hint = "Password",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {passwordState.value = it}
        )

        MyTextField(
            textFieldState = confirmPasswordState.value,
            hint = "Re type password",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {confirmPasswordState.value = it}
        )

        println(emailState.value.text)
        // Register Button
        Button(
            onClick = {
                println("email"+emailState.value.text)

                // Create User object from the state values
                val user = User(
                    id = 11,
                    email = emailState.value.text,
                    name = nameState.value.text,
                    password = passwordState.value.text,
                    username = userName.value.text, isEnabled = true
                )
//                 Call the ViewModel function to register the user
                registerViewModel.register(user)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailState.value.text.isNotEmpty() && nameState.value.text.isNotEmpty() && passwordState.value.text.isNotEmpty() && confirmPasswordState.value.text.isNotEmpty()
        ) {
            Text(
                text = "Register",
                fontSize = 17.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Already have an account? ",
                fontSize = 16.sp,
            )
            Text(
                text = "Login",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }

        Spacer(modifier = Modifier.height(1.dp))
    }
}