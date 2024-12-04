package com.yome.dildiy

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yome.dildiy.ui.ecommerce.ProductScreen.ProductHomeScreen
import com.yome.dildiy.networking.DildiyClient
import com.yome.dildiy.networking.createHttpClient
import com.yome.dildiy.shoppingCart.CartScreen
import com.yome.dildiy.ui.ecommerce.checkout.CheckoutScreen
import com.yome.dildiy.ui.ecommerce.checkout.MapWithLocationPicker
import com.yome.dildiy.ui.ecommerce.checkout.WebViewScreen
import com.yome.dildiy.ui.ecommerce.createProduct.UploadScreen
//import com.yome.dildiy.networking.DildiyClient
//import com.yome.dildiy.networking.createHttpClient
import com.yome.dildiy.ui.login.LoginScreen
import com.yome.dildiy.ui.ecommerce.navigation.AppBottomNavigation
import com.yome.dildiy.ui.ecommerce.productdetail.ProductDetailScreen
import com.yome.dildiy.ui.ecommerce.profile.ProfileScreen
//import com.yome.dildiy.ui.register.HomeScreen
import com.yome.dildiy.ui.register.RegisterScreen
import com.yome.dildiy.ui.ecommerce.search.SearchScreen
import com.yome.dildiy.util.PreferencesHelper
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    @SuppressLint("RememberReturnType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContent {

            val jwtToken = PreferencesHelper.getJwtToken(this)


//            startKoin {
//                androidContext(this@MainActivity)
//                modules(appModule()) // Pass your Koin module(s) here
//            }

            // Use NavController to control navigation between login and bottom navigation
            val navController = rememberNavController()

            // Conditional check to navigate between Login or Home
            val userLoggedIn = remember { mutableStateOf(false) }

            val context = LocalContext.current

            // Check if the user is logged in (You can replace this with real user data logic)
            LaunchedEffect(Unit) {
                userLoggedIn.value = isUserExists(context) // Replace with real check
            }

            println("user_preference " + isUserExists(context))
            // Navigation Graph
            NavHost(
                navController = navController,
                startDestination = if (jwtToken != null) "Home" else "login"
            ) {

                // Login & Register Screens
                composable("login") {
                    LoginScreen(navController = navController)  // Login screen is the starting screen
                }

                composable("register") {
                    RegisterScreen(navController = navController, client = remember {
                        DildiyClient(createHttpClient(OkHttp.create()))
                    })  // Register screen will be shown when navigating to this route
                }

                // Home & Bottom Navigation Screens
                composable("Home") {
                    ProductHomeScreen(navController = navController)
                }

                composable("Search") {
                    SearchScreen(navController = navController)
                }

                composable("Upload") {
                    UploadScreen(navController = navController)
                }

                composable("cart") {
                    CartScreen(navController = navController)
                }
                composable("mapScreen") { MapWithLocationPicker(navController) }

                composable(
                    "checkout/{jsonCartItem}",
                    arguments = listOf(navArgument("jsonCartItem") { type = NavType.StringType })
                ) { backStackEntry ->
                    val jsonCartItem = backStackEntry.arguments?.getString("jsonCartItem")
                    if (jsonCartItem != null) {
                        CheckoutScreen(
                            navController = navController, jsonCartItem = jsonCartItem
                        )
                    }
                }

                composable(
                    route = "webViewScreen?url={url}&tx_ref={tx_ref}",
                    arguments = listOf(
                        navArgument("url") { type = NavType.StringType },
                        navArgument("tx_ref") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val url = backStackEntry.arguments?.getString("url") ?: ""
                    val txRef = backStackEntry.arguments?.getString("tx_ref") ?: ""
                    WebViewScreen(url = url, txRef = txRef, navController = navController)
                }

                composable("Profile") {
                    ProfileScreen(navController = navController)
                }

                composable("productDetail/{productId}") { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId")
                    productId?.let {
                        ProductDetailScreen(productId = it.toInt(), navController = navController)
                    }
                }
            }

            // Show the bottom navigation bar if the user is logged in
            if (jwtToken != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AppBottomNavigation(navController = navController)
                }
            }
        }
    }
}

@Composable
fun MiddleCartScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        // Delay for visual effect (optional)
        delay(1000) // 1 second delay for demo purposes
        // Navigate to Cart
        navController.navigate("cart")
    }
}

@Composable
fun AppNavigation(client: DildiyClient) {
    // Initialize the NavController
    val navController = rememberNavController()

    // Set up the NavHost with login and register screens
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)  // Login screen is the starting screen
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                client = client
            )  // Register screen will be shown when navigating to this route
        }
        composable("product") {
            ProductHomeScreen(navController = navController)  // Register screen will be shown when navigating to this route
        }
//        composable("productDetail/{productId}") { backStackEntry ->
//            val productId = backStackEntry.arguments?.getString("productId")
//            ProductDetailScreen(productId = productId) // Navigate to product detail screen
//        }
    }
}

@Composable
fun InboxScreen() {
    Text("inbox")
}

// Function to check if a preference exists using SharedPreferences
fun isUserExists(context: Context): Boolean {
    // Get the SharedPreferences instance (you can change "my_preferences" to your preference name)
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preference", Context.MODE_PRIVATE)

    // Define the key you're checking for
    val preferenceKey = "user_preference"

    // Check if the key exists in SharedPreferences
    return sharedPreferences.contains(preferenceKey)
}