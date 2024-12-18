package com.yome.dildiy.networking

import android.content.Context
import com.yome.dildiy.model.Orders
import com.yome.dildiy.remote.dto.Payload
import com.yome.dildiy.ui.ecommerce.checkout.DeliveryPriceResponse
import com.yome.dildiy.ui.ecommerce.checkout.DeliveryRequest
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val httpClient: HttpClient) {

    suspend fun calculateDeliveryPrice(
        deliveryRequest: DeliveryRequest, context: Context
    ): Flow<NetWorkResult<DeliveryPriceResponse>> {
        return toResultFlow {
            try {

                val response = httpClient.post {
                    headers.append(
                        HttpHeaders.ContentType,
                        "application/json")
                    headers.append(HttpHeaders.Authorization, "" + getJwtToken(context))

                    url {
                        // Use the appropriate URL for your Android emulator (10.0.2.2 refers to the host machine)
                        protocol = URLProtocol.HTTP
                        host = "10.0.2.2" // Host is the IP address for localhost from the Android emulator
                        port = 8081 // The port where your backend server is running
                        path("delivery/calculate") // Adjust to the correct endpoint for fetching cart items
                    }

                    println("Payment Url " + url.toString())
                    setBody(deliveryRequest) // Sending the order data as the request body
                }.body<DeliveryPriceResponse>()

                // Wrap the response in a successful result
                NetWorkResult.Success(response)
            } catch (e: Exception) {
                // Catch any exceptions that occur during the payment request
                println("Error initiating payment: ${e.message}")
                // Handle the error and return a failed result
                NetWorkResult.Error<DeliveryPriceResponse>(
                    _data = null,
                    exception = "Network error: ${e.localizedMessage}"
                )
            }
        }
    }

}