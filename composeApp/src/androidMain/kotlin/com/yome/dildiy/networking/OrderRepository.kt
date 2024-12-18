package com.yome.dildiy.networking

import android.content.Context
import com.yome.dildiy.model.Orders
import com.yome.dildiy.util.PreferencesHelper
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class OrderRepository(private val httpClient: HttpClient) {

    // Place an order
    suspend fun placeOrder(order: Orders, context: Context): Flow<NetWorkResult<Orders>> {
        return toResultFlow {
            val orderWithNullIds = order.copy(
                id = null,  // Set the order ID to null
                items = order.items.map { it.copy(id = null) },  // Set item IDs to null
                cartItems = order.cartItems.map { it.copy(id = null) }  // Set cart item IDs and product IDs to null
            )
            println("Placing order: $orderWithNullIds")

            val response = httpClient.post {
                headers.append(HttpHeaders.Authorization, "Bearer ${getJwtToken(context)}")
                contentType(ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2" // Replace with your backend server's IP or domain
                    port = 8081
                    path("/api/orders")
                }
                setBody(orderWithNullIds) // Send the order as the request body
            }.body<Orders>()

            println("Server response: $response")
            NetWorkResult.Success(response)
        }
    }

    // Fetch all orders
    suspend fun getOrders(context: Context): Flow<NetWorkResult<List<Orders>>> {
        return toResultFlow {
            println("Fetching orders...")
            val userId = PreferencesHelper.getUsername(context)
            val response = httpClient.get {
                headers.append(HttpHeaders.Authorization, "Bearer ${getJwtToken(context)}")

                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2"
                    port = 8081
                    path("api/orders/user/$userId")
                }
            }.body<List<Orders>>()

            println("Server response: $response")
            NetWorkResult.Success(response)
        }
    }

    // Fetch details for a specific order
    suspend fun getOrderDetails(orderId: String, context: Context): Flow<NetWorkResult<Orders>> {
        return toResultFlow {
            println("Fetching details for order ID: $orderId")

            val response = httpClient.get {
                headers.append(HttpHeaders.Authorization, "Bearer ${getJwtToken(context)}")

                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2"
                    port = 8081
                    path("orders/$orderId") // Dynamic path for the specific order ID
                }
            }.body<Orders>()

            println("Server response: $response")
            NetWorkResult.Success(response)
        }
    }

    suspend fun initializePayment(
        order: Orders,
        context: Context
    ): String {
        val chapaUrl = "https://api.chapa.co/v1/transaction/initialize"
        val jwtToken = getJwtToken(context)

        val payload = mapOf(
            "amount" to order.totalPrice.toString(),
            "currency" to "ETB",
            "email" to order.email,
            "first_name" to order.fullName,
            "last_name" to order.fullName,
            "phone_number" to order.phone,
            "tx_ref" to "order-${System.currentTimeMillis()}",
            "callback_url" to "https://your-backend.com/payment-callback",
            "return_url" to "https://your-frontend.com/payment-complete",
            "customization" to mapOf(
                "title" to "Order Payment",
                "description" to "Payment for your recent order"
            )
        )

        val response: HttpResponse = httpClient.post(chapaUrl) {
            headers {
                append(HttpHeaders.Authorization, "Bearer CHASECK-xxxxxxxxxxxxxxxx")
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(payload)
        }

        val responseBody: String = response.bodyAsText()
        val responseData = Json.decodeFromString<Map<String, Any>>(responseBody)

        if (responseData["status"] == "success") {
            return responseData["data"].let { it as Map<String, String> }["checkout_url"]
                ?: throw Exception("Checkout URL not found!")
        } else {
            throw Exception("Payment initialization failed: ${responseData["message"]}")
        }
    }


    suspend fun verifyTransaction(transactionRef: String, context: Context): Boolean {
        val chapaUrl = "https://api.chapa.co/v1/transaction/verify/$transactionRef"
        val jwtToken = getJwtToken(context)

        val response: HttpResponse = httpClient.get(chapaUrl) {
            headers {
                append(HttpHeaders.Authorization, "Bearer CHASECK-xxxxxxxxxxxxxxxx")
            }
        }

        val responseBody: String = response.bodyAsText()
        val responseData = Json.decodeFromString<Map<String, Any>>(responseBody)

        return responseData["status"] == "success" && (responseData["data"] as Map<String, Any>)["status"] == "success"
    }

}