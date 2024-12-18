package com.yome.dildiy.ui.ecommerce.checkout

import android.content.Context
import android.util.Log
import android.widget.RemoteViews.RemoteCollectionItems
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.networking.DeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class DeliveryVm(private val deliveryRepository: DeliveryRepository) : ViewModel() {

    // Internal state that holds the PaymentState
    private val _deliverState = MutableStateFlow(DeliveryState())
    val paymentState = _deliverState.asStateFlow()
    private val _deliveryPrice = MutableStateFlow(0.0)
    val deliveryPrice = _deliveryPrice.asStateFlow()

    // Function to initiate payment
    fun calculateDeliveryPrice(deliveryRequest: DeliveryRequest, context: Context) {
        viewModelScope.launch {
            _deliverState.update { it.copy(isLoading = true) }
            try {
                deliveryRepository.calculateDeliveryPrice(deliveryRequest, context =context).collect { response ->
                    when (response.status) {
                        ApiStatus.LOADING -> {
                            _deliverState.update { it.copy(isLoading = true) }
                            Log.d("CheckoutUrl  " ,"Loading")

                        }
                        ApiStatus.SUCCESS -> {
                            val priceResponse = response.data
                            Log.d("delivery price  " , priceResponse.toString())
                            if (priceResponse != null) {
                                _deliveryPrice.value = priceResponse.deliveryPrice
                            }
                            _deliverState.update {
                                it.copy(
                                    isLoading = false,
                                    success = priceResponse,
                                    errorMessage = null
                                )
                            }
                        }
                        ApiStatus.ERROR -> {
                            Log.d("CheckoutUrl  " , "error")
                            _deliverState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = response.message ?: "Unknown error"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _deliverState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // Data class representing the internal state of payment process
    data class DeliveryState(
        val isLoading: Boolean = false,
        val success: DeliveryPriceResponse? = null,
        val errorMessage: String? = null
    ) {
        // Convert PaymentState to UI state (PaymentUiState)
        fun toUiState(): DeliveryUiState {
            return DeliveryUiState(
                isLoading = isLoading,
                success = success,
                errorMessage = errorMessage,
                isSuccess = success != null && errorMessage == null
            )
        }
    }

    // Sealed class for different UI states (for display)
    data class DeliveryUiState(
        val isLoading: Boolean = false,
        val success: DeliveryPriceResponse? = null,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )
    sealed class DeliveryScreenState {
        object Loading : DeliveryScreenState()
        data class Error(val errorMessage: String) : DeliveryScreenState()
        data class Success(val responseData: DeliveryPriceResponse?) :
            DeliveryScreenState()  // Assuming CartItem represents an item in the cart
    }

}


@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double
)
@Serializable
data class DeliveryRequest(
    val cartItems: List<CartItemDTO>,
    val deliveryLocation: Location
)
@Serializable
data class DeliveryPriceResponse(
    val deliveryPrice: Double,
    val distanceInKm: Double
)