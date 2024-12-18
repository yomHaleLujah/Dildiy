package com.yome.dildiy.ui.ecommerce.checkout

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.model.CartItem
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



    val _selectedLocation = MutableStateFlow<GeoPoint?>(null)
    var selectedLocation = _selectedLocation.asStateFlow()

    val _cartItems = MutableStateFlow<List<CartItemDTO>>(emptyList())
    var cartItems = _cartItems.asStateFlow()

    val _orderSubmissionState =
        MutableStateFlow<OrderSubmissionState>(OrderSubmissionState.Idle)
    var orderSubmissionState = _orderSubmissionState.asStateFlow()


    private val _lastName = MutableStateFlow(TextFieldValue("")) // Store TextFieldValue
    val lastName: StateFlow<TextFieldValue> get() = _lastName

    private val _nameState = MutableStateFlow(TextFieldValue(""))
    val nameState: StateFlow<TextFieldValue> = _nameState

    private val _emailState = MutableStateFlow(TextFieldValue(""))
    val emailState: StateFlow<TextFieldValue> = _emailState

    private val _phoneNumberState = MutableStateFlow(TextFieldValue(""))
    val phoneNumberState: StateFlow<TextFieldValue> = _phoneNumberState

    private val _addressState = MutableStateFlow(TextFieldValue(""))
    val addressState: StateFlow<TextFieldValue> = _addressState

    private val _apartmentState = MutableStateFlow(TextFieldValue(""))
    val apartmentState: StateFlow<TextFieldValue> = _apartmentState

    private val _specialInstructionState = MutableStateFlow(TextFieldValue(""))
    val specialInstructionState: StateFlow<TextFieldValue> = _specialInstructionState

    // Update functions
    fun setName(value: TextFieldValue) {
        _nameState.value = value
    }

    fun setEmail(value: TextFieldValue) {
        _emailState.value = value
    }

    fun setPhoneNumber(value: TextFieldValue) {
        _phoneNumberState.value = value
    }

    fun setAddress(value: TextFieldValue) {
        _addressState.value = value
    }

    fun setApartment(value: TextFieldValue) {
        _apartmentState.value = value
    }

    fun setSpecialInstruction(value: TextFieldValue) {
        _specialInstructionState.value = value
    }

    fun setLastName(value: String) {
        // Update only the text while preserving caret position
        _lastName.value = _lastName.value.copy(
            text = value,
            selection = TextRange(value.length) // Move the caret to the end
        )
    }

    fun validateForm(): ValidationResult {
        if (nameState.value.text.isBlank() ||
            emailState.value.text.isBlank() ||
            phoneNumberState.value.text.isBlank() ||
            addressState.value.text.isBlank() ||
            apartmentState.value.text.isBlank() ||
            specialInstructionState.value.text.isBlank()
        ) {
            return ValidationResult(false, "All fields are required.")
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailState.value.text).matches()) {
            return ValidationResult(false, "Invalid email address.")
        }

        return ValidationResult(true, "Form is valid.")
    }

}
data class ValidationResult(val isValid: Boolean, val message: String)


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
    val cartItems: List<CartItemDTO> = Json.decodeFromString(cartItemsJson)

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
        cartItems = cartItems,
        items = orderItems
    )
}
