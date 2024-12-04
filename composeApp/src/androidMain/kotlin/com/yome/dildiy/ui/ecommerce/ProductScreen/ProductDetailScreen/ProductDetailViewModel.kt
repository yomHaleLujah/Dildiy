package com.yome.dildiy.ui.ecommerce.productdetail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.ProductDetailRepository
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.ui.ecommerce.ProductScreen.ProductViewModel.ProductScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailVm(private val productDetailRepository: ProductDetailRepository) :
    androidx.lifecycle.ViewModel() {

    private val productDetailViewState: MutableStateFlow<ProductScreenState> =
        MutableStateFlow(ProductScreenState.Loading)

    // Internal state that holds the HomeState
    private val _productDetailState = MutableStateFlow(HomeState())
    val productDetailState = _productDetailState.asStateFlow()  // Expose as StateFlow for UI to observe

    // Function to fetch product details
    fun getProduct(productId: Int, context : Context) {
        // Use viewModelScope to automatically manage coroutines tied to the ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Fetch product details from repository in IO dispatcher
                productDetailRepository.getProduct(productId, context = context).collect { response ->
                    // Get the current state before modifying it
                    val currentState = _productDetailState.value

                    when (response.status) {
                        ApiStatus.LOADING -> {
                            // Update state to loading
                            _productDetailState.update { currentState.copy(isLoading = true, errorMessage = null) }
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
                                    errorMessage = "Product not found",
                                    responseData = null
                                )
                            }
                            _productDetailState.update { updatedState }
                        }
                        ApiStatus.ERROR -> {
                            // Handle error
                            _productDetailState.update { currentState.copy(isLoading = false, errorMessage = response.message) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch and handle unexpected exceptions
                _productDetailState.update { it.copy(isLoading = false, errorMessage = "Failed to fetch data: ${e.message}") }
            }
        }
    }

    // Sealed class for different states in UI
    sealed class HomeScreenState {
        object Loading : HomeScreenState()
        data class Error(val errorMessage: String) : HomeScreenState()
        data class Success(val responseData: Product) : HomeScreenState()
    }

    // Data class representing the state of the product details screen
    data class HomeState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: Product? = null  // Nullable Product to handle empty state
    ) {
        // Convert HomeState to UI state (HomeScreenState)
        fun toUiState(): HomeScreenState {
            return when {
                isLoading -> HomeScreenState.Loading
                errorMessage?.isNotEmpty() == true -> HomeScreenState.Error(errorMessage)
                responseData != null -> HomeScreenState.Success(responseData)
                else -> HomeScreenState.Error("Unknown error")
            }
        }
    }
}
