package com.yome.dildiy.ui.ecommerce.orderScreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.yome.dildiy.Product
import com.yome.dildiy.model.Orders
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.ui.ecommerce.checkout.OrderVm
import com.yome.dildiy.util.BaseUris
import org.koin.mp.KoinPlatform.getKoin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SoldScreen(navController: NavController, soldViewModel: SoldViewModel= getKoin().get()) {
    val getSoldScreenState by soldViewModel.getSoldViewState.collectAsState()
    val context = LocalContext.current

// Trigger to load sold items when the view is launched
    LaunchedEffect(true) {
        soldViewModel.getSoldItems(context)
    }

    when (getSoldScreenState) {
        // Loading state - shows CircularProgressIndicator
        is SoldViewModel.GetSoldScreenState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        // Success state - shows the list of sold items
        is SoldViewModel.GetSoldScreenState.Success -> {
            val soldItems = (getSoldScreenState as SoldViewModel.GetSoldScreenState.Success).responseData ?: emptyList()
            if (soldItems.isEmpty()) {
                Text(
                    text = "No sold items found.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                CartItemList(soldItems)
            }
        }

        // Error state - shows error message
        is SoldViewModel.GetSoldScreenState.Error -> {

        }
    }

}




@Composable
fun CartItemList(cartItems: List<Pair<CartItemDTO, Product>>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(cartItems) { (cartItemDTO, product) -> // Destructure the Pair directly
            CartItemView(cartItemDTO, product) // Pass the items to the view
        }
    }
}

@Composable
fun CartItemView(cartItem: CartItemDTO, product: Product) {
    // Row for displaying cart item info
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(Color.White)
        .padding(8.dp)
        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))) {

        // Product Image
        product.image?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Product Name and Quantity
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Quantity: ${cartItem.quantity}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Product Price
        Text(
            text = "Price: \$${product.price}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Green
        )
    }
}
