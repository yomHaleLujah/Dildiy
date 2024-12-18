package com.yome.dildiy.ui.ecommerce.profile

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.remote.dto.User
import com.yome.dildiy.ui.ecommerce.navigation.AppBottomNavigation
import com.yome.dildiy.ui.ecommerce.orderScreen.OrderScreen
import com.yome.dildiy.ui.ecommerce.orderScreen.SoldScreen
import com.yome.dildiy.util.PreferencesHelper
import org.koin.androidx.compose.getKoin


@Composable
fun ProfileScreen2(
    navController: NavController,
    viewModel: ProfileVm = getKoin().get() // Inject ViewModel using Koin
) {
    val profileScreenState by viewModel.productViewState.collectAsState()
    val context = LocalContext.current
    val user = PreferencesHelper.getUsername2(context)
    UserProfile(navController)


}

@Composable
fun UserProfile(navController: NavController) {
    Column {
        // Header Section
//        ProfileSection(user = user)
        Spacer(modifier = Modifier.height(16.dp))

        // Tabs and Product List
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        PostTabView(
            imageWithTexts = listOf(
                ImageWithText(Icons.Default.GridView, "Posts"),
                ImageWithText(Icons.Default.ShoppingCartCheckout, "Orders"),
                ImageWithText(Icons.Default.GridView, "Sold"),
                ImageWithText(Icons.Default.GridView, "Returns"),
                ImageWithText(Icons.Default.LocationOn, "Shipping Progress"),
                ImageWithText(Icons.Default.LocalGroceryStore, "Store")
            )
        ) { selectedTabIndex = it }

        when (selectedTabIndex) {
            0 -> ProductList(navController = navController)
            1 -> OrderScreen(navController = navController)
            2 -> SoldScreen(navController = navController)

// Handle other tabs as needed
        }
    }
}

