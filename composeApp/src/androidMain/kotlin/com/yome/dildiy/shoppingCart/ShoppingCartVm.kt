package com.yome.dildiy.shoppingCart


import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.model.CartItem
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.CartDTO
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.networking.GetCartRepository
import com.yome.dildiy.networking.ShoppingCartRepository
import com.yome.dildiy.ui.ecommerce.ProductScreen.ProductDetailScreen.CartItemRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShoppingCartViewModel(private val shoppingCartRepository: ShoppingCartRepository,private val cartRepository: GetCartRepository) :
    androidx.lifecycle.ViewModel() {

    private val shoppingCartViewState: MutableStateFlow<CartScreenState> =
        MutableStateFlow(CartScreenState.Loading)

    // Internal state that holds the CartState
    private val _totalPrice = mutableStateOf(0f) // Track the total price
    var totalPrice: State<Float> = _totalPrice
    private val _shoppingCartState = MutableStateFlow(CartState())
    private val _shoppingCartState2 = MutableStateFlow(CartState2())
    private val _shoppingCartState3 = MutableStateFlow(CartState3())
    private val _shoppingCartState5 = MutableStateFlow<List<CartItemDTO>>(emptyList())
    var shoppingCartState5: StateFlow<List<CartItemDTO>> = _shoppingCartState5

    val shoppingCartState = _shoppingCartState.asStateFlow()  // Expose as StateFlow for UI to observe
    val shoppingCartState2 = _shoppingCartState2.asStateFlow()  // Expose as StateFlow for UI to observe
    val shoppingCartState3 = _shoppingCartState3.asStateFlow()  // Expose as StateFlow for UI to observe

    private val _cartQuantity = MutableStateFlow(0)
    val cartQuantity: StateFlow<Int> = _cartQuantity

    private val _cartItemsState = MutableStateFlow<List<CartItemDTO>>(emptyList())
    val cartItemsState: StateFlow<List<CartItemDTO>> = _cartItemsState

    fun removeCartItem(cartItemId: Long) {
        // Update the cart items state directly from the ViewModel
        _cartItemsState.value = _cartItemsState.value.filter { it.id != cartItemId }
    }
    // Function to update cart quantity
    fun updateCartQuantity(newQuantity: Int) {
        if (_cartQuantity.value != newQuantity) {
            _cartQuantity.value = newQuantity
        }
    }


    // Function to add a value to the total price
    fun addToTotalPrice(value: Double) {
        _totalPrice.value += value.toFloat()
    }
    fun decreaseToTotalPrice(value: Double) {
        val newTotal = _totalPrice.value - value.toFloat()
        if (newTotal >= 0) {
            _totalPrice.value = newTotal
        } else {
            _totalPrice.value = 0f // Ensure it does not go below zero
        }
    }

    fun updateTotalPrice(cartItems: List<CartItemDTO>) {
        _totalPrice.value = cartItems.sumOf { it.product.price * it.quantity }.toFloat()
    }

    fun addCartItems(
        username: String,
        context: Context,
        cartItemRequest: CartItemRequest?
    ) {
        // Use viewModelScope to automatically manage coroutines tied to the ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Fetch cart items from repository in IO dispatcher
                shoppingCartRepository.addCartItem(username, context = context, cartItemRequest = cartItemRequest).collect { response ->
                    // Get the current state before modifying it
                    val currentState = _shoppingCartState.value

                    when (response.status) {
                        ApiStatus.LOADING -> {
                            // Update state to loading
                            _shoppingCartState.update { currentState.copy(isLoading = true, errorMessage = null) }
                        }
                        ApiStatus.SUCCESS -> {
                            Log.d("ShoppingCartVM", " Api Success $response.data")

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
                                    errorMessage = "Cart is empty",
                                    responseData = null
                                )
                            }
                            _shoppingCartState.update { updatedState }
                        }
                        ApiStatus.ERROR -> {
                            // Handle error
                            _shoppingCartState.update { currentState.copy(isLoading = false, errorMessage = response.message) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch and handle unexpected exceptions
                _shoppingCartState.update { it.copy(isLoading = false, errorMessage = "Failed to fetch data: ${e.message}") }
            }
        }
    }

    fun deleteCartItem(
        productId: String, context: Context
    ) {
        // Use viewModelScope to automatically manage coroutines tied to the ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Fetch cart items from repository in IO dispatcher
                shoppingCartRepository.deleteCartItem(productId = productId, context = context ).collect { response ->
                    // Get the current state before modifying it
                    val currentState = _shoppingCartState3.value

                    when (response.status) {
                        ApiStatus.LOADING -> {
                            // Update state to loading
                            _shoppingCartState3.update { currentState.copy(isLoading = true, errorMessage = null) }
                        }
                        ApiStatus.SUCCESS -> {
                            Log.d("ShoppingCartVM", " Api Success $response.data")

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
                                    errorMessage = "Cart is empty",
                                    responseData = null
                                )
                            }
                            _shoppingCartState3.update { updatedState }
                        }
                        ApiStatus.ERROR -> {
                            // Handle error
                            _shoppingCartState3.update { currentState.copy(isLoading = false, errorMessage = response.message) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch and handle unexpected exceptions
                _shoppingCartState3.update { it.copy(isLoading = false, errorMessage = "Failed to fetch data: ${e.message}") }
            }
        }
    }

    // Function to fetch all cart items for a user
    fun fetchCartItems(username: String, context: Context) {
        // Use viewModelScope to manage coroutine tied to ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Fetch cart items from repository in IO dispatcher
                cartRepository.getCart(context).collect { response ->
                    // Get the current state before modifying it
                    val currentState = _shoppingCartState2.value

                    when (response.status) {
                        ApiStatus.LOADING -> {
                            Log.d("ShoppingCartVM LOADING", "Api Error ${response.message}")

                            // Update state to loading
                            _shoppingCartState2.update { currentState.copy(isLoading = true, errorMessage = null) }
                        }
                        ApiStatus.SUCCESS -> {
                            Log.d("ShoppingCartVM", "Api Success ${response.data}")
                            val cartItems = response.data?.cartItems ?: emptyList()

                            // Update the total price whenever cart items are fetched
                            updateTotalPrice(cartItems)
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
                                    errorMessage = "Cart is empty",
                                    responseData = null
                                )
                            }
                            _shoppingCartState2.update { updatedState }
                        }
                        ApiStatus.ERROR -> {
                            Log.e("ShoppingCartVM", "Api Error ${response.message}")

                            // Handle error
                            _shoppingCartState2.update { currentState.copy(isLoading = false, errorMessage = response.message) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch and handle unexpected exceptions
                _shoppingCartState2.update { it.copy(isLoading = false, errorMessage = "Failed to fetch data: ${e.message}") }
            }
        }
    }

    fun removeItem(cartItemId: Long) {
         _shoppingCartState5.value = _shoppingCartState5.value.filter { it.id != cartItemId }

    }


    sealed class CartScreenState2 {
        object Loading : CartScreenState2()
        data class Error(val errorMessage: String) : CartScreenState2()
        data class Success(val responseData: CartDTO?) : CartScreenState2()  // Assuming CartItem represents an item in the cart
    }

    // Data class representing the state of the shopping cart screen
    data class CartState2(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: CartDTO? = null  // Nullable List of CartItem to handle empty state
    ) {
        // Convert CartState to UI state (CartScreenState)
        fun toUiState(): CartScreenState2 {
            return when {
                isLoading -> CartScreenState2.Loading
                errorMessage?.isNotEmpty() == true -> CartScreenState2.Error(errorMessage)
                responseData != null -> CartScreenState2.Success(responseData)
                else -> CartScreenState2.Error("Unknown error")
            }
        }
    }

    //cart screen 3
    sealed class CartScreenState3 {
        object Loading : CartScreenState3()
        data class Error(val errorMessage: String) : CartScreenState3()
        data class Success(val responseData: List<CartItemDTO>?) : CartScreenState3()  // Assuming CartItem represents an item in the cart
    }

    // Data class representing the state of the shopping cart screen
    data class CartState3(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: List<CartItemDTO>? = null  // Nullable List of CartItem to handle empty state
    ) {
        // Convert CartState to UI state (CartScreenState)
        fun toUiState(): CartScreenState3 {
            return when {
                isLoading -> CartScreenState3.Loading
                errorMessage?.isNotEmpty() == true -> CartScreenState3.Error(errorMessage)
                responseData != null -> CartScreenState3.Success(responseData)
                else -> CartScreenState3.Error("Unknown error")
            }
        }
    }


    // Method to update the quantity and recalculate the total price
    // Method to update the quantity and recalculate the total price
    fun updateItemQuantity(cartItemId: String, increase: Boolean, cartItems: List<CartItemDTO>) {
        // Find the cart item by its ID
        val updatedCartItems = cartItems.toMutableList()
        val cartItem = updatedCartItems.find { it.id.toString() == cartItemId } // Convert cartItem.id to String

        if (cartItem != null) {
            // Update the quantity based on the 'increase' flag
            if (increase) {
                cartItem.quantity++
            } else {
                if (cartItem.quantity > 1) cartItem.quantity--  // Prevent going below 1
            }

            // Update the shopping cart state with the modified cart items

            _shoppingCartState5.update { currentList ->
                updatedCartItems // Replace the current list with the updated one
            }

            recalculateTotalPrice()

        }
    }    // Recalculate the total price based on cart item quantities and prices
    fun recalculateTotalPrice() {
        var total = 0f  // Initialize total price to 0

        // Iterate over each cart item and add the total price of the item (price * quantity)
        for (cartItem in shoppingCartState5.value) {
            val price = cartItem.product.price ?: 0f  // Default to 0f if price is invalid or null
            val quantity = cartItem.quantity  // Assuming quantity is always an integer

            // Add the price * quantity to the total
            total += price.toFloat() * quantity
        }

        // Update totalPrice using its internal setter (totalPrice.value)
        _totalPrice.value = total  // _totalPrice is mutable, we update its value here
    }

    // Optional: Method to remove an item from the cart

    //cart screen 1

    // Sealed class for different states in UI
    sealed class CartScreenState {
        object Loading : CartScreenState()
        data class Error(val errorMessage: String) : CartScreenState()
        data class Success(val responseData: CartItem?) : CartScreenState()  // Assuming CartItem represents an item in the cart
    }

    // Data class representing the state of the shopping cart screen
    data class CartState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: CartItem? = null  // Nullable List of CartItem to handle empty state
    ) {
        // Convert CartStat to UI state (CartScreenState)
        fun toUiState(): CartScreenState {
            return when {
                isLoading -> CartScreenState.Loading
                errorMessage?.isNotEmpty() == true -> CartScreenState.Error(errorMessage)
                responseData != null -> CartScreenState.Success(responseData)
                else -> CartScreenState.Error("Unknown error")
            }
        }
    }
}
