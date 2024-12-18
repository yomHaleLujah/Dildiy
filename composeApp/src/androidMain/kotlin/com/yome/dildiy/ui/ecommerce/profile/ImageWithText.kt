package com.yome.dildiy.ui.ecommerce.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.yome.dildiy.Product
import com.yome.dildiy.R
import com.yome.dildiy.remote.dto.User

data class ImageWithText(
    val image: ImageVector,
    val text: String
)

@Composable
fun TopBar(
    name : String,
    modifier: Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Top Bar",
            tint = Color.Black,
            modifier = Modifier.size(24.dp))
        Text(name,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black)
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Top Bar",
            tint = Color.Black,
            modifier = Modifier.size(24.dp))
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Top Bar",
            tint = Color.Black,
            modifier = Modifier.size(20.dp))

    }
}

@Composable
fun ProfileSection(
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            RoundImage(image = "https://i.pinimg.com/236x/db/1f/9a/db1f9a3eaca4758faae5f83947fa807c.jpg", modifier = Modifier
                .size(100.dp)
                .weight(3f)
            )

            Spacer(modifier = Modifier.width(16.dp))
            StatSection(modifier = Modifier.weight(7f))
        }
        Spacer(modifier = Modifier.width(10.dp))
        ProfileDescription(
            displayName = "Coding Beast",
            description = "10 years of coding experience \n" +
                    "Want me to make your app. Just email me \n" +
                    "Do subscribe to my youtube channel \n" +
                    "Link Below",
            url = "https://www.youtube.com/codingbeast",
            followedBy = listOf("Phillip","coding in flow"),
            otherCount = 17
        )

    }
}

@Composable
fun RoundImage(
    image: String,
    modifier: Modifier = Modifier
){
    Image(painter = rememberImagePainter("https://i.pinimg.com/236x/db/1f/9a/db1f9a3eaca4758faae5f83947fa807c.jpg"),
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                1.dp, Color.LightGray, CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
    )
}

@Composable
fun StatSection(modifier: Modifier = Modifier){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        ProfileStat(numberText = "601", text = "Posts")
        ProfileStat(numberText = "99.8K", text = "Followers")
        ProfileStat(numberText = "72", text = "Following")
    }
}

@Composable
fun ProfileStat(
    numberText: String,
    text: String,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = numberText,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
        )
    }
}

@Composable
fun ProfileDescription(
    displayName: String,
    description: String,
    url: String,
    followedBy: List<String>,
    otherCount: Int
){
    val letterSpacing = 0.5.sp
    val lineHeight = 20.sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = displayName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        Text(
            text = description,
            fontSize = 16.sp,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        Text(
            text = url,
            fontSize = 16.sp,
            color = Color(0xFF4D61CF),
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
    }
}

@Composable
fun PostTabView(
    modifier: Modifier = Modifier,
    imageWithTexts: List<ImageWithText>,
    onTabSelected: (selectedIndex: Int) -> Unit
) {
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    val inactiveColor = Color(0xFF777777)
    val activeColor = Color.Red // Set active color to red

    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = Color.Transparent, // Transparent background
        indicator = { /* No indicator to remove blue line */ },
        divider = {}, // No divider to avoid any default color
        modifier = modifier
    ) {
        imageWithTexts.forEachIndexed { index, item ->
            Tab(
                selected = selectedTabIndex == index,
                selectedContentColor = activeColor, // Change the selected content color to red
                unselectedContentColor = inactiveColor,
                onClick = {
                    selectedTabIndex = index
                    onTabSelected(index)
                }
            ) {
                Column {
                    Icon(
                        imageVector = item.image,
                        contentDescription = item.text,
                        tint = if (selectedTabIndex == index) activeColor else inactiveColor,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(20.dp)
                    )
                    Text(text = item.text)
                }

            }
        }
    }
}



@Composable
fun PostSection(
    products: List<Product>,
    post: List<Painter>,
    modifier: Modifier = Modifier
){
    LazyVerticalGrid(columns = GridCells.Fixed(3),
        modifier = modifier.scale(1.01f)
    ){
        items(post.size){
            Image(
                painter =post[it],
                contentDescription ="posts",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(
                        width = 1.dp,
                        color = Color.White,
                    )
            )
        }
    }
}


@Composable
fun ProfileScreen(user: User, products: List<Product>){
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(10.dp))
        TopBar(user.name)
        Spacer(modifier = Modifier.height(10.dp))
        ProfileSection()
        Spacer(modifier = Modifier.height(25.dp))
        PostTabView(
            imageWithTexts = listOf(
                ImageWithText(
                    image = Icons.Default.GridView,
                    text = "Posts"
                ),
                ImageWithText(
                    image =Icons.Default.LocationOn,
                    text = "Posts"
                ),
                ImageWithText(
                    image = Icons.Default.LocalGroceryStore,
                    text = "Posts"
                ),
                ImageWithText(
                    image = Icons.Default.ShoppingCart,
                    text = "Posts"
                )
            )
        ) {
            selectedTabIndex = it
        }

        when(selectedTabIndex){
            0 -> PostSection(
                products,
                post = listOf(
                    painterResource(id = R.drawable.image_1),
                    painterResource(id = R.drawable.login),
                    painterResource(id = R.drawable.register),
                    painterResource(id = R.drawable.image_1),
                    painterResource(id = R.drawable.login),
                    painterResource(id = R.drawable.register) ,
                    painterResource(id = R.drawable.image_1),
                    painterResource(id = R.drawable.login),
                    painterResource(id = R.drawable.register),
                    painterResource(id = R.drawable.image_1),
                    painterResource(id = R.drawable.login),
                    painterResource(id = R.drawable.register)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}