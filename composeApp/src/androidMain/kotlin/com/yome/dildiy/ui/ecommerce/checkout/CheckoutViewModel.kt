package com.yome.dildiy.ui.ecommerce.checkout

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.model.OrderItem
import com.yome.dildiy.model.Orders
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.networking.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

class CheckoutViewModel(
    private val orderRepository: OrderRepository // Replace with your actual repository interface
) : ViewModel() {

    val _emailAddress = MutableStateFlow("")
    var emailAddress = _emailAddress.asStateFlow()

    val _phoneNumber = MutableStateFlow("")
    var phoneNumber = _phoneNumber.asStateFlow()

    private val _specialInstructions = MutableStateFlow("")
    val specialInstructions: StateFlow<String> = _specialInstructions

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _apartment = MutableStateFlow("")
    val apartment: StateFlow<String> = _apartment


    val _selectedLocation = MutableStateFlow<GeoPoint?>(null)
    var selectedLocation = _selectedLocation.asStateFlow()

    val _cartItems = MutableStateFlow<List<CartItemDTO>>(emptyList())
    var cartItems = _cartItems.asStateFlow()

    val _orderSubmissionState =
        MutableStateFlow<OrderSubmissionState>(OrderSubmissionState.Idle)
    var orderSubmissionState = _orderSubmissionState.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName


    fun setFistName(value: String) {
        _firstName.value = value
    }
    fun setLastName(value: String) {
        _lastName.value = value
    }
    fun setEmailAddress(value: String) {
        _emailAddress.value = value
    }

    fun setPhoneNumber(value: String) {
        _phoneNumber.value = value
    }

    fun setSpecialInstructions(value: String) {
        _specialInstructions.value = value
    }

    fun setAddress(value: String) {
        _address.value = value
    }

    fun setApartment(value: String) {
        _apartment.value = value
    }

    // Submit the order;
    fun submitOrder(cartItems: List<CartItemDTO>, context: Context , latlang : String ) {
           println("Order $context")

        if (firstName.value.isBlank() || lastName.value.isBlank() || emailAddress.value.isBlank() || phoneNumber.value.isBlank() || address.value.isBlank()) {
            _orderSubmissionState.update { OrderSubmissionState.Error("Please fill in all required fields.") }
            return
        }

        viewModelScope.launch {
            _orderSubmissionState.update { OrderSubmissionState.Loading }
            try {

                println("Order $context")

                val order = prepareOrder(
                    fullName = firstName.value + " " + lastName.value,
                    email = emailAddress.value,
                    phone = phoneNumber.value,
                    address = address.value,
                    apartment = apartment.value,
                    specialInstructions = specialInstructions.value,
                    location = latlang,
                    cartItems = cartItems
                )

                println("Order $order")


                val orderResponse = orderRepository.placeOrder(
                    order, context
                )
                _orderSubmissionState.update { OrderSubmissionState.Success(orderResponse.toString()) }
            } catch (e: Exception) {
                _orderSubmissionState.update { OrderSubmissionState.Error("Failed to submit the order: ${e.message}") }
            }
        }
    }
}

// States for order submission
sealed class OrderSubmissionState {
    object Idle : OrderSubmissionState()
    object Loading : OrderSubmissionState()
    data class Success(val orderId: String) : OrderSubmissionState()
    data class Error(val message: String) : OrderSubmissionState()
}

// This function creates an Order object from the provided data
fun prepareOrder2(
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

    // Create the Order object
    return Orders(
        userId = "USER_ID", // This should be dynamically set (for example, from the logged-in user)
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
        cartItems = cartItemsJson,
        items = orderItems
    )
}
