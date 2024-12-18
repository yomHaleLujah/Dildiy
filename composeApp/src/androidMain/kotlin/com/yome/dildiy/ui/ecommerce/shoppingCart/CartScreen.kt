package com.yome.dildiy.ui.ecommerce.shoppingCart

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yome.dildiy.R

import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.util.BaseUris
import com.yome.dildiy.util.PreferencesHelper
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform.getKoin


@OptIn(InternalCoroutinesApi::class)
@Composable
fun CartScreen(
    cartViewModel: ShoppingCartViewModel = getKoin().get(), navController : NavController
) {
//    val cartItemsState = remember { mutableStateOf<List<CartItemDTO>>(emptyList()) }
    val context = LocalContext.current
    val username = PreferencesHelper.getUsername(context)
    val cartState by cartViewModel.shoppingCartState2.collectAsState()
    val cartState2 by cartViewModel.shoppingCartState2.collectAsState()
//    var totalPrice by remember { mutableStateOf(0f) }
    var isCartEmpty by remember { mutableStateOf(true) } // State to track if the cart is empty
    val refreshTrigger = remember { mutableStateOf(false) }
    val totalPrice by cartViewModel.totalPrice // Observe the total price from ViewModel
    val cartItemsState = remember { mutableStateOf<List<CartItemDTO>>(emptyList()) }

    val checkout by remember { derivedStateOf { cartItemsState.value } }
    // Force recomposition when refreshTrigger changes
    LaunchedEffect(refreshTrigger.value) {
        // Perform any actions to refresh the screen, e.g., fetching cart items again
        cartViewModel.fetchCartItems(username.toString(), context)
    }

    // Function to trigger refresh
    fun refreshPage() {
        refreshTrigger.value = !refreshTrigger.value // Toggle to force recomposition
    }

    // Function to remove item from the local cartItemsState list
    fun removeCartItemLocally(cartItemId: Long) {
        cartItemsState.value = cartItemsState.value.filter { it.id != cartItemId }
        println("checkout 5 $checkout")

    }

    LaunchedEffect(username) {
        Log.d("Cart Screen", "Fetching items again" )
        cartViewModel.fetchCartItems(username.toString(), context)
        println("checkout 4 $checkout")

    }
    // Update `isCartEmpty` whenever `cartItemsState` changes
    LaunchedEffect(cartItemsState.value) {
        isCartEmpty = cartItemsState.value.isEmpty()
    }


    LaunchedEffect(cartState) {
        when (val state = cartState2.toUiState()) {
            is ShoppingCartViewModel.CartScreenState2.Success -> {
                cartItemsState.value = state.responseData?.cartItems ?: emptyList()
                println("checkout 3 $checkout")

                val cartItems = state.responseData?.cartItems
            }
            is ShoppingCartViewModel.CartScreenState2.Error -> {
                // Handle error state
            }
            else -> {
                // Handle other states like Loading if needed
            }
        }
    }
    // Update cart items when the state changes

    // Update the total price when cart items change
    // Update the total price when cart items change
    // Update cart items and calculate the total price whenever the cart state changes

    if(!isCartEmpty)
    Column(
        modifier = Modifier
            .padding(16.dp) // Optional padding around the column
            .fillMaxSize(), // Make sure the column takes full size of the screen
        verticalArrangement = Arrangement.spacedBy(8.dp), // Space between items
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!isCartEmpty) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Receipt icon section
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color(0x8DB3B0B0), shape = RoundedCornerShape(15.dp))
                            .clip(RoundedCornerShape(15.dp))
                            .padding(8.dp) // Adding padding inside the icon
                    )

                    // Total price section
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.subtitle1,
                            color = Color.Gray
                        )
                        Text(
                            text = "$${"%.2f".format(totalPrice)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary
                        )
                    }

                    // Checkout button section
                    Button(
                        onClick = {
                            println("Chekout main $checkout")

                            checkout?.let {
                                val jsonCartItem = Json.encodeToString(it)

                                navController.navigate("checkout/${jsonCartItem}")
                            }
//                            navController.navigate("checkout")
                           },
                        modifier = Modifier
                            .height(45.dp) // Adjusted height to match the icon size
                            .padding(start = 8.dp), // Optional padding between elements
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor =Color.Red ,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Checkout >",
                            style = MaterialTheme.typography.button
                        )
                    }
                }
            }

        }


        // Displaying the loading, success, or error state
        when (val state = cartState2.toUiState()) {
            is ShoppingCartViewModel.CartScreenState2.Loading -> {
                CircularProgressIndicator()  // Show loading spinner
            }

            is ShoppingCartViewModel.CartScreenState2.Success -> {
                val cartItems = state.responseData?.cartItems  // List of cart items
                // Calculate total price from cart items

//                println("checkout 2 $checkout")

                if (cartItems.isNullOrEmpty()) {
                    Text(
                        text = "Your cart is empty.",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.height(20.dp)
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    for (cartItem in cartItems) {
                        println("Cart Item  $cartItem")
                    }
                    cartItemsState.value = cartItems

                    // Displaying cart items list in a LazyColumn
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight()
                            .fillMaxWidth()
                            // Adjust as needed
                            .heightIn(min = 0.dp) // Allow column to expand without clipping
                    ) {
                        items(cartItemsState.value) { cartItem ->
                            var quantity = remember { mutableStateOf(cartItem.quantity) }

                            CartItemRow(
                                cartItem = cartItem,
                                onQuantityChange = { updatedQuantity ->
                                    quantity.value = updatedQuantity // Update the quantity state

                                    // Update total price based on quantity change
                                    val difference =
                                        (updatedQuantity - cartItem.quantity) * cartItem.product.price
                                    val newTotal = totalPrice + difference

                                    // Update the cart item's quantity locally
                                    cartItem.quantity = updatedQuantity
                                },
                                onRemoveItem = {
                                    cartItem.id?.let { removeCartItemLocally(it) }
                                    printSelectedItems(checkout!!)
                                    refreshPage()  // Trigger full screen refresh
                                    cartItemsState.value = cartItemsState.value.filter { it.id != cartItem.id }
                                    println("checkout 1 $checkout")
                                    print("cartItemsState.value " + cartItemsState.value)
                                    print("cartItemsState.value" + cartItem.id )
                                    val newTotal =
                                        totalPrice - (cartItem.product.price * cartItem.quantity)
                                },
                                cartVm = cartViewModel
                            )
                        }
                    }
                }
            }

            is ShoppingCartViewModel.CartScreenState2.Error -> {
                Text(
                    text = "Error: ${state.errorMessage}",
                    color = Color.Red,
                    style = MaterialTheme.typography.h1
                )
            }
        }




    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize(), // Takes up the full size of the screen
            contentAlignment = Alignment.Center // Centers content within the Box
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your Cart Is Empty",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )

                // Add Lottie animation below the text, if you like
                LottieAnimationView2(R.raw.cart2)
            }
        }
    }
}



@Composable
fun CartItemRow(cartVm : ShoppingCartViewModel,cartItem: CartItemDTO, onQuantityChange: (Int) -> Unit, onRemoveItem: () -> Unit, ) {

    var quantity by remember { mutableStateOf(cartItem.quantity) }  // Track quantity state
    println("Quantity $quantity")
    // Get the available stock from the product
    val availableStock = cartItem.product.stock
    val context = LocalContext.current



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    // Handle vertical drag if needed
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Product image
        Image(
            painter = rememberImagePainter(BaseUris.IMAGE_BASE_URL + cartItem.product.image),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .background(Color(0x8DB3B0B0), shape = RoundedCornerShape(10.dp))
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        // Product details (Name, Price, Quantity)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cartItem.product.name ?: "Unknown Item",
                fontWeight = FontWeight(700),
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "$${cartItem.product.price}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "  x $quantity")
            }
        }

        // Quantity update section (Plus and Minus buttons)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrease Quantity
            IconButton(
                onClick = {
                    if(quantity==1){
                        onRemoveItem() // Trigger remove item action
                        cartVm.deleteCartItem(cartItem.product.id.toString(), context)

                    }
                    if (quantity >= 1) {
                        quantity--
                        onQuantityChange(cartItem.quantity - 1) // Update the quantity in the cart
                        cartVm.decreaseToTotalPrice(cartItem.product.price)
                    }
                    else {
                        onRemoveItem() // Trigger remove item action
                        cartVm.deleteCartItem(cartItem.product.id.toString(), context)
                    }
                },
                modifier = Modifier
                    .background(color = Color.White, shape = CircleShape)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease Quantity"
                )
            }

            // Display Quantity
            Text(
                text ="$quantity",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(35.dp)
                    .wrapContentHeight()
            )

            // Increase Quantity
            IconButton(
                onClick = {

                    if (quantity < availableStock) { // Ensure the quantity doesn't exceed available stock
                        quantity++
                        onQuantityChange(cartItem.quantity + 1) // Update the quantity in the cart
                        cartVm.addToTotalPrice(cartItem.product.price)

                    } else {
                        Toast.makeText(
                            context,
                            "Cannot add more than the available stock: $availableStock",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .background(color = Color.White, shape = CircleShape)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase Quantity"
                )
            }
        }

        // Remove Item Button
        IconButton(
            onClick = {
                onRemoveItem() // Trigger remove item action
                cartVm.deleteCartItem(cartItem.product.id.toString(), context)
                // Call ViewModel's delete function to ensure the backend is updated
                },
            modifier = Modifier
                .background(color = Color.White, shape = CircleShape)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove Item"
            )
        }
    }


}

fun printSelectedItems(cartItems: List<CartItemDTO>) {
    println("checkout main 3 $cartItems")
}

@Composable
fun LottieAnimationView2(resources: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resources))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Keep playing the animation
    )

    // Center the animation in a constrained Box
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth() // Limit to parent width
            .padding(16.dp) // Add padding for spacing
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(300.dp) // Control size explicitly

        )


    }
}


