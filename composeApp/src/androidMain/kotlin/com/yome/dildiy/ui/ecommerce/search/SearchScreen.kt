package com.yome.dildiy.ui.ecommerce.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.airbnb.lottie.LottieAnimationView
import com.yome.dildiy.R
import com.yome.dildiy.design.system.MyTextField
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.ui.ecommerce.profile.TopBar
import com.yome.dildiy.util.BaseUris
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun SearchScreen(viewModel: SearchViewModel = getKoin().get(), navController: NavController) {
    val searchState by viewModel.searchViewState.collectAsState()

    val searchByNameState = remember { mutableStateOf(TextFieldValue()) }
    val searchByDescription = remember { mutableStateOf(TextFieldValue()) }
    val searchByCategory = remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current

    Column(){
//        Spacer(modifier = Modifier.height(10.dp))
//        TopBar("Search")
//        Spacer(modifier = Modifier.height(16.dp))

        com.yome.dildiy.util.LottieAnimationView(R.raw.search2)

        Column( modifier = Modifier.padding(horizontal = 25.dp)
        ) {
            MyTextField(
                textFieldState = searchByNameState.value,
                hint = "Search by name",
                leadingIcon = Icons.Outlined.Person,
                keyboardType = KeyboardType.Text,
                onValueChange = { searchByNameState.value = it
                    viewModel._searchName.value = it.text },
                modifier = Modifier.fillMaxWidth()
            )
            MyTextField(
                textFieldState = searchByDescription.value,
                hint = "Search by description",
                leadingIcon = Icons.Outlined.Description,
                keyboardType = KeyboardType.Text,
                onValueChange = { searchByDescription.value = it
                    viewModel._searchDescription.value = it.text},
                modifier = Modifier.fillMaxWidth()
            )
            MyTextField(
                textFieldState = searchByCategory.value,
                hint = "Search by Category",
                leadingIcon = Icons.Outlined.Category,
                keyboardType = KeyboardType.Text,
                onValueChange = { searchByCategory.value = it
                    viewModel._searchCategory.value = it.text
                },
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.material.Button(
                onClick = {

                    viewModel.search(context) // Trigger the search when the button is clicked
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.5f)
                    .height(50.dp)
            ) {
                androidx.compose.material.Text("Search", fontSize = 18.sp, color = Color.White)
            }


            // Handle UI states (Loading, Error, Success)
            when (val state = searchState) {
                is SearchViewModel.SearchScreenState.Loading -> {
                    CircularProgressIndicator() // Show loading indicator
                }

                is SearchViewModel.SearchScreenState.Error -> {
                    Text("Error: ${state.errorMessage}") // Show error message
                }

                is SearchViewModel.SearchScreenState.Success -> {
                    // Display the products
                    // Display the products
                    // Display the products
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),  // Adjust the number of columns as per your design
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp) // Add padding around the grid
                    ) {
                        itemsIndexed(state.responseData) { index, product ->  // Use itemsIndexed to get both the index and the item
                            ProductItem(product = product, navController = navController)      // Display the product item
                        }
                    }


//                LazyColumn {
//                    items(state.responseData) { product ->
//                        Text(product.name) // Example of displaying product name
//                    }
                }
            }

        }
        }

    }



@Composable
fun ProductItem(product: Product, navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        // Product Image (Main Image)


        Column(
            modifier = Modifier.padding(horizontal = 8.dp).clickable {
                navController.navigate("productDetail/${product.id}")
            }
        ) {
            Image(
                painter = rememberImagePainter(BaseUris.IMAGE_BASE_URL +product.image),
                contentDescription = product.name,
            modifier = Modifier.size(200.dp)
            )
            Text(
                text = product.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\$${product.price}",
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    color = Color.Green
                )
            }
        }
    }
}
