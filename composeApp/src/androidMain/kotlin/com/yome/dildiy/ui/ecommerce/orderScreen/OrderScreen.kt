package com.yome.dildiy.ui.ecommerce.orderScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.yome.dildiy.model.Orders
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.ui.ecommerce.checkout.OrderVm
import com.yome.dildiy.util.BaseUris
import org.koin.mp.KoinPlatform.getKoin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun OrderScreen(navController: NavController, orderViewModel: OrderVm = getKoin().get()) {
    val getOrderScreenState by orderViewModel.getOrderViewState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(true) {
        orderViewModel.getOrders(context)
    }
    when (getOrderScreenState) {
        is OrderVm.GetOrderScreenState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        is OrderVm.GetOrderScreenState.Success -> {
            val orders = (getOrderScreenState as OrderVm.GetOrderScreenState.Success).responseData ?: emptyList()
            if (orders.isEmpty()) {
                Text(
                    text = "No orders found.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = "Your Orders",
                            modifier = Modifier.padding(22.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    items(orders.size) { order ->
                        OrderItemView(orders.get(order), navController = navController)
                    }
                }
            }
        }

        is OrderVm.GetOrderScreenState.Error -> {
            Text(
                text = "Error: ${(getOrderScreenState as OrderVm.GetOrderScreenState.Error).errorMessage}",
                modifier = Modifier.padding(16.dp),
                color = Color.Red
            )
        }
    }
}


@Composable
fun OrderItemView(order: Orders, navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        val orderDateTime = formatOrderDateTime(order.createdAt)
        val formattedTotalPrice = formatPrice(order.totalPrice)

        Text(text = "Order Date: $orderDateTime | Total: $formattedTotalPrice", color = Color.Red // Example for green
        )

        order.cartItems.forEach { cartItem ->
            OrderDetails(cartItem, navController = navController)
        }
    }
}


@Composable
fun OrderDetails(cartItem: CartItemDTO, navController: NavController) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            val painter: Painter = rememberImagePainter(BaseUris.IMAGE_BASE_URL+cartItem.product.image)
            Image(
                painter = painter,
                contentDescription = "Product Image",
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth().clickable {
                    navController.navigate("productDetail/${cartItem.product.id}")
                }
            )
            Text(text = cartItem.product.name, modifier = Modifier.padding(top = 8.dp))
            Text(text = "$${cartItem.product.price}", modifier = Modifier.padding(top = 4.dp))
        }
    }
}

fun formatOrderDateTime(dateTime: String): String {
    return try {
        val parsedDateTime = LocalDateTime.parse(dateTime) // Parse ISO 8601 datetime string
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm") // Desired format
        parsedDateTime.format(formatter)
    } catch (e: Exception) {
        dateTime // Fallback to original string if parsing fails
    }
}
fun formatPrice(price: Double): String {
    return String.format("%.2f", price)
}