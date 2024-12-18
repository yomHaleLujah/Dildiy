//package com.yome.dildiy.ui.ecommerce.shoppingCart
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.yome.dildiy.Dao.CartRepository
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//
//class CartVM(private val repository: CartRepository)    : androidx.lifecycle.ViewModel() {
//
//    val cartItems: StateFlow<List<CartItem>> = repository.cartItems.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.Lazily,
//        initialValue = emptyList()
//    )
//
//    // Function to add a product with ID, name, price, and quantity
//    fun addItem(productId: Int, name: String, price: Double, quantity: Int) {
//        viewModelScope.launch {
//            val cartItem = CartItem(productId = productId, name = name, price = price, quantity = quantity)
//            repository.addCartItem(cartItem)
//        }
//    }
//
//    fun removeItem(item: CartItem) {
//        viewModelScope.launch {
//            repository.removeCartItem(item)
//        }
//    }
//}
