package com.yome.dildiy.ui.ecommerce.createProduct

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.UploadProductRepository
import com.yome.dildiy.remote.dto.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UploadProductVm(private val uploadProductRepository: UploadProductRepository) :
    androidx.lifecycle.ViewModel() {
    val name = MutableStateFlow("")
    val price = MutableStateFlow("")
    val stock = MutableStateFlow("")
    val description = MutableStateFlow("")
    val category = MutableStateFlow("")

//    private val uploadProductViewState: MutableStateFlow<UploadProductScreenState> =
//        MutableStateFlow(ProductScreenState.Loading)

    // Internal state that holds the HomeState
    private val _uploadProduct = MutableStateFlow(UploadProductState())
    val uploadProduct = _uploadProduct.asStateFlow()  // Expose as StateFlow for UI to observe
    val uploadProductState = _uploadProduct.asStateFlow()  // Expose as StateFlow for UI to observe

    // Function to fetch product details
    fun uploadProduct(product: Product, imageUris: SnapshotStateList<Uri>, context: Context) {
        // Use viewModelScope to automatically manage coroutines tied to the ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Fetch product details from repository in IO dispatcher
                uploadProductRepository.uploadProduct(product, imageUris, context).collect { response ->
                    // Get the current state before modifying it
                    val currentState = _uploadProduct.value

                    when (response.status) {
                        ApiStatus.LOADING -> {
                            // Update state to loading
                            _uploadProduct.update { currentState.copy(isLoading = true, errorMessage = null) }
                        }
                        ApiStatus.SUCCESS -> {
                            // Handle successful response
                            val updatedState = if (response.data != null) {
                                println("response image list" + response.data)
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
                            _uploadProduct.update { updatedState }
                        }
                        ApiStatus.ERROR -> {
                            println("response image list" + "Product not found" + response.message)
                            // Handle error
                            _uploadProduct.update { currentState.copy(isLoading = false, errorMessage = response.message) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch and handle unexpected exceptions
                _uploadProduct.update { it.copy(isLoading = false, errorMessage = "Failed to fetch data: ${e.message}") }
            }
        }
    }

    // Sealed class for different states in UI
    sealed class UploadProductScreenState {
        object Loading : UploadProductScreenState()
        data class Error(val errorMessage: String) : UploadProductScreenState()
        data class Success(val responseData: List<String>) : UploadProductScreenState()
    }

    // Data class representing the state of the product details screen
    data class UploadProductState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: List<String>? = null  // Nullable Product to handle empty state
    ) {
        // Convert HomeState to UI state (HomeScreenState)
        fun toUiState(): UploadProductScreenState {
            return when {
                isLoading -> UploadProductScreenState.Loading
                errorMessage?.isNotEmpty() == true -> UploadProductScreenState.Error(errorMessage)
                responseData != null -> UploadProductScreenState.Success(responseData)
                else -> UploadProductScreenState.Error("Unknown error")
            }
        }
    }
}
