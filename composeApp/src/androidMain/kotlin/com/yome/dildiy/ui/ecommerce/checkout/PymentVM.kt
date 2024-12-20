package com.yome.dildiy.ui.ecommerce.checkout

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.Dao.PaymentResponse
import com.yome.dildiy.model.OrderItem
import com.yome.dildiy.remote.dto.Payload
import com.yome.dildiy.model.Orders
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.CartDTO
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.networking.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class PaymentViewModel(private val paymentRepository: PaymentRepository) : ViewModel() {


    // Internal state that holds the PaymentState
    private val _paymentState = MutableStateFlow(PaymentState())
    val paymentState = _paymentState.asStateFlow()  // Expose as StateFlow for UI to observe

    private val _paymentState2 = MutableStateFlow(PaymentState2())
    val paymentState2 = _paymentState2.asStateFlow()  // Expose as StateFlow for UI to observe

    private val _hasNavigatedToWebView = MutableStateFlow(false)
    val hasNavigatedToWebView: StateFlow<Boolean> = _hasNavigatedToWebView.asStateFlow()

    // Initialize finishIt as a MutableStateFlow of Orders with a default value or null
    private val _finishIt = MutableStateFlow<Orders?>(null) // Using nullable type to handle uninitialized state
    val finishIt: StateFlow<Orders?> get() = _finishIt // Expose as a read-only StateFlow

    // Function to update the value of finishIt
    fun setFinishIt(order: Orders) {
        _finishIt.value = order
    }
    // tx_ref generated once and exposed as LiveData
    private val _txRef = MutableLiveData<String>().apply {
        value = generateTxRef()
    }
    val txRef: LiveData<String> get() = _txRef

    // Function to generate a single tx_ref
     fun generateTxRef(): String {
        return "chewatatest${(1000000000..9999999999).random()}"
    }
    fun generateTxRef2(){
        _txRef.value = "chewatatest${(1000000000..9999999999).random()}"
    }

    // Optional function to get the tx_ref value directly
    fun getTxRef(): String = _txRef.value.orEmpty()
    // Function to set the navigation state
    fun setNavigatedToWebView(hasNavigated: Boolean) {
        _hasNavigatedToWebView.value = hasNavigated
    }
    // Function to initiate payment
    fun initiatePayment(order: Orders, context: Context, paymentRequest: Payload) {
        viewModelScope.launch {
            _paymentState.update { it.copy(isLoading = true) }
            try {
                paymentRepository.initiatePayment(order=order, context =context, paymentRequest = paymentRequest ).collect { response ->
                    when (response.status) {
                        ApiStatus.LOADING -> {
                            _paymentState.update { it.copy(isLoading = true) }
                            Log.d("CheckoutUrl  " ,"Loading")

                        }
                        ApiStatus.SUCCESS -> {
                            setNavigatedToWebView(false)
                            val checkoutUrl = response.data?.data?.checkout_url
                            Log.d("CheckoutUrl  " , checkoutUrl.toString())
                            _paymentState.update {
                                it.copy(
                                    isLoading = false,
                                    checkoutUrl = checkoutUrl,
                                    errorMessage = null
                                )
                            }
                        }
                        ApiStatus.ERROR -> {
                            Log.d("CheckoutUrl  " , "error")
                            _paymentState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = response.message ?: "Unknown error"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _paymentState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    fun verifyPayment(tx_ref: String) {
        viewModelScope.launch {
            _paymentState2.update { it.copy(isLoading = true) }
            try {
                paymentRepository.verifyPayment( tx_ref).collect { response ->
                    when (response.status) {
                        ApiStatus.LOADING -> {
                            _paymentState2.update { it.copy(isLoading = true) }

                        }
                        ApiStatus.SUCCESS -> {

                            setNavigatedToWebView(false)
                            val responsePayment = response.data
                            println("$responsePayment + Response payment")
                            _paymentState2.update {
                                it.copy(
                                    isLoading = false,
                                    success = responsePayment,
                                    errorMessage = null
                                )
                            }
                        }
                        ApiStatus.ERROR -> {
                            Log.d("CheckoutUrl  " , "error")
                            _paymentState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = response.message ?: "Unknown error"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _paymentState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // Data class representing the internal state of payment process
    data class PaymentState(
        val isLoading: Boolean = false,
        val checkoutUrl: String? = null,
        val errorMessage: String? = null
    ) {
        // Convert PaymentState to UI state (PaymentUiState)
        fun toUiState(): PaymentUiState {
            return PaymentUiState(
                isLoading = isLoading,
                checkoutUrl = checkoutUrl,
                errorMessage = errorMessage,
                isSuccess = checkoutUrl != null && errorMessage == null
            )
        }
    }

    // Sealed class for different UI states (for display)
    data class PaymentUiState(
        val isLoading: Boolean = false,
        val checkoutUrl: String? = null,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )
    sealed class PaymentScreenState {
        object Loading : PaymentScreenState()
        data class Error(val errorMessage: String) : PaymentScreenState()
        data class Success(val responseData: CartDTO?) : PaymentScreenState()  // Assuming CartItem represents an item in the cart
    }



    // Data class representing the internal state of payment process
    data class PaymentState2(
        val isLoading: Boolean = false,
        val success: PaymentResponse? = null,
        val errorMessage: String? = null
    ) {
        // Convert PaymentState to UI state (PaymentUiState)
        fun toUiState(): PaymentUiState2 {
            return PaymentUiState2(
                isLoading = isLoading,
                success = success,
                errorMessage = errorMessage,
                isSuccess = success != null && errorMessage == null
            )
        }
    }

    // Sealed class for different UI states (for display)
    data class PaymentUiState2(
        val isLoading: Boolean = false,
        val success: PaymentResponse? = null,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )
    sealed class PaymentScreenState2 {
        object Loading : PaymentScreenState2()
        data class Error(val errorMessage: String) : PaymentScreenState2()
        data class Success(val responseData: CartDTO?) : PaymentScreenState2()  // Assuming CartItem represents an item in the cart
    }



}


fun prepareOrder(
    userId: String,
    fullName: String,
    email: String,
    phone: String,
    address: String,
    apartment: String?,
    specialInstructions: String?,
    location: String,
    cartItems: List<CartItemDTO>
): Orders {

    Log.d("CartItems", "Cart items before preparing order: $cartItems")

    // Calculate the total price of the order
    val totalPrice = cartItems.sumOf { it.quantity * it.product.price }

    // Create a list of OrderItem from the CartItemDTO
    val orderItems = cartItems.map { cartItem ->
        OrderItem(
            productId = cartItem.product.id.toString(),
            productName = cartItem.product.name,
            quantity = cartItem.quantity,
            price = cartItem.product.price
        )
    }

    Log.d("OrderItems", "Order items created: $orderItems")

    // Generate timestamps (createdAt and updatedAt)
    val now = LocalDateTime.now().toString()

    // Prepare the cartItems as a JSON string (or can use other formats)
    val cartItemsJson = Json.encodeToString(cartItems)
    val cartItems: List<CartItemDTO> = Json.decodeFromString(cartItemsJson)

    // Create the Order object
    val orders = Orders(
        userId = userId,
        fullName = fullName,
        email = email,
        phone = phone,
        address = address,
        apartment = apartment,
        specialInstructions = specialInstructions,
        location = location,
        totalPrice = totalPrice,
        createdAt = now,
        updatedAt = now,
        cartItems = cartItems,
        items = orderItems
    )

    println("Prepare Order  "+ orders)
return orders
}

