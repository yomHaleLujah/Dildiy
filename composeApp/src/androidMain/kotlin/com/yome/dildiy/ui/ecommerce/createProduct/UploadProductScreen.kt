package com.yome.dildiy.ui.ecommerce.createProduct


import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.yome.dildiy.R
import com.yome.dildiy.design.system.MyTextField
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.ui.ecommerce.profile.TopBar
import com.yome.dildiy.util.PreferencesHelper.getUsername
import org.koin.mp.KoinPlatform.getKoin
import java.io.File

@Composable
fun UploadScreen(
    uploadProductVm: UploadProductVm = getKoin().get(), navController: NavController
) {

    // State to observe the screen state
    val uplaodProductState by uploadProductVm.uploadProductState.collectAsState()
    val context: Context = LocalContext.current


    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val descriptionState = remember { mutableStateOf(TextFieldValue()) }
    val priceState = remember { mutableStateOf(TextFieldValue()) }
    val stockState = remember { mutableStateOf(TextFieldValue()) }
    val categoryState = remember { mutableStateOf(TextFieldValue()) }

    // Safely parse the price and stock, provide default values in case of empty string
    val price = priceState.value.text.toDoubleOrNull() ?: 0.0  // Default to 0.0 if invalid
    val stock = stockState.value.text.toIntOrNull() ?: 0  // Default to 0 if invalid

    val userId = getUsername(context)
    if (userId != null) {
        println("User ID: $userId")
    } else {
        println("No user data found!")
    }
    val product: Product = Product(
//        TODO()
        id = null,
        name = nameState.value.text,
        description = descriptionState.value.text,
        price = price,
        stock = stock,
        image = "",
        category = categoryState.value.text,
        imageList = emptyList(),
        userId = userId.toString(),
        vat = 15.0,
    )


    Column {

        Column(modifier = Modifier.padding(horizontal = 25.dp))

        {

            com.yome.dildiy.util.LottieAnimationView(R.raw.settingwithcolor)

            MyTextField(
                textFieldState = nameState.value,
                hint = "Product name",
                leadingIcon = Icons.Outlined.Person,
                keyboardType = KeyboardType.Text,
                onValueChange = {
                    nameState.value = it
                    uploadProductVm.name.value = it.text
                },
                modifier = Modifier.fillMaxWidth()
            )
            MyTextField(
                textFieldState = descriptionState.value,
                hint = "Product description",
                leadingIcon = Icons.Outlined.Description,
                keyboardType = KeyboardType.Text,
                onValueChange = {
                    if (it.text.length <= 350) { // Ensure input is limited to 50 characters
                        descriptionState.value = it
                        uploadProductVm.description.value = it.text
                    } else {
                        Toast.makeText(
                            context,
                            "Description cannot exceed 350 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MyTextField(
                    textFieldState = priceState.value,
                    hint = "Price",
                    leadingIcon = Icons.Outlined.Money,
                    keyboardType = KeyboardType.Text,
                    onValueChange = {
                        priceState.value = it
                        uploadProductVm.price.value = it.text
                    },
                    modifier = Modifier
                        .weight(1f) // Take half the available width
                        .fillMaxWidth()
                )
                MyTextField(
                    textFieldState = stockState.value,
                    hint = "In Stock",
                    leadingIcon = Icons.Outlined.Inventory,
                    keyboardType = KeyboardType.Text,
                    onValueChange = {
                        stockState.value = it
                        uploadProductVm.stock.value = it.text
                    },
                    modifier = Modifier
                        .weight(1f) // Take half the available width
                        .fillMaxWidth()
                )
            }


            MyTextField(
                textFieldState = categoryState.value,
                hint = "Product Category like Electronics, Clothing...",
                leadingIcon = Icons.Outlined.Category,
                keyboardType = KeyboardType.Text,
                onValueChange = {
                    if (it.text.length <= 50) {
                        categoryState.value = it
                        uploadProductVm.category.value = it.text
                    } else {
                        Toast.makeText(
                            context,
                            "Category cannot exceed 50 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Add Image", fontSize = 34.sp)
            Text(
                text = "For a better experience, please upload images with transparent PNG.",
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBF360C),
                    fontSize = 14.sp
                )
            )

            val imageUris = remember { mutableStateListOf<Uri>() }

            // Function to add an image URI (if there is room for more)
            fun addImageUri(uri: Uri) {
                if (imageUris.size < 4) {
                    imageUris.add(uri)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(imageUris.size + 1) { index ->
                    if (index < imageUris.size) {
                        // Display the existing image
                        MyImageArea(
                            directory = File(LocalContext.current.cacheDir, "images"),
                            uri = imageUris[index],
                            onSetUri = {
                                // Replace the image at the index with the new URI
                                imageUris[index] = it
                            }
                        )
                    } else {
                        // Add a new URI if there's room
                        if (imageUris.size < 4) {
                            val newUri = Uri.parse("file://new_image_path")
                            addImageUri(newUri)
                        }
                    }
                }
            }


            Button(
                onClick = {
                    uploadProductVm.uploadProduct(
                        product,
                        imageUris,
                        context
                    ) // Trigger the search when the button is clicked
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                androidx.compose.material.Text("Upload", fontSize = 18.sp, color = Color.White)
            }


        }

        // Layout based on the current state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val state = uplaodProductState.toUiState()) {
                is UploadProductVm.UploadProductScreenState.Loading -> {
                    CircularProgressIndicator()  // Show loading spinner
                }

                is UploadProductVm.UploadProductScreenState.Success -> {
                    navController.navigate("home")
                    Toast.makeText(
                        LocalContext.current,
                        "Product created successfully",
                        Toast.LENGTH_LONG
                    )
                }

                is UploadProductVm.UploadProductScreenState.Error -> {
                    Toast.makeText(
                        LocalContext.current,
                        "Upload product faild",
                        Toast.LENGTH_LONG
                    )
                }
            }
        }
    }
}

@Composable
fun AddImageButton(onAddImage: (Uri) -> Unit) {
    Box(
        modifier = Modifier
            .clickable {
                // Simulate adding a new image URI (replace with actual image picking logic)
                val newUri = Uri.parse("file://new_image_path")
                onAddImage(newUri)
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Add spacing between items
            modifier = Modifier.padding(16.dp) // Add padding around the row
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Image",
                modifier = Modifier.size(24.dp), // Set a consistent icon size
                tint = Color.Red // Icon color
            )
        }

    }
}
