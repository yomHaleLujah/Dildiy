package com.yome.dildiy.ui.ecommerce.ProductScreen
import com.google.accompanist.pager.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.yome.dildiy.R
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.util.BaseUris
import com.yome.dildiy.util.BaseUris.Companion.IMAGE_BASE_URL
//import com.yome.dildiy.ui.register.HomeUIContent
//import com.yome.dildiy.ui.register.HomeViewModel
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductHomeScreen(
    viewModel: ProductViewModel = getKoin().get(),
//    client: DildiyClient,
    navController: NavController
) {
    val productScreenState by viewModel.productViewState.collectAsState()


    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getProducts(context)
    }

    Box(        modifier = Modifier.fillMaxSize() // Use fillMaxSize to fill the entire screen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp) // Padding to avoid the bottom navigation overlap
        ) {
            when (productScreenState) {
                is ProductViewModel.ProductScreenState.Loading -> {
                    CircularProgressIndicator()
                }

                is ProductViewModel.ProductScreenState.Success -> {
                    println("show listt ")
                    // Show the list of products in a vertical pager
                    val products =
                        (productScreenState as ProductViewModel.ProductScreenState.Success).responseData
                    VerticalPager(
                        count = products.size,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        ProductCard(products[page], navController)
                    }
                }

                is ProductViewModel.ProductScreenState.Error -> {
                    // Show error message
                    Text(
                        text = "Error:EcommerceViewModel.ProductState.Error",
//                        modifier =
//                        Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        // Search icon stays fixed at top right
        IconButton(
            onClick = {
                // Handle the search action
                println("Search clicked!")
            },

            modifier = Modifier
                .align(Alignment.TopEnd).padding(top = 16.dp, end = 16.dp)
        ) {
            Icon(modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.Search, // Search icon
                contentDescription = "Search",
                tint = Color.White,

            )
        }
    }
}


@Composable
fun ProductCard(product: Product, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray) // Set a contrasting background color

    ) {
        // Full-screen image
        Image(
            painter = rememberImagePainter(IMAGE_BASE_URL + product.image), // Replace with your drawable resource
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight().clickable {
                    navController.navigate("productDetail/${product.id}")
                },
            contentScale = ContentScale.FillWidth // Ensure image takes up the whole screen and is cropped
        )

        Column(
            modifier = Modifier.fillMaxWidth()

//                .fillMaxSize()
                .padding (horizontal = 16.dp)
                .align(Alignment.BottomStart)// Optional padding for the entire screen
        ) {
            // Product Name (Username)
            Text(
                text = product.name ?: "Unknown User", // Replace with the actual username
                fontSize = 20.sp, // Adjust font size to make it more readable
                fontWeight = FontWeight.Bold, // Make it bold for emphasis
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Product Description
            Text(
                text = product.description, // Product description
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp) // Padding below description text
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.7f)),  // Correct color parameter for Material3
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "\$${product.price}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )

                }
                Button(
                    onClick = {
                        navController.navigate("productDetail/${product.id}")

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),  // Correct color parameter for Material3
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Shop Now >",
                        color = Color.White, // White text color
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

        }

    }

}




@Composable
fun ProductDetailsScreen(
    product: Product, // Assume you pass a product object with username and description
    onShopNowClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Optional padding for the entire screen
    ) {
        // Product Name (Username)
        Text(
            text = "${product.name}", // Replace with actual username
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp) // Padding below username text
        )

        // Product Description
        Text(
            text = product.description, // Product description
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp) // Padding below description text
        )


        Button(
            onClick = onShopNowClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RectangleShape
        ) {
            Text(
                text = "Shop Now >",
                color = Color.White, // White text color
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
