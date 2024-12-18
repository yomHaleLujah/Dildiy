package com.yome.dildiy.ui.ecommerce.checkout

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yome.dildiy.R
import com.yome.dildiy.design.system.MyTextField
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.remote.dto.Customization
import com.yome.dildiy.remote.dto.Payload
import com.yome.dildiy.util.PreferencesHelper
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform.getKoin
import org.osmdroid.util.GeoPoint


@SuppressLint("RememberReturnType")
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = getKoin().get(),
    navController: NavHostController,
    jsonCartItem: String,
    paymentViewModel: PaymentViewModel = getKoin().get(),
    deliveryVm: DeliveryVm = getKoin().get()
) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp)) {
        Column(
            modifier = Modifier.padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make the column scrollable
        ) {
            println(jsonCartItem + " Json cart Item")
            LottieAnimationView2(R.raw.chcekout2)
            val cartItems: List<CartItemDTO> = Json.decodeFromString(jsonCartItem)
            Log.d("CheckoutScreen", cartItems.toString())
            val paymentState by paymentViewModel.paymentState.collectAsState()
            val hasNavigatedToWebView by paymentViewModel.hasNavigatedToWebView.collectAsState()
            val deliveryPrice by deliveryVm.deliveryPrice.collectAsState()
            var latlang = ""
            val tx_ref = paymentViewModel.getTxRef()
            val totalPrice = cartItems.sumOf { it.quantity * it.product.price }
            val finishIt by paymentViewModel.finishIt.collectAsState()


            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "Sub total ",
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.Gray,
                        fontSize = 20.sp,
                    )
                    androidx.compose.material.Text(
                        text = "$${"%.2f".format(totalPrice)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
                Column(
                ) {
                    Text(
                        text = "Shipping",
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.Gray,
                        fontSize = 20.sp,
                    )
                    androidx.compose.material.Text(
                        text = "$${"%.2f".format(deliveryPrice)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
                Column(
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.Gray,
                        fontSize = 20.sp,
                    )
                    androidx.compose.material.Text(
                        text = "$${"%.2f".format(totalPrice + deliveryPrice)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            val FirstnameState by viewModel.nameState.collectAsState()
            val emailState by viewModel.emailState.collectAsState()
            val phoneNumberState by viewModel.phoneNumberState.collectAsState()
            val addressState by viewModel.addressState.collectAsState()
            val apartmentState by viewModel.apartmentState.collectAsState()
            val specialInstructionState by viewModel.specialInstructionState.collectAsState()

            val lastNameState by viewModel.lastName.collectAsState()


            MyTextField(
                textFieldState = FirstnameState,
                hint = "First Name",
                leadingIcon = Icons.Outlined.Person,
                onValueChange = { viewModel.setName(it) },
                modifier = Modifier.fillMaxWidth()
            )

            MyTextField(
                textFieldState = lastNameState, // Use the state from the ViewModel
                hint = "Last name",
                leadingIcon = Icons.Outlined.Person,
                keyboardType = KeyboardType.Text,
                onValueChange = {
                    viewModel.setLastName(it.text) // Update the ViewModel
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            MyTextField(
                textFieldState = emailState,
                hint = "Email",
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                onValueChange = { viewModel.setEmail(it) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            MyTextField(
                textFieldState = phoneNumberState,
                hint = "Phone Number",
                leadingIcon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone,
                onValueChange = { viewModel.setPhoneNumber(it) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            MyTextField(
                textFieldState = addressState,
                hint = "Address",
                leadingIcon = Icons.Outlined.LocationOn,
                onValueChange = { viewModel.setAddress(it) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            MyTextField(
                textFieldState = apartmentState,
                hint = "Apartment",
                leadingIcon = Icons.Outlined.Home,
                onValueChange = { viewModel.setApartment(it) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            MyTextField(
                textFieldState = specialInstructionState,
                hint = "Special Instructions",
                leadingIcon = Icons.Outlined.Info,
                onValueChange = { viewModel.setSpecialInstruction(it) },
                modifier = Modifier.fillMaxWidth()
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
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
//                        Icon(
//                            imageVector = Icons.Default.Map,
//                            contentDescription = "Map Icon",
//                            tint = Color.White,
//                            modifier = Modifier.size(24.dp)
//                        )

                    }
                }

                // Green checkmark positioned at the top-right corner
                if (deliveryPrice != 0.0) {
                    Image(
                        painter = painterResource(id = R.drawable.check2), // Drawable resource
                        contentDescription = "Selected Location",
                        modifier = Modifier
                            .size(45.dp)
                            .align(Alignment.TopEnd) // Align the icon to the top-right corner
                            .padding(4.dp), // Add some padding for spacing
                        // Prevent tinting of the image
                        colorFilter = null
                    )
                }
            }
            // Display selected location if available
            val selectedLocation = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<GeoPoint>("selectedLocation")

            selectedLocation?.let {
//                Text("Selected Drop-Off Location:")
//                Text("Latitude: ${it.latitude}")
//                Text("Longitude: ${it.longitude}")
                val request = DeliveryRequest(
                    cartItems = cartItems,
                    deliveryLocation = Location(it.latitude, it.longitude)
                )
                deliveryVm.calculateDeliveryPrice(request, context)



                latlang = it.latitude.toString() + "," + it.longitude.toString()
            }


            /*Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // "Select Drop-Off Location" button
                androidx.compose.material.Button(
                    onClick = {

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
//                        Icon(
//                            imageVector = Icons.Default.Map,
//                            contentDescription = "Map Icon",
//                            tint = Color.White,
//                            modifier = Modifier.size(24.dp)
//                        )

                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.check2), // Drawable resource
                    contentDescription = "Selected Location",
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.TopEnd) // Align the icon to the top-right corner
                        .padding(4.dp), // Add some padding for spacing
                    // Prevent tinting of the image
                    colorFilter = null
                )
            }*/
            var isLoading by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                androidx.compose.material.Button(
                    onClick = {
                        val result = viewModel.validateForm()
                        if (!result.isValid) {
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        } else {
                            paymentViewModel.generateTxRef()
                            isLoading = true
                            val userId = PreferencesHelper.getUsername(context)
                            val order = prepareOrder(
                                userId = userId!!,
                                fullName = FirstnameState.text + " " + lastNameState.text,
                                email = emailState.text,
                                phone = phoneNumberState.text,
                                address = addressState.text,
                                apartment = apartmentState.text,
                                specialInstructions = specialInstructionState.text,
                                location = latlang,
                                cartItems = cartItems
                            )
                            val newTotalPrice = order.totalPrice + deliveryPrice

                            val paymentRequest = Payload(
                                amount = newTotalPrice.toString(),  // Example amount, replace it with `order.amount` if needed
                                currency = "ETB", // Replace with the order currency if dynamic
                                email = emailState.text,  // Replace with the order email if dynamic
                                first_name = FirstnameState.text,  // Replace with the order first name if dynamic
                                last_name = lastNameState.text,  // Replace with the order last name if dynamic
                                phone_number = phoneNumberState.text,  // Replace with the order phone number if dynamic
                                tx_ref = tx_ref,  // Replace with a dynamic reference if necessary
                                callback_url = "https://webhook.site/077164d6-29cb-40df-ba29-8a00e59a7e60",  // Your callback URL
                                return_url = "",  // Your return URL after payment
                                customization = Customization(
                                    title = "Payment",
                                    description = order.userId
                                )
                            )

                            var order2 = order
                            order2.totalPrice += deliveryPrice

                            paymentViewModel.setFinishIt(order2)

//                    viewModel.submitOrder(cartItems = cartItems, context = context, latlang= latlang) // Trigger the search when the button is clicked
                            paymentViewModel.initiatePayment(
                                order = order,
                                context = context,
                                paymentRequest = paymentRequest,
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp) // Adjust size to fit inside the button
                        )
                    }
                    androidx.compose.material.Text("Pay now", fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Money,
                        contentDescription = "Map Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (isLoading)
                    Image(
                        painter = painterResource(id = R.drawable.check2), // Drawable resource
                        contentDescription = "Selected Location",
                        modifier = Modifier
                            .size(45.dp)
                            .align(Alignment.TopEnd) // Align the icon to the top-right corner
                            .padding(4.dp), // Add some padding for spacing
                        // Prevent tinting of the image
                        colorFilter = null
                    )
            }

            LaunchedEffect(paymentState) {
                when (val state = paymentState) {
                    is PaymentViewModel.PaymentState -> {
                        when {
                            state.isLoading -> {
                                println("hasNavigatedToWebView")

//                                paymentViewModel.setNavigatedToWebView(true)
//                                navController.navigate("webViewScreen?url=$url&tx_ref=$tx_ref") {
//                                    // Reset navigation state when the navigation stack changes
//                                }
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
}




