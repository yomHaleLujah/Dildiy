package com.yome.dildiy.ui.ecommerce.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.yome.dildiy.ui.ecommerce.profile.ProductItem
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(bottom = 50.dp) // Add padding for smoother scrolling
    ) {
        // Header Section
        item {
            com.yome.dildiy.util.LottieAnimationView(R.raw.search2)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                MyTextField(
                    textFieldState = searchByNameState.value,
                    hint = "Search by name",
                    leadingIcon = Icons.Outlined.Person,
                    keyboardType = KeyboardType.Text,
                    onValueChange = {
                        searchByNameState.value = it
                        viewModel._searchName.value = it.text
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                MyTextField(
                    textFieldState = searchByDescription.value,
                    hint = "Search by description",
                    leadingIcon = Icons.Outlined.Description,
                    keyboardType = KeyboardType.Text,
                    onValueChange = {
                        searchByDescription.value = it
                        viewModel._searchDescription.value = it.text
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                MyTextField(
                    textFieldState = searchByCategory.value,
                    hint = "Search by Category",
                    leadingIcon = Icons.Outlined.Category,
                    keyboardType = KeyboardType.Text,
                    onValueChange = {
                        searchByCategory.value = it
                        viewModel._searchCategory.value = it.text
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                androidx.compose.material.Button(
                    onClick = {
                        viewModel.search(context) // Trigger the search
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
            }
        }

        // Content Section
        when (val state = searchState) {
            is SearchViewModel.SearchScreenState.Loading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            is SearchViewModel.SearchScreenState.Error -> {
                item {
                    Text(
                        text = "Error: ${state.errorMessage}",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            is SearchViewModel.SearchScreenState.Success -> {
                items(state.responseData.chunked(2)) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowProducts.forEach { product ->
                            Box(
                                modifier = Modifier
                                    .weight(1f) // Equal weight to fill available space
                                    .padding(horizontal = 8.dp)
                            ) {
                                ProductItem(
                                    product = product,
                                    navController = navController
                                )
                            }
                        }

                        // Add empty space if there's only one product in the row
                        if (rowProducts.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

    }
}




@Composable
fun ProductItem(product: Product, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("productDetail/${product.id}") },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Image
        Image(
            painter = rememberImagePainter(BaseUris.IMAGE_BASE_URL + product.image),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Ensures square images
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Product Name
        Text(
            text = product.name,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp
            ),
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Product Price
        Text(
            text = "\$${product.price}",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = Color.Green
        )
    }
}
