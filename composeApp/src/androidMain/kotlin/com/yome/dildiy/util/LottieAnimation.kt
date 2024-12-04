package com.yome.dildiy.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.Resources
import com.yome.dildiy.R

@Composable
fun LottieAnimationView(resources: Int) {
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
            .padding(16.dp) // Add padding for spacing
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp) // Control size explicitly
        )
    }
}
