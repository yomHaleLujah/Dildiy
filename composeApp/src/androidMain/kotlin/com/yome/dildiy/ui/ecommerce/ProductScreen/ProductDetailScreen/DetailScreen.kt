package com.yome.dildiy.ui.ecommerce.ProductScreen.ProductDetailScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.ui.ecommerce.productdetail.ProductDetailVm
import com.yome.dildiy.ui.ecommerce.shoppingCart.ShoppingCartViewModel
import com.yome.dildiy.util.BaseUris.Companion.IMAGE_BASE_URL
import com.yome.dildiy.util.PreferencesHelper
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun ProductDetailScreen1(
    product: Product,
    viewModel: ProductDetailVm = getKoin().get(),
    popBack: () -> Unit,
    cartVm: ShoppingCartViewModel = getKoin().get(),
    navController: NavController,
) {
    // Observe the ViewModel's state
    val state by viewModel.productDetailState.collectAsState()
    val context = LocalContext.current
    val username = PreferencesHelper.getUsername(context)

    var selectedPicture by remember { mutableStateOf(product?.imageList?.get(0) ?: "") }
    var quantity by remember { mutableStateOf(1) }
    var cartQuantity by remember { mutableStateOf(0) } // This ensures recomposition
    val cartState2 by cartVm.shoppingCartState2.collectAsState()
    val cartQuantity2 by cartVm.cartQuantity.collectAsState()
    var displayedQuantity by remember { mutableStateOf(0) }

    // Calculate total price
    val totalPrice = remember { derivedStateOf { product.price * quantity.toDouble() } }

    LaunchedEffect(username) {
        cartVm.fetchCartItems(username.toString(), context)
    }

    LaunchedEffect(cartState2) {
        when (val state = cartState2.toUiState()) {
            is ShoppingCartViewModel.CartScreenState2.Success -> {
                val cartItems = state.responseData?.cartItems
                val targetCartItem = cartItems?.find { it.product.id.toInt() == product.id }

                // Avoid updating if already set
                if (targetCartItem != null && displayedQuantity != targetCartItem.quantity) {
                    displayedQuantity = targetCartItem.quantity
                    // Only update ViewModel if there's a real change
                    if (cartQuantity2 != targetCartItem.quantity) {
                        cartVm.updateCartQuantity(targetCartItem.quantity)
                    }
                }
            }

            else -> {}
        }
    }


    /*
        when (val state = cartState2.toUiState()) {
            is ShoppingCartViewModel.CartScreenState2.Loading -> {
            }

            is ShoppingCartViewModel.CartScreenState2.Success -> {

                val cartItems = state.responseData?.cartItems  // List of cart items
                val targetCartItem = cartItems?.find { it.product.id.toInt() == product.id }
                if (targetCartItem != null && displayedQuantity != targetCartItem.quantity) {
                    displayedQuantity = targetCartItem.quantity
                    cartVm.updateCartQuantity(targetCartItem.quantity)
                }else{}
            }

            is ShoppingCartViewModel.CartScreenState2.Error -> {
            }

        }
    */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 55.dp)
            .background(color = Color(0x8DB3B0B0))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { popBack() },
                modifier = Modifier
                    .background(color = Color.White, shape = CircleShape)
                    .clip(CircleShape)
            ) {
                Image(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null
                )
            }
            Row(
                modifier = Modifier
                    .width(70.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(3.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
//                        navController.navigate("middleCart?refresh=${System.currentTimeMillis()}")
                    },

                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cart $cartQuantity2",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Image(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null
                )

            }
        }

        // Image
        Image(
            painter = rememberImagePainter(IMAGE_BASE_URL + selectedPicture),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )

        // Thumbnail images
        LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            items(product.imageList.size) {
                IconButton(
                    onClick = { selectedPicture = product.imageList[it] },
                    modifier = Modifier
                        .size(50.dp)
                        .border(
                            width = 1.dp,
                            color = if (selectedPicture == product.imageList[it]) MaterialTheme.colors.primary else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(10.dp))
                        .padding(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Image(
                        painter = rememberImagePainter(IMAGE_BASE_URL + product.imageList[it]),
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Product details and actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White,
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(15.dp)
                ) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = product.description,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface
                    )

                }

                // Heart Icon
                IconButton(onClick = { /*TODO*/ }) {
                    Image(
                        imageVector = Icons.Default.HeartBroken,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.Red),
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0x75F44336),
                                shape = RoundedCornerShape(
                                    topStart = 20.dp,
                                    bottomStart = 20.dp
                                )
                            )
                            .padding(10.dp)
                            .weight(1f)
                    )
                }
            }

            // Quantity and Price Sectiona
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0x8DB3B0B0),
                        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                    )
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decrease Quantity
                    IconButton(
                        onClick = {
                            if (quantity > 1) {
                                quantity--
                            }
                        },
                        modifier = Modifier
                            .background(color = Color.White, shape = CircleShape)
                            .clip(CircleShape)
                    ) {
                        Image(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null
                        )
                    }

                    // Display Quantity
                    Text(
                        text = quantity.toString(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(35.dp)
                            .wrapContentHeight()
                    )

                    // Increase Quantity
                    IconButton(
                        onClick = {
                            val stock = product.stock
                            if (quantity < stock) {
                                quantity++
                            } else {
                                Toast.makeText(
                                    context,
                                    "You can add a maximum of $stock items at a time.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .background(color = Color.White, shape = CircleShape)
                            .clip(CircleShape)
                    ) {
                        Image(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }

                // Display Total Price
                Text(
                    text = "Total: \$${totalPrice.value}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            // Add to Cart Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 30.dp, bottom = 30.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    onClick = {
                        cartQuantity = quantity
                        val cartItemRequest = product.id?.let {
                            CartItemRequest(
                                productId = it,  // Assuming the product has an `id` field
                                cartId = null,           // Assuming cartId is not set yet
                                quantity = quantity
                            )
                        }
                        if (username != null) {
                            cartVm.addCartItems(
                                username = username,
                                cartItemRequest = cartItemRequest,
                                context = context
                            )
                        }
                        cartVm.updateCartQuantity(quantity)

                        Toast.makeText(
                            context,
                            "Successfully added to cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(text = "Add to Cart", fontSize = 16.sp)
                }
            }
        }
    }
}

@Serializable
data class CartItemRequest(
    val productId: Int,
    val cartId: Int?,  // Nullable type to allow 'null' value
    val quantity: Int
)