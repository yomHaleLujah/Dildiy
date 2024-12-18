package com.yome.dildiy.ui.ecommerce.checkout

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.model.Orders
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.DeliveryRepository
import com.yome.dildiy.networking.OrderRepository
import com.yome.dildiy.ui.ecommerce.profile.ProfileVm.ProfileScreenState
import com.yome.dildiy.ui.ecommerce.profile.ProfileVm.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderVm(private val orderRepository: OrderRepository) : ViewModel() {

    // Internal state that holds the PaymentState
    private val _orderState = MutableStateFlow(OrderState())
    val orderState = _orderState.asStateFlow()

    private val _getOrderState = MutableStateFlow(GetOrderState())
    private val _getOrderViewState: MutableStateFlow<GetOrderScreenState> =
        MutableStateFlow(GetOrderScreenState.Loading)
    val getOrderViewState = _getOrderViewState.asStateFlow()

    fun getOrders(context: Context) {
        viewModelScope.launch {
            _getOrderState.update { it.copy(isLoading = true) }
            try {
                orderRepository.getOrders(context = context).collect { response ->
                    when (response.status) {
                        ApiStatus.LOADING -> {
                            _getOrderState.update { it.copy(isLoading = true) }
//                            _getOrderViewState.value = GetOrderScreenState.Loading
                        }

                        ApiStatus.SUCCESS -> {

                            val orderResponse = response.data

                            _getOrderState.update {
                                it.copy(
                                    isLoading = false,
                                    success = orderResponse,
                                    errorMessage = null
                                )
                            }
                            println(" orders Responce $orderResponse")
                            _getOrderViewState.value = GetOrderScreenState.Success(orderResponse)
                        }

                        ApiStatus.ERROR -> {
                            _getOrderState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = response.message ?: "Unknown error"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _getOrderState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // Function to initiate payment
    fun placeOrder(order: Orders, context: Context, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _orderState.update { it.copy(isLoading = true) }
            try {
                orderRepository.placeOrder(order = order, context = context).collect { response ->
                    when (response.status) {
                        ApiStatus.LOADING -> {
                            _orderState.update { it.copy(isLoading = true) }

                        }

                        ApiStatus.SUCCESS -> {
                            val orderResponse = response.data

                            _orderState.update {
                                it.copy(
                                    isLoading = false,
                                    success = orderResponse,
                                    errorMessage = null
                                )
                            }
                        }

                        ApiStatus.ERROR -> {
                            _orderState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = response.message ?: "Unknown error"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _orderState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.localizedMessage}"
                    )
                }
            }
        }
    }


    // Data class representing the internal state of payment process
    data class OrderState(
        val isLoading: Boolean = false,
        val success: Orders? = null,
        val errorMessage: String? = null
    ) {
        // Convert PaymentState to UI state (PaymentUiState)
        fun toUiState(): OrderUiState {
            return OrderUiState(
                isLoading = isLoading,
                success = success,
                errorMessage = errorMessage,
                isSuccess = success != null && errorMessage == null
            )
        }
    }

    // Sealed class for different UI states (for display)
    data class GetOrderUiState(
        val isLoading: Boolean = false,
        val success: List<Orders>? = emptyList(),
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )

    sealed class GetOrderScreenState {
        object Loading : GetOrderScreenState()
        data class Error(val errorMessage: String) : GetOrderScreenState()
        data class Success(val responseData: List<Orders>?) :
            GetOrderScreenState()  // Assuming CartItem represents an item in the cart
    }

    data class GetOrderState(
        val isLoading: Boolean = false,
        val success: List<Orders>? = emptyList(),
        val errorMessage: String? = null
    ) {
        // Convert PaymentState to UI state (PaymentUiState)
        fun toUiState():GetOrderUiState {
            return GetOrderUiState(
                isLoading = isLoading,
                success = success,
                errorMessage = errorMessage,
                isSuccess = success != null && errorMessage == null
            )
        }
    }

    // Sealed class for different UI states (for display)
    data class OrderUiState(
        val isLoading: Boolean = false,
        val success: Orders? = null,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )

    sealed class OrderScreenState {
        object Loading : OrderScreenState()
        data class Error(val errorMessage: String) : OrderScreenState()
        data class Success(val responseData: DeliveryPriceResponse?) :
            OrderScreenState()  // Assuming CartItem represents an item in the cart
    }

}
