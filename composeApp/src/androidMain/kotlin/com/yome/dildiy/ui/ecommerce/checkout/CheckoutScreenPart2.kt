package com.yome.dildiy.ui.ecommerce.checkout

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.room.Room
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.location.LocationServices
import com.yome.dildiy.R
import com.yome.dildiy.util.PreferencesHelper
import kotlinx.coroutines.Dispatchers
import org.koin.mp.KoinPlatform.getKoin
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

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
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp) // Control size explicitly

        )


    }
}

@Composable
fun MapWithLocationPicker(
    navController: NavHostController,
    viewModel: DeliveryVm = getKoin().get()
) {
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
        modifier = Modifier.fillMaxSize().padding(bottom = 55.dp),
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


@Composable
fun WebViewScreen(
    url: String,
    txRef: String,
    navController: NavHostController,
    onDispose: () -> Unit = {},
    viewModel: PaymentViewModel = getKoin().get(),
    orderVm: OrderVm = getKoin().get()
) {
    // Remember WebView instance to handle navigation
    val context = LocalContext.current
    val webView = remember { WebView(context).apply { settings.javaScriptEnabled = true } }
    var canGoBack by remember { mutableStateOf(false) }
    val paymentState2 by viewModel.paymentState2.collectAsState()
    val finishIt by viewModel.finishIt.collectAsState()
    val orderState by orderVm.orderState.collectAsState()


    LaunchedEffect(orderState) {
        when (val state = orderState) {
            is OrderVm.OrderState -> {
                when {
                    state.isLoading -> {
                    }

                    (state.success != null) -> {
                        navController.navigate("profile")
                        viewModel.generateTxRef2()
                    }

                    state.errorMessage != null -> {

                        finishIt?.let { order ->
                            {
                                PreferencesHelper.saveOrder(context, order)
                            }
                        }
                        // Handle error state
                        Log.e("PaymentError", state.errorMessage)
                    }
                }
            }
        }

    }
    LaunchedEffect(paymentState2) {
        println("I am in launched effect")
        when (val state = paymentState2) {
            is PaymentViewModel.PaymentState2 -> {
                when {
                    state.isLoading -> {
                    }

                    (state.success != null) -> {
                        println("$txRef and ${state.success.data.txRef}")
                        // Use finishIt in your UI logic
                        navController.navigate("profile")
                        viewModel.generateTxRef2()
//                        navController.navigate("profile")
                    }

                    state.errorMessage != null -> {
                        finishIt?.let { order ->
                            {
                                PreferencesHelper.saveOrder(context, order)
                            }
                        }
                        // Handle error state
                        Log.e("PaymentError", state.errorMessage)
                    }
                }
            }
        }
    }

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

        //"Finish Payment" Button
        androidx.compose.material.Button(
            onClick = {
                //Navigate back or to another screen after finishing payment
                if (txRef != null) {

                    finishIt?.let { order ->
                        val newOrder = order.copy(
                            txRef = txRef
                        )

                        orderVm.placeOrder(newOrder, context, {},
                            onFailure = {}
                        )
                    }
//                    viewModel.verifyPayment(txRef)
                }
                //Navigate back
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 55.dp),
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
