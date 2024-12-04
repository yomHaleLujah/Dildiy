package com.yome.dildiy.networking


import android.content.Context
import com.yome.dildiy.remote.dto.Payload
import com.yome.dildiy.model.Orders
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

class PaymentRepository(private val httpClient: HttpClient) {

    suspend fun initiatePayment(order: Orders, context: Context, paymentRequest: Payload): Flow<NetWorkResult<PaymentResponse>> {
        return toResultFlow {
           try {
               println("Initiating payment for order: $order")
               // Create the PaymentRequest object

               val response = httpClient.post {
                   headers.append(
                       HttpHeaders.ContentType,
                       "application/json")
                   headers.append(
                       HttpHeaders.Authorization,
                       "Bearer CHASECK_TEST-UdaZQORRKU8g6PKazgV0av8XXIeP0gMQ"
                   )

                   url {
                       protocol = URLProtocol.HTTPS
                       host =
                           "api.chapa.co" // Use the appropriate IP or domain for your backend
//                    port = 8081 // Port where the backend server is running
                       path("v1/transaction/initialize") // Your payment initialization API endpoint
                   }

                   println("Payment Url " + url.toString())
                   setBody(paymentRequest) // Sending the order data as the request body
               }.body<PaymentResponse>()

               // Wrap the response in a successful result
               NetWorkResult.Success(response)
           }
           catch (e: Exception) {
               // Catch any exceptions that occur during the payment request
               println("Error initiating payment: ${e.message}")
               // Handle the error and return a failed result
               NetWorkResult.Error<PaymentResponse>(_data = null, exception = "Network error: ${e.localizedMessage}")
           }
        }
    }

    suspend fun initiatePayment2(order: Orders, context: Context, paymentRequest: Payload): Flow<NetWorkResult<PaymentResponse>> {
        return toResultFlow {
            val response = httpClient.post {
                headers.append(HttpHeaders.Authorization, "Bearer CHASECK_TEST-UdaZQORRKU8g6PKazgV0av8XXIeP0gMQ" )
                contentType(ContentType.Application.Json)

                url {
                    protocol = URLProtocol.HTTPS
                    host =
                        "api.chapa.co" // Use the appropriate IP or domain for your backend
//                    port = 8081 // Port where the backend server is running
                    path("v1/transaction/initialize") // Your payment initialization API endpoint
                }

                setBody(paymentRequest) // Send the CartItem in the body of the request
            }.body<PaymentResponse>()

            NetWorkResult.Success(response)
        }
    }

    suspend fun verifyPayment(tx_rf: String): Flow<NetWorkResult<com.yome.dildiy.Dao.PaymentResponse>> {
        return toResultFlow {
            try { println("I am in getProducts + $tx_rf")


            val response = httpClient.get {
                headers.append(HttpHeaders.Authorization, "Bearer CHASECK_TEST-UdaZQORRKU8g6PKazgV0av8XXIeP0gMQ" )

                url {
                    // Use the appropriate URL for your Android emulator (10.0.2.2 refers to the host machine)
                    protocol = URLProtocol.HTTPS
                    host = "api.chapa.co" // Host is the IP address for localhost from the Android emulator
                    path("v1/transaction/verify/"+tx_rf) // The endpoint path (e.g., "/products")
                                   }
            }.body<com.yome.dildiy.Dao.PaymentResponse>()

            println("   server response..." + response)

            // Wrap the response into a successful result
            NetWorkResult.Success(response)

        }
        catch (e: Exception) {
            // Catch any exceptions that occur during the payment request
            println("Error initiating payment: ${e.message}")
            // Handle the error and return a failed result
            NetWorkResult.Error<com.yome.dildiy.Dao.PaymentResponse>(_data = null, exception = "Network error: ${e.localizedMessage}")
        }
    }
    }
}


@Serializable
data class PaymentResponse(
    val message: String,
    val status: String,
    val data: PaymentData
)

@Serializable
data class PaymentData(
    val checkout_url: String
)


