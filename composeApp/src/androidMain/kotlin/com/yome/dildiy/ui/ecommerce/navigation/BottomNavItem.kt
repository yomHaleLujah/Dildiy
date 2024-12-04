package com.yome.dildiy.ui.ecommerce.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.filled.Add  // This imports the Add icon
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Contextual


sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object Upload : BottomNavItem("upload", Icons.Default.PlayArrow, "Uplaod")
    object Cart : BottomNavItem("cart", Icons.Default.Search, "Cart")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")

}


@kotlinx.serialization.Serializable
object HomeScreens

@kotlinx.serialization.Serializable
object SearchScreens

@kotlinx.serialization.Serializable
object UploadScreens

@kotlinx.serialization.Serializable
object CartScreens

@kotlinx.serialization.Serializable
object ProfileScreens


@Serializable
sealed class BottomScreens<T>(val name: String, @Contextual val icon: ImageVector, val route: String) {
    @Serializable
    data object Home : BottomScreens<HomeScreens>(name = "Home", icon = Icons.Filled.Home, route = "home")

    @Serializable
    data object Search : BottomScreens<SearchScreens>(name = "Search", icon = Icons.Filled.Search, route = "search")

    @Serializable
    data object Upload : BottomScreens<UploadScreens>(name = "Upload", icon = Icons.Filled.Add, route = "upload")
    @Serializable
    data object Cart : BottomScreens<CartScreens>(name = "Cart", icon = Icons.Filled.ShoppingCart, route = "cart")

    @Serializable
    data object Profile : BottomScreens<ProfileScreens>(name = "Profile", icon = Icons.Filled.Person, route = "profile")
}

@Composable
fun AppBottomNavigation(navController: NavController) {
    val bottomScreens = remember {
        listOf(
            BottomScreens.Home,
            BottomScreens.Search,
            BottomScreens.Upload,
            BottomScreens.Cart,
            BottomScreens.Profile
        )
    }

    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.background) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomScreens.forEach { screen ->
            val isSelected = currentDestination?.route?.lowercase() == screen.route.toLowerCase()
            BottomNavigationItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector =screen.icon,
                        contentDescription = screen.name,
                        modifier = Modifier.then(if (isSelected) Modifier.size(40.dp) else Modifier.size(27.dp)) // Increase size when selected

                    )
                },
                label = {
                    Text(
                        text = screen.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}