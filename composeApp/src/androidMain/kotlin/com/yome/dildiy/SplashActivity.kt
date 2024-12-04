package com.yome.dildiy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.delay

class SplashActivity: ComponentActivity() {

    override fun onCreate(savedINstanceStace: Bundle?){
        super.onCreate(savedINstanceStace)

        setContent{
            MaterialTheme{
                SplashScreen()
            }
        }
    }


}
@Preview
@Composable
private fun SplashScreen() {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        delay(2000)  // Delay for 2 seconds
        context.startActivity(Intent(context, MainActivity::class.java))
    }

    Box(modifier = Modifier.fillMaxSize().background(
        color = Color.Red
    ), contentAlignment =  Alignment.Center ){
        Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null)
    }
}