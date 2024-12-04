package com.yome.dildiy.ui.ecommerce.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberImagePainter
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.remote.dto.User
import com.yome.dildiy.ui.ecommerce.navigation.AppBottomNavigation
import com.yome.dildiy.util.BaseUris
import com.yome.dildiy.util.PreferencesHelper
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import org.koin.mp.KoinPlatform.getKoin


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController, modifier: Modifier = Modifier, profileVm: ProfileVm = getKoin().get(),
) {
    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController = navController)
        }
    ) {

        // Check for JWT token in SharedPreferences
        val context = LocalContext.current
        val token = getJwtToken(context)

        if (token.isNullOrEmpty()) {
            DummyProfile(navController)
        } else {

                UserProfile(navController, profileVm = profileVm)
        }
    }
}


@Composable
fun UserProfile(navController: NavController, profileVm: ProfileVm) {
    val context = LocalContext.current

    // State to hold user data
    var user by remember { mutableStateOf<User?>(null) }
    user = PreferencesHelper.getUsername2(context)

    // State for toggling product list visibility
    var showProductList by remember { mutableStateOf(true) }

    // Observe product list state from ViewModel
    val profileScreenState by profileVm.productViewState.collectAsState()

    // Fetch the products when the user clicks "List"
   LaunchedEffect(true) {
        println("I am inside the launched effect")
        if (showProductList) {
        user?.username?.let { profileVm.getAllProduct(it,context) }
        }
    }

    if (user != null) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Logout Button
            Text(
                text = "Logout",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable {
                        PreferencesHelper.clearJwtToken(context = context)
                        navController.navigate("register") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Profile Picture
                Image(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(177.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Display username
                Text(
                    text = "${user?.username}",
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.height(10.dp))


                ProfileScreen2(navController)

                // Conditionally show product list
/*
                    when (profileScreenState) {
                        is ProfileVm.ProfileScreenState.Loading -> {
                            CircularProgressIndicator()
                        }

                        is ProfileVm.ProfileScreenState.Success -> {
                            println("ProfileScreenState Success")
                            ProductList(
                                productList = (profileScreenState as ProfileVm.ProfileScreenState.Success).responseData,
                                navController = navController
                            )
                        }

                        is ProfileVm.ProfileScreenState.Error -> {
                            // Show error message
                            androidx.compose.material3.Text(
                                text = "Error:EcommerceViewModel.ProductState.Error",
//                        modifier =
//                        Modifier.align(Alignment.Center)
                            )
                        }
                    }
*/
                }
        }
    } else {
        Text(text = "Loading user data...")
    }
}


@Composable
fun DummyProfile(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Large Profile Icon
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            modifier = Modifier.size(100.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Compose Button
        Button(
            onClick = {
            PreferencesHelper.clearJwtToken(context = context)
                navController.navigate("login")
                },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.5f)
                .height(50.dp)
        ) {
            Text("Logout", fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
fun ProductList(modifier: Modifier = Modifier, productList: List<Product>, navController: NavController) {
    println("Lazy vertical grid")
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 items per row
        contentPadding = PaddingValues(16.dp), // Optional padding
        modifier = modifier.fillMaxSize()
    ) {
        items(productList.size) { product ->
            // Create your product item UI here, including image and click action
            ProductItem(product = product, navController = navController, productList )
        }
    }
}

@Composable
fun ProductItem(product: Int, navController: NavController, productList: List<Product>) {
    // Handle click to navigate to the product detail screen
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate to product detail screen
                navController.navigate("productDetail/${productList.get(product).id}")
            }
    ) {
        Image(
            painter = rememberImagePainter(BaseUris.IMAGE_BASE_URL+ productList.get(product).image), // Image URL from the product object
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Ensures square aspect ratio for images
        )
    }
}

@Composable
fun IconRowList(navController: NavController, onListClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly, // Distribute the icons evenly across the row
        verticalAlignment = Alignment.CenterVertically // Align icons vertically at the center
    ) {
        // First Icon (Product List)
        IconButton(onClick = { navController.navigate("productList") }) {
            Icon(
                imageVector = Icons.Filled.List,
                contentDescription = "Product List",
                modifier = Modifier.size(30.dp)
            )
        }

        // Second Icon (Order List)
        IconButton(onClick = { navController.navigate("orderList") }) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCartCheckout,
                contentDescription = "Order List",
                modifier = Modifier.size(30.dp)
            )
        }

        // Third Icon (Sold List)
        IconButton(onClick = { navController.navigate("soldList") }) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Sold List",
                modifier = Modifier.size(30.dp)
            )
        }

        // Fourth Icon (Another Section)
        IconButton(onClick = { navController.navigate("anotherSection") }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "Another Section",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

