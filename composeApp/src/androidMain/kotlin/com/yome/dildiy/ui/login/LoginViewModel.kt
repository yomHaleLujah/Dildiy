/*
package com.yome.dildiy.ui.login


import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.yome.dildiy.remote.dto.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.yome.dildiy.networking.DildiyClient
import com.yome.dildiy.ui.ecommerce.navigation.AppBottomNavigation


import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.UnknownHostException

// Define the ViewModel to manage registration state
class LoginViewModel : ViewModel(), KoinComponent {

    private val registerClient: DildiyClient by inject()  // Inject RegisterClient using Koin

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    // Function to register user
// Assuming User and Error are predefined classes
    fun loginUser(
        context: Context,
        dildiyClient: DildiyClient,
        user: User,
        navController: NavController
    ) {
        println("hi")
        println(user)
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                when (val result = dildiyClient.loginUser(user)) {
                    is com.yome.dildiy.util.Result.Success -> {
                        _loginState.value = LoginState.Success(result.data) // Success case

                        saveUser(context, result.data)  // Save the user data
                        // Navigate to the login screen on success
                        navController.navigate("Profile") {
                            popUpTo("login") {
                                inclusive = true
                            }  // Remove the login screen from the back stack
                        }

                    }

                    is com.yome.dildiy.util.Result.Error -> {
                        println("hi error" + result.error.toString())
                        _loginState.value =
                            LoginState.Error(result.error.toString() ?: "Unknown error occurred")
                    }
                }
            } catch (e: UnknownHostException) {
                // Handle server down / no internet connection
                _loginState.value =
                    LoginState.Error("No internet connection. Please check your connection.")
            } catch (e: Exception) {
                // Catch any other exceptions
                println("hi errors" + e.toString())
                _loginState.value = LoginState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }


    // Sealed class to represent different states of registration
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val error: String) : LoginState()
    }

    // Function to save User object to SharedPreferences
    fun saveUser(context: Context, user: User) {
        // Get the SharedPreferences instance
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("user_preference", Context.MODE_PRIVATE)

        // Convert the User object to a JSON string using Kotlinx Serialization
        val userJson = Json.encodeToString(user)

        // Save the JSON string in SharedPreferences
        sharedPreferences.edit().putString("user_preference", userJson).apply()
    }

}

*/


package com.yome.dildiy.ui.ecommerce.createProduct

import androidx.lifecycle.viewModelScope
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.LoginRepository
import com.yome.dildiy.networking.UserResponse
import com.yome.dildiy.remote.dto.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginVm(private val loginRepository: LoginRepository) :
    androidx.lifecycle.ViewModel() {
    val name = MutableStateFlow("")
    val price = MutableStateFlow("")
    val stock = MutableStateFlow("")
    val description = MutableStateFlow("")

//    private val uploadProductViewState: MutableStateFlow<UploadProductScreenState> =
//        MutableStateFlow(ProductScreenState.Loading)

    // Internal state that holds the HomeState
    private val _login = MutableStateFlow(LoginState())
    val loginState = _login.asStateFlow()  // Expose as StateFlow for UI to observe

    // Function to fetch product details
    fun login(user: User) {
        // Use viewModelScope to automatically manage coroutines tied to the ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Fetch product details from repository in IO dispatcher
                loginRepository.login(user).collect { response ->
                    // Get the current state before modifying it
                    val currentState = _login.value

                    when (response.status) {
                        ApiStatus.LOADING -> {
                            // Update state to loading
                            _login.update { currentState.copy(isLoading = true, errorMessage = null) }
                        }
                        ApiStatus.SUCCESS -> {
                            // Handle successful response
                            val updatedState = if (response.data != null) {
                                currentState.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    responseData = response.data
                                )
                            } else {
                                currentState.copy(
                                    isLoading = false,
                                    errorMessage = "Login not found",
                                    responseData = null
                                )
                            }
                            _login.update { updatedState }
                        }
                        ApiStatus.ERROR -> {
                            // Handle error
                            _login.update { currentState.copy(isLoading = false, errorMessage = response.message) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch and handle unexpected exceptions
                _login.update { it.copy(isLoading = false, errorMessage = "Failed to fetch data: ${e.message}") }
            }
        }
    }

    // Sealed class for different states in UI
    sealed class LoginScreenState {
        object Loading : LoginScreenState()
        data class Error(val errorMessage: String) : LoginScreenState()
        data class Success(val responseData: UserResponse?) : LoginScreenState()
    }

    // Data class representing the state of the product details screen
    data class LoginState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: UserResponse? = null  // Nullable Product to handle empty state
    ) {
        // Convert HomeState to UI state (HomeScreenState)
        fun toUiState(): LoginScreenState {
            return when {
                isLoading -> LoginScreenState.Loading
                errorMessage?.isNotEmpty() == true -> LoginScreenState.Error(errorMessage)
                responseData != null -> LoginScreenState.Success(responseData)
                else -> LoginScreenState.Error("Unknown error")
            }
        }
    }
}
