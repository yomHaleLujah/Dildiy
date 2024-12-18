package com.yome.dildiy

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yome.dildiy.ui.ecommerce.ProductScreen.ProductHomeScreen
import com.yome.dildiy.networking.DildiyClient
import com.yome.dildiy.networking.createHttpClient
import com.yome.dildiy.ui.ecommerce.shoppingCart.CartScreen
import com.yome.dildiy.ui.ecommerce.checkout.CheckoutScreen
import com.yome.dildiy.ui.ecommerce.checkout.MapWithLocationPicker
import com.yome.dildiy.ui.ecommerce.checkout.OrderVm
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
import com.yome.dildiy.ui.mekina.PickupLocationScreen
import com.yome.dildiy.util.PreferencesHelper
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.androidx.compose.getKoin
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class MainActivity : ComponentActivity() {

    @SuppressLint("RememberReturnType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContent {

            var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }

            // Use NavController to control navigation between login and bottom navigation
            val navController = rememberNavController()

            // Conditional check to navigate between Login or Home

            val context = LocalContext.current

            // Check if the user is logged in (You can replace this with real user data logic)

            val jwtToken = remember { mutableStateOf(PreferencesHelper.getJwtToken(context)) }
            val userLoggedIn = remember { mutableStateOf(jwtToken!=null) }

            // Observe token changes in SharedPreferences
            LaunchedEffect(Unit) {
                // Add a listener for token changes in SharedPreferences
                val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == "jwt_token") {
                        jwtToken.value = PreferencesHelper.getJwtToken(context)
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
            }
            LaunchedEffect(Unit) {
                userLoggedIn.value = isUserExists(context) // Replace with real check
            }
            val orderVm: OrderVm = getKoin().get() // Ensure this is the correct way to instantiate

            preventInterruptionDuringOrder(
                context = context,
                orderVm = orderVm,
                onSuccess = {
                    Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show()
                },
                onFailure = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
            println("user_preference " + isUserExists(context))
            // Navigation Graph
            NavHost(
                navController = navController,
                startDestination =if (!getJwtToken(context).isNullOrEmpty()) "Home" else "login"
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
//                    if(PreferencesHelper.isMerchant(context))
                    UploadScreen(navController = navController)
                }

                composable("cart") {
                    CartScreen(navController = navController)
                }
                composable("mapScreen") { MapWithLocationPicker(navController) }

                composable("mekina"){
                    PickupLocationScreen(onLocationSelected = { location ->
                        selectedLocation = location
                        Log.d("PickupLocation", "Selected location: Lat = ${location.latitude}, Lng = ${location.longitude}")
                        // Navigate to the next screen or save the location
                        navController.navigate("nextScreen")
                    })
                }
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

fun preventInterruptionDuringOrder(context: Context, orderVm: OrderVm,onSuccess: () -> Unit, onFailure: (String) -> Unit){
    val order = PreferencesHelper.getOrder(context)
    if(order!=null){
        orderVm.placeOrder(order, context,
            onSuccess = {
                PreferencesHelper.clearOrder(context) // Clear saved order after success
                onSuccess() // Perform additional actions on success
            },
            onFailure = { errorMessage ->
                onFailure(errorMessage) // Notify about the failure
            }
        )
    }
}
fun getJwtToken(context: Context): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("jwt_token", null)
}
