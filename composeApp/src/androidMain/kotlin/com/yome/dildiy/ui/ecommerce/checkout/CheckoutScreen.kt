package com.yome.dildiy.ui.ecommerce.checkout

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Note
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.location.LocationServices
import com.yome.dildiy.remote.dto.Customization
import com.yome.dildiy.remote.dto.Payload
import com.yome.dildiy.R
import com.yome.dildiy.design.system.MyTextField
import com.yome.dildiy.model.OrderItem
import com.yome.dildiy.model.Orders
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.shoppingCart.ShoppingCartViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform.getKoin
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.time.LocalDateTime
import java.util.UUID


@SuppressLint("RememberReturnType")
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = getKoin().get(),
    navController: NavHostController,
    jsonCartItem: String,
    paymentViewModel: PaymentViewModel = getKoin().get()
) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp)) {
        Column(
            modifier = Modifier.padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make the column scrollable
        ) {
            LottieAnimationView2(R.raw.chcekout2)
            val firstName = viewModel.firstName.collectAsState()
            val lastName = viewModel.lastName.collectAsState()
            val emailAddress = viewModel.emailAddress.collectAsState()
            val phoneNumber = viewModel.phoneNumber.collectAsState()
            val specialInstructions = viewModel.specialInstructions.collectAsState()
            val address = viewModel.address.collectAsState()
            val apartment = viewModel.apartment.collectAsState()
//        val cartItem = Json.decodeFromString<CartItemDTO>(jsonCartItem)
            val cartItems: List<CartItemDTO> = Json.decodeFromString(jsonCartItem)
            Log.d("CheckoutScreen", cartItems.toString())
            val paymentState by paymentViewModel.paymentState.collectAsState()
//            var hasNavigatedToWebView by remember { mutableStateOf(false) }
            val hasNavigatedToWebView by paymentViewModel.hasNavigatedToWebView.collectAsState()
            val paymentState2 by paymentViewModel.paymentState2.collectAsState()

            var latlang = ""

            val tx_ref = paymentViewModel.getTxRef()

            MyTextField(
                textFieldState = TextFieldValue(firstName.value),
                hint = "First name",
                leadingIcon = Icons.Outlined.Person,
                keyboardType = KeyboardType.Text,
                onValueChange = { viewModel.setFistName(it.text) },
                modifier = Modifier.fillMaxWidth()
            )
            MyTextField(
                textFieldState = TextFieldValue(lastName.value),
                hint = "Last name",
                leadingIcon = Icons.Outlined.Person,
                keyboardType = KeyboardType.Text,
                onValueChange = { viewModel.setLastName(it.text) },
                modifier = Modifier.fillMaxWidth()
            )

            MyTextField(
                textFieldState = TextFieldValue(emailAddress.value),
                hint = "Email Address",
                leadingIcon = Icons.Outlined.Mail,
                keyboardType = KeyboardType.Text,
                onValueChange = {
                    viewModel.setEmailAddress(it.text)
                },
                modifier = Modifier.fillMaxWidth()
            )
            MyTextField(
                textFieldState = TextFieldValue(phoneNumber.value),
                hint = "Phone number",
                leadingIcon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Text,
                onValueChange = {
                    viewModel.setPhoneNumber(it.text)
                },
                modifier = Modifier.fillMaxWidth()
            )

            MyTextField(
                textFieldState = TextFieldValue(address.value),
                hint = "Address",
                leadingIcon = Icons.Outlined.Home, // You can use an icon like Icons.Outlined.Home for address
                keyboardType = KeyboardType.Text,
                onValueChange = { viewModel.setAddress(it.text) },
                modifier = Modifier.fillMaxWidth()
            )

            MyTextField(
                textFieldState = TextFieldValue(apartment.value),
                hint = "Apartment, Suite, etc.",
                leadingIcon = Icons.Outlined.LocationCity, // You can use an icon like Icons.Outlined.LocationCity for apartment
                keyboardType = KeyboardType.Text,
                onValueChange = {
                    viewModel.setApartment(it.text)
                },
                modifier = Modifier.fillMaxWidth()
            )
            // MyTextField for Special Instructions
            MyTextField(
                textFieldState = TextFieldValue(specialInstructions.value),
                hint = "Special Instructions (Optional)",
                leadingIcon = Icons.Outlined.Note, // You can use any suitable icon here
                keyboardType = KeyboardType.Text,
                onValueChange = { viewModel.setSpecialInstructions(it.text) },
                modifier = Modifier
                    .fillMaxWidth()
            )

            // "Select Drop-Off Location" button
            androidx.compose.material.Button(
                onClick = {
                    navController.navigate("mapScreen")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Pick Drop-Off location", fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                }
            }
            // Display selected location if available
            /*         val selectedLocation = navController.currentBackStackEntry
                         ?.savedStateHandle
                         ?.get<GeoPoint>("selectedLocation")

                     selectedLocation?.let {
                         Text("Selected Drop-Off Location:")
                         Text("Latitude: ${it.latitude}")
                         Text("Longitude: ${it.longitude}")
                         latlang = it.latitude.toString()+","+ it.longitude.toString()
                     }*/


            androidx.compose.material.Button(
                onClick = {
                    val order = prepareOrder(
                        fullName = firstName.value + " " + lastName.value,
                        email = emailAddress.value,
                        phone = phoneNumber.value,
                        address = address.value,
                        apartment = apartment.value,
                        specialInstructions = specialInstructions.value,
                        location = latlang,
                        cartItems = cartItems
                    )
                    val paymentRequest = Payload(
                        amount = order.totalPrice.toString(),  // Example amount, replace it with `order.amount` if needed
                        currency = "ETB", // Replace with the order currency if dynamic
                        email = emailAddress.value,  // Replace with the order email if dynamic
                        first_name = firstName.value,  // Replace with the order first name if dynamic
                        last_name = lastName.value,  // Replace with the order last name if dynamic
                        phone_number = phoneNumber.value,  // Replace with the order phone number if dynamic
                        tx_ref = tx_ref,  // Replace with a dynamic reference if necessary
                        callback_url = "https://webhook.site/077164d6-29cb-40df-ba29-8a00e59a7e60",  // Your callback URL
                        return_url = "",  // Your return URL after payment
                        customization = Customization(
                            title = "Payment",
                            description = order.userId
                        )
                    )

                    println("Payment Request $paymentRequest")
//                    viewModel.submitOrder(cartItems = cartItems, context = context, latlang= latlang) // Trigger the search when the button is clicked
                    paymentViewModel.initiatePayment(
                        order = order,
                        context = context,
                        paymentRequest = paymentRequest,
                    )
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                androidx.compose.material.Text("Pay now", fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Money,
                    contentDescription = "Map Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // "Select Drop-Off Location" button
            androidx.compose.material.Button(
                onClick = {
                    navController.navigate("mapScreen")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Confirm Order", fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                }
            }
            LaunchedEffect(paymentState) {
                when (val state = paymentState) {
                    is PaymentViewModel.PaymentState -> {
                        when {
                            state.isLoading -> {
                            }

                            (!hasNavigatedToWebView && state.checkoutUrl != null) -> {
                                println("hasNavigatedToWebView")
                                paymentViewModel.setNavigatedToWebView(true)
                                navController.navigate("webViewScreen?url=${Uri.encode(state.checkoutUrl)}&tx_ref=$tx_ref") {
                                    // Reset navigation state when the navigation stack changes
                                }

                            }

                            state.errorMessage != null -> {
                                // Handle error state
                                Log.e("PaymentError", state.errorMessage)
                            }
                        }
                    }
                }
            }




            // Reset navigation state when this screen is disposed
            DisposableEffect(Unit) {
                onDispose {
                    paymentViewModel.setNavigatedToWebView(true)
                }
            }

        }

    }
//        MapWithLocationPicker(navController)
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

@Composable
fun MapWithLocationPicker(navController: NavHostController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val mapView = remember { MapView(context) }
    val selectedLocation = remember { mutableStateOf<GeoPoint?>(null) }
    val currentLocation = remember { mutableStateOf<GeoPoint?>(null) }

    // Function to get current location
    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation.value = GeoPoint(location.latitude, location.longitude)
                    mapView.controller.setCenter(currentLocation.value)
                } else {
                    // Default to Addis Ababa if the current location is not available
                    mapView.controller.setCenter(GeoPoint(9.0370, 38.7510))
                }
            }
        } else {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
            // Default to Addis Ababa if permission is not granted
            mapView.controller.setCenter(GeoPoint(9.0370, 38.7510))
        }
    }

    // Get the current location when the composable is first launched
    LaunchedEffect(Unit) {
        getCurrentLocation()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter // Align content to the top center
    ) {
        // AndroidView for the Map
        AndroidView(
            factory = {
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setBuiltInZoomControls(true)
                    setMultiTouchControls(true)
                    controller.setZoom(17.0)

                    // Center map initially to current or default location
                    val initialCenter = currentLocation.value ?: GeoPoint(9.0370, 38.7510)
                    controller.setCenter(initialCenter)

                    overlays.add(
                        MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p?.let {
                                    selectedLocation.value = it
                                    Log.d(
                                        "MapPicker",
                                        "Location selected: Lat = ${it.latitude}, Lng = ${it.longitude}"
                                    )

                                    // Clear previous markers
                                    overlays.removeIf { overlay -> overlay is Marker }

                                    // Add a new marker for the selected location
                                    val marker = Marker(this@apply).apply {
                                        position = it
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        title = "Selected Location"
                                        icon = ContextCompat.getDrawable(
                                            context,
                                            R.drawable.marker50
                                        ) // Red marker
                                    }
                                    overlays.add(marker)
                                    invalidate()
                                }
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint?): Boolean = false
                        })
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Display the "Confirm Location" button only after a location is selected
        if (selectedLocation.value != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Center the button horizontally
            ) {
                androidx.compose.material.Button(
                    onClick = {
                        Toast.makeText(context, "Confirmed!", Toast.LENGTH_SHORT).show()
                        // Pass the selected location back to the CheckoutScreen
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedLocation", selectedLocation.value)
                        navController.popBackStack() // Navigate back to the previous screen
                    },
                    modifier = Modifier
                        .padding(8.dp).fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                ) {
                    Text(
                        text = "Click to Confirm your exact drop off Location",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}


fun redirectToPayment(context: Context, checkoutUrl: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl))
    context.startActivity(browserIntent)
}


@Composable
fun WebView() {

    // Declare a string that contains a url
    val mUrl = "https://www.google.com"

    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}

@Composable
fun WebViewScreen(
    url: String,
    txRef: String,
    navController: NavHostController,
    onDispose: () -> Unit = {}, viewModel: PaymentViewModel = getKoin().get()
) {
    // Remember WebView instance to handle navigation
    val context = LocalContext.current
    val webView = remember { WebView(context).apply { settings.javaScriptEnabled = true } }
    var canGoBack by remember { mutableStateOf(false) }
    val paymentState2 by viewModel.paymentState2.collectAsState()


    LaunchedEffect(paymentState2) {
        println("I am in launched effect")
        when (val state = paymentState2) {
            is PaymentViewModel.PaymentState2 -> {
                when {
                    state.isLoading -> {
                    }

                    (state.success != null) -> {
                        println("$txRef and ${state.success.data.tx_ref}")
                        navController.popBackStack()
                        viewModel.generateTxRef()
//                        if(txRef==state.success.data.tx_ref)   {
//                            println("$txRef and $state")
//                        }
                    }

                    state.errorMessage != null -> {
                        // Handle error state
                        Log.e("PaymentError", state.errorMessage)
                    }
                }
            }
        }
    }
//    val txRef = remember(url) {
//        Uri.parse(url).getQueryParameter("tx_ref")
//    }
//    val url = remember(url) {
//        Uri.parse(url).getQueryParameter("url")
//    }

    println(url + " My url and tx ref  " + txRef)

    Column(modifier = Modifier.fillMaxSize()) {
        // WebView
        AndroidView(
            factory = { webView },
            update = {
                if (url != null) {
                    it.loadUrl(url)
                }
                it.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        canGoBack = view?.canGoBack() == true
                    }
                }
            },
            modifier = Modifier.weight(1f) // Occupy remaining vertical space
        )

        // "Finish Payment" Button
        androidx.compose.material.Button(
            onClick = {
                // Navigate back or to another screen after finishing payment
                if (txRef != null) {
                    viewModel.verifyPayment(txRef)
                }
                // Navigate back
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 52.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),

            ) {
            Text(text = "Confirm Payment", fontSize = 18.sp, color = Color.White)
        }
    }

    // Handle back press
    BackHandler(enabled = canGoBack) {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            navController.popBackStack() // Navigate back if no more history
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onDispose()
            webView.destroy() // Clean up the WebView
        }
    }
}


fun prepareOrder(
    fullName: String,
    email: String,
    phone: String,
    address: String,
    apartment: String?,
    specialInstructions: String?,
    location: String,
    cartItems: List<CartItemDTO>
): Orders {

    Log.d("CartItems", "Cart items before preparing order: $cartItems")

    // Calculate the total price of the order
    val totalPrice = cartItems.sumOf { it.quantity * it.product.price }

    // Create a list of OrderItem from the CartItemDTO
    val orderItems = cartItems.map { cartItem ->
        OrderItem(
            productId = cartItem.product.id.toString(),
            productName = cartItem.product.name,
            quantity = cartItem.quantity,
            price = cartItem.product.price
        )
    }
    Log.d("OrderItems", "Order items created: $orderItems")

    // Generate timestamps (createdAt and updatedAt)
    val now = LocalDateTime.now().toString()

    // Prepare the cartItems as a JSON string (or can use other formats)
    val cartItemsJson = Json.encodeToString(cartItems)

    // Create the Order object
    return Orders(
        userId = "USER_ID", // This should be dynamically set (for example, from the logged-in user)
        fullName = fullName,
        email = email,
        phone = phone,
        address = address,
        apartment = apartment,
        specialInstructions = specialInstructions,
        location = location,
        totalPrice = totalPrice,
        createdAt = now,
        updatedAt = now,
        cartItems = cartItemsJson,
        items = orderItems
    )

}