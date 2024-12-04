package com.yome.dildiy.ui.ecommerce.profile

import android.content.Context
import com.yome.dildiy.networking.ProfileRepository
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.remote.dto.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileVm(private val profileRepository: ProfileRepository) :
    androidx.lifecycle.ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState())
    private val _profileViewState: MutableStateFlow<ProfileScreenState> =
        MutableStateFlow(ProfileScreenState.Loading)
    val productViewState = _profileViewState.asStateFlow()

    // Function to fetch product details
    fun getAllProduct(username: String, context: Context) {
        // Use viewModelScope to automatically manage coroutines tied to the ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Fetch product details from repository in IO dispatcher
                profileRepository.getAllProductsByUsername(username, context).collect { response ->
                    // Get the current state before modifying it
                    val currentState = _profileState.value

                    when (response.status) {
                        ApiStatus.LOADING -> {
                            // Update state to loading
                            _profileState.update { currentState.copy(isLoading = true, errorMessage = null) }
                            _profileViewState.value = ProfileScreenState.Loading
                        }
                        ApiStatus.SUCCESS -> {
                            val updatedState = if (response.data != null) {
                                currentState.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    responseData = response.data
                                )
                            } else {
                                currentState.copy(
                                    isLoading = false,
                                    errorMessage = "Product not found",
                                    responseData = emptyList()
                                )
                            }
                            _profileState.update { updatedState }
                            _profileViewState.value = updatedState.toUiState()
                        }
                        ApiStatus.ERROR -> {
                            // Handle error
                            _profileState.update { currentState.copy(isLoading = false, errorMessage = response.message) }
                            _profileViewState.value = ProfileScreenState.Error(response.message ?: "Unknown error")
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch and handle unexpected exceptions
                _profileState.update { it.copy(isLoading = false, errorMessage = "Failed to fetch data: ${e.message}") }
            }
        }
    }

    // Sealed class for different states in UI
    sealed class ProfileScreenState {
        object Loading : ProfileScreenState()
        data class Error(val errorMessage: String) : ProfileScreenState()
        data class Success(val responseData: List<Product>) : ProfileScreenState()
    }

    // Data class representing the state of the product details screen
    data class ProfileState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: List<Product> = emptyList()  // Nullable Product to handle empty state
    ) {
        // Convert HomeState to UI state (HomeScreenState)
        fun toUiState(): ProfileScreenState {
            return when {
                isLoading -> ProfileScreenState.Loading
                errorMessage?.isNotEmpty() == true -> ProfileScreenState.Error(errorMessage)
                responseData != null -> ProfileScreenState.Success(responseData)
                else -> ProfileScreenState.Error("Unknown error")
            }
        }
    }
}
