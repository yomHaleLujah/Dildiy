package com.yome.dildiy.ui.mekina

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.yome.dildiy.R
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun PickupLocationScreen(
    onLocationSelected: (GeoPoint) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val selectedLocation = remember { mutableStateOf<GeoPoint?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = {
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(17.0)

                    // Center map to a default location (e.g., Addis Ababa)
                    val initialCenter = GeoPoint(9.0370, 38.7510)
                    controller.setCenter(initialCenter)

                    // Handle map taps
                    overlays.add(
                        MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p?.let {
                                    selectedLocation.value = it
                                    // Clear previous markers
                                    overlays.removeIf { overlay -> overlay is Marker }

                                    // Add a new marker
                                    val marker = Marker(this@apply).apply {
                                        position = it
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        title = "Pickup Location"
                                        icon = ContextCompat.getDrawable(
                                            context,
                                            R.drawable.marker50 // Your custom marker drawable
                                        )
                                    }
                                    overlays.add(marker)
                                    invalidate()
                                }
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint?): Boolean = false
                        })
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Show a confirm button after location selection
        selectedLocation.value?.let {
            Button(
                onClick = {
                    onLocationSelected(it) // Pass selected location to the caller
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Confirm Pickup Location")
            }
        }
    }
}
