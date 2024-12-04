package com.yome.dildiy.ui.register

/*
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.yome.dildiy.remote.dto.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.yome.dildiy.networking.DildiyClient


import kotlinx.coroutines.launch

// Define the ViewModel to manage registration state
class RegisterViewModel : ViewModel(), KoinComponent {

    private val registerClient: DildiyClient by inject()  // Inject RegisterClient using Koin

    private val _registerState = mutableStateOf<RegisterState>(RegisterState.Idle)
    val registerState: State<RegisterState> = _registerState

    // Function to register user
// Assuming User and Error are predefined classes
    fun registerUser(dildiyClient: DildiyClient, user: User, navController: NavController) {
        println("hi")
        println(user)
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            when (val result = dildiyClient.registerUser(user)) {
                is com.yome.dildiy.util.Result.Success -> {
                    _registerState.value = RegisterState.Success(result.data) // Success case
                    println("hi suceess")
                    // Navigate to the login screen on success
                    navController.navigate("login") {
                        // Optionally clear the backstack if you don't want to go back to the register screen
                        popUpTo("register") { inclusive = true }
                    }
                }

                is com.yome.dildiy.util.Result.Error -> {
                    println("hi error" + result.error.toString())
                    _registerState.value =
                        RegisterState.Error(result.error.toString() ?: "Unknown error occurred")
                }
            }
        }
    }

    // Sealed class to represent different states of registration
    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val user: User) : RegisterState()
        data class Error(val error: String) : RegisterState()
    }
}
*/


import androidx.lifecycle.viewModelScope
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.RegisterRepository
import com.yome.dildiy.remote.dto.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterVm(private val registerRepository: RegisterRepository) :
    androidx.lifecycle.ViewModel() {
    val name = MutableStateFlow("")
    val price = MutableStateFlow("")
    val stock = MutableStateFlow("")
    val description = MutableStateFlow("")

//    private val uploadProductViewState: MutableStateFlow<UploadProductScreenState> =
//        MutableStateFlow(ProductScreenState.Loading)

    // Internal state that holds the HomeState
    private val _register = MutableStateFlow(RegisterState())
    val loginState = _register.asStateFlow()  // Expose as StateFlow for UI to observe

    // Function to fetch product details
    fun register(user: User) {
        // Use viewModelScope to automatically manage coroutines tied to the ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Fetch product details from repository in IO dispatcher
                registerRepository.register(user).collect { response ->
                    // Get the current state before modifying it
                    val currentState = _register.value

                    when (response.status) {
                        ApiStatus.LOADING -> {
                            // Update state to loading
                            _register.update { currentState.copy(isLoading = true, errorMessage = null) }
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
                            _register.update { updatedState }
                        }
                        ApiStatus.ERROR -> {
                            // Handle error
                            _register.update { currentState.copy(isLoading = false, errorMessage = response.message) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch and handle unexpected exceptions
                _register.update { it.copy(isLoading = false, errorMessage = "Failed to fetch data: ${e.message}") }
            }
        }
    }

    // Sealed class for different states in UI
    sealed class RegisterScreenState {
        object Loading : RegisterScreenState()
        data class Error(val errorMessage: String) : RegisterScreenState()
        data class Success(val responseData: String?) : RegisterScreenState()
    }

    // Data class representing the state of the product details screen
    data class RegisterState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: String? = null  // Nullable Product to handle empty state
    ) {
        // Convert HomeState to UI state (HomeScreenState)
        fun toUiState(): RegisterScreenState {
            return when {
                isLoading -> RegisterScreenState.Loading
                errorMessage?.isNotEmpty() == true -> RegisterScreenState.Error(errorMessage)
                responseData != null -> RegisterScreenState.Success(responseData)
                else -> RegisterScreenState.Error("Unknown error")
            }
        }
    }
}
