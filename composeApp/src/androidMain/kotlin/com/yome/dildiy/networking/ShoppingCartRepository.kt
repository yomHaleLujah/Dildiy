package com.yome.dildiy.networking

import io.ktor.client.request.setBody import android.content.Context
import android.util.Log
import com.yome.dildiy.model.Cart
import com.yome.dildiy.model.CartItem
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.ui.ecommerce.ProductScreen.ProductDetailScreen.CartItemRequest
import com.yome.dildiy.util.PreferencesHelper
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.delete
import io.ktor.client.request.put
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.ContentType
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow

class ShoppingCartRepository(private val httpClient: HttpClient) {

    suspend fun getCart(context : Context): Flow<NetWorkResult<Cart>> {
        val username = PreferencesHelper.getUsername(context)
        Log.d("ShoppingCartRepository.getCartItem()", "Hit Api 10.0.2.2:8081/api/cart/user/$username with")

        return toResultFlow {
            val response = httpClient.get {
                headers.append(HttpHeaders.Authorization, ""+getJwtToken(context))

                url {
                    // Use the appropriate URL for your Android emulator (10.0.2.2 refers to the host machine)
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2" // Host is the IP address for localhost from the Android emulator
                    port = 8081 // The port where your backend server is running
                    path("/api/cart/user/$username") // Adjust to the correct endpoint for fetching cart items
                }
            }.body<Cart>()
            Log.d("ShoppingCartRepository.getCartItem() response", "Hit Api 10.0.2.2:8081/api/cart/user/$username with $response")

            // Wrap the response into a successful result
            NetWorkResult.Success(response)
        }
    }

    // Function to add an item to the cart
    suspend fun addCartItem(username: String, context: Context, cartItemRequest: CartItemRequest?): Flow<NetWorkResult<CartItem>> {
        return toResultFlow {
            val response = httpClient.post {
                headers.append(HttpHeaders.Authorization, "" + getJwtToken(context))
                contentType(ContentType.Application.Json)
                Log.d("ShoppingCartRepository", "Hit Api 10.0.2.2:8081/api/cart/user/$username with $cartItemRequest")

                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2"
                    port = 8081
                    path("/api/cart/user/$username/update") // Adjust to the correct endpoint for fetching cart items
                }

                setBody(cartItemRequest) // Send the CartItem in the body of the request
            }.body<CartItem>()

            NetWorkResult.Success(response)
        }
    }

    // Function to update a cart item
    suspend fun updateCartItem(): Flow<NetWorkResult<CartItem>> {
        return toResultFlow {
            val response = httpClient.put {
//                headers.append(HttpHeaders.Authorization, "" + getJwtToken(context))
                contentType(ContentType.Application.Json)

                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2"
                    port = 8081
//                    path("cart/${cartItem.id}") // Assuming the ID is needed for updating
                }

//                setBody(cartItem) // Send the updated CartItem in the body of the request
            }.body<CartItem>()

            NetWorkResult.Success(response)
        }
    }

    // Function to delete a cart item
    suspend fun deleteCartItem(productId: String, context: Context): Flow<NetWorkResult<List<CartItemDTO>>> {
        val username = PreferencesHelper.getUsername(context)
        return toResultFlow {
            val response = httpClient.delete {
                headers.append(HttpHeaders.Authorization, "" + getJwtToken(context))

                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2"
                    port = 8081
                    path("/api/cart/user/$username/delete/$productId") // Endpoint for deleting a specific cart item
                }

                // Wrap the response to indicate success
            }.body<List<CartItemDTO>>()

            NetWorkResult.Success(response)
        }
    }
}
