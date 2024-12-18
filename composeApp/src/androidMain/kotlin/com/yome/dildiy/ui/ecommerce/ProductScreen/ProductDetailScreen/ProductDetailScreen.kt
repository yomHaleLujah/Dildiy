package com.yome.dildiy.ui.ecommerce.productdetail

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.yome.dildiy.remote.dto.Product
//import com.yome.dildiy.ui.ecommerce.shoppingCart.CartVM
import com.yome.dildiy.util.PreferencesHelper
import org.koin.mp.KoinPlatform.getKoin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yome.dildiy.R
import com.yome.dildiy.ui.ecommerce.ProductScreen.ProductDetailScreen.ProductDetailScreen1

@Composable
fun ProductDetailScreen(
    productId: Int,  // The productId you want to display
    productDetailVm: ProductDetailVm = getKoin().get(),
    navController: NavController
//    cartViewModel: CartVM = getKoin().get()  // Inject CartViewModel
) {
//    DetailScreen()

    // State to observe the screen state
    val homeState by productDetailVm.productDetailState.collectAsState()
    var productInCart = 0
    var quantity by remember { mutableStateOf(1) }
    val context = LocalContext.current
    // Trigger the function to fetch the product when the screen is loaded
    LaunchedEffect(productId) {
        productDetailVm.getProduct(productId, context)
    }

    // Layout based on the current state

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = homeState.toUiState()) {
            is ProductDetailVm.HomeScreenState.Loading -> {
                CircularProgressIndicator()  // Show loading spinner
            }

            is ProductDetailVm.HomeScreenState.Success -> {
                // Show product details
               val product = state.responseData
                println(product.toString() + "response data")
                ProductDetailScreen1(navController = navController, product = product,popBack = { navController.popBackStack() } // Close the screen when back arrow is clicked
                )

//                ProductDetailContent(
//                    state.responseData,
//                    context = LocalContext.current,
////                    cartViewModel = cartViewModel
//                )
            }

            is ProductDetailVm.HomeScreenState.Error -> {
                // Show error message
                Text(
                    text = "Error: ${state.errorMessage}",
                    color = Color.Red,
                    style = MaterialTheme.typography.h1
                )
            }
        }
    }


}


/*@Composable
fun ProductDetailContent(product: Product, context: Context,) {
    // State to manage full-screen image viewing
    var isFullScreenImageVisible by remember { mutableStateOf(false) }
    var fullScreenImageUrl by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    val maxStock = product.stock ?: 1
    var cartCount by remember { mutableStateOf(PreferencesHelper.getCartCount(context)) }


    // Update SharedPreferences when the cart count changes
    LaunchedEffect(cartCount) {
        PreferencesHelper.setCartCount(context, cartCount)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Main content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main product image
            Image(
                painter = rememberImagePainter("https://i.ibb.co/WPbkb9W/habesha-girl.png"),
                contentDescription = product.name,
                modifier = Modifier
                    .width(250.dp) // Adjust the size as needed
                    .height(350.dp)
                    .padding(bottom = 16.dp)
            )

            // Product details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Product Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Product Description
                Text(
                    text = product.description ?: "No description available",
                    style = MaterialTheme.typography.body2.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Product Price
                Text(
                    text = "Price: $${product.price}",
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Stock info
                if (product.stock != null) {
                    Text(
                        text = "In Stock: ${product.stock}",
                        style = MaterialTheme.typography.body2.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            // Display 3 smaller images below the main image
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Small images
                for (i in 1..3) {
                    Image(
                        painter = rememberImagePainter("https://i.ibb.co/WPbkb9W/habesha-girl.png"), // Placeholder image URL
                        contentDescription = "Small Image $i",
                        modifier = Modifier
                            .size(100.dp)
                            .clickable {
                                // Show the clicked image in fullscreen
                                fullScreenImageUrl = "https://i.ibb.co/WPbkb9W/habesha-girl.png"
                                isFullScreenImageVisible = true
                            }
                    )
                }

            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(8.dp)
            ) {

                Button(
                    onClick = {
                        if (quantity > 1) quantity -= 1
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                ) {
                    Text("-", color = Color.White, fontSize = 20.sp)
                }

                Text(
                    text = "$quantity",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { if (quantity < maxStock) quantity += 1 },
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                ) {
                    Text("+", color = Color.White, fontSize = 20.sp)
                }
            }

            // Add to Cart Button
            Button(
                onClick = {
                    product.id?.let {
//                        cartViewModel.addItem(
//                            productId = it,
//                            name = product.name,
//                            price = product.price,
//                            quantity = quantity
//                        )
                    }
                    cartCount += quantity
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            ) {
                Text(color = Color.White, text = "Add to Cart")
            }

        }

        // "Add to Cart" button in the top-right corner
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Button(
                onClick = { *//* Handle add to cart action *//* },
                shape = RoundedCornerShape(10.dp),
                colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                modifier = Modifier.padding(8.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "add to cart",
                        tint = Color.Black, // Icon color (white)
                        modifier = Modifier.size(24.dp).height(50.dp)
                        // Adjust the size as needed
                    )
                    Text(text = "$cartCount")
                }


            }
        }

        // Full-screen image dialog
        if (isFullScreenImageVisible) {
            Dialog(onDismissRequest = { isFullScreenImageVisible = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)), // Dark background
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberImagePainter(fullScreenImageUrl),
                        contentDescription = "Full-screen Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(16.dp)
                            .clickable {
                                isFullScreenImageVisible = false
                            } // Click to close fullscreen
                    )
                }
            }
        }
    }
}



@Composable
fun DetailScreen() {
    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
    ) {
        // Profile Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(R.drawable.image_1), // Replace with your image
                contentDescription = "Profile Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Kai's creator portfolio",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Image(
                        painter = painterResource(R.drawable.image_1), // Replace with your image
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                            .padding(2.dp)
                            .background(Color.LightGray, CircleShape)
                            .padding(2.dp)
                            .background(Color.White, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "@kaiblue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        // Content Section
        Surface(
            color = Color.Black,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Hi, I’m Kai. I’m a beauty and skincare enthusiast. Believer in self-care. 🌸🌈",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Previous work",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Blue,
                        modifier = Modifier.clickable { *//* Handle edit click *//* }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "I love speaking with my followers with honesty, talking about my real opinion on products.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Image Gallery Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(5) { index ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(100.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.image_1), // Replace with your image
                                contentDescription = "Work Sample",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button
                Button(
                    onClick = { *//* Handle add section click *//* },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Add section", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}*/

