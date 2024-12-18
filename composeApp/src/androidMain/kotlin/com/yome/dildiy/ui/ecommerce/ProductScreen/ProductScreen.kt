package com.yome.dildiy.ui.ecommerce.ProductScreen
import androidx.compose.animation.animateContentSize
import com.google.accompanist.pager.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
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

    ) {
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
                .padding (horizontal = 16.dp)
                .align(Alignment.BottomStart)// Optional padding for the entire screen
        ) {

            TextWithBorder(
                text = product.name ?: "Unknown User",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textColor = Color.Black,
                borderColor = Color.White,
                borderWidth = 6f // Adjust for thicker or thinner borders
            )

            val (expanded, onExpandedChange) = rememberSaveable { mutableStateOf(false) }
            ExpandingText(
                text = product.description
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
                        .padding(start = 0.dp)
                        .padding( 8.dp)
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
                        text = "Shop Now   ",
                        color = Color.White, // White text color
                        style = MaterialTheme.typography.bodyLarge
                    )
                    androidx.compose.material.Icon(
                        imageVector = Icons.Default.ArrowRightAlt,
                        contentDescription = "Map Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }

    }

}




@Composable
fun TextWithBorder(
    text: String,
    fontSize: TextUnit = 20.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    textColor: Color = Color.White,
    borderColor: Color = Color.Black,
    borderWidth: Float = 4f // Thickness of the border
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = textColor,
        style = TextStyle(
            shadow = Shadow(
                color = borderColor,
                offset = Offset(0f, 0f), // Centered shadow
                blurRadius = borderWidth // Adjust border thickness
            )
        )
    )
}

private const val MINIMIZED_MAX_LINES = 2

@Composable
fun ExpandingText(
    modifier: Modifier = Modifier,
    text: String,
    expandedMaxLines: Int = Int.MAX_VALUE,
    minimizedMaxLines: Int = MINIMIZED_MAX_LINES,
) {
    var isExpanded by remember { mutableStateOf(false) } // Track expanded/collapsed state
    var isTextTruncated by remember { mutableStateOf(false) } // Track if text is truncated

    // Text widget for displaying the content
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = if (isExpanded) expandedMaxLines else minimizedMaxLines,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { textLayoutResult ->
            // Check if the text is overflowing (truncated)
            isTextTruncated = textLayoutResult.hasVisualOverflow
        }
    )

    // Show Read More or Read Less based on the truncation and expanded state
    if (isTextTruncated || isExpanded) {
        Text(
            text = if (isExpanded) "Read Less" else "Read More", // Toggle between Read More and Read Less
            color = Color.Blue,
            modifier = Modifier
                .clickable {
                    // Toggle the expanded/collapsed state
                    isExpanded = !isExpanded
                }
                .padding(top = 4.dp) // Optional padding between the text and button
        )
    }
}
