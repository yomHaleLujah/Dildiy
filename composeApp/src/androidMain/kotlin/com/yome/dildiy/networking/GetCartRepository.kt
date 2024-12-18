package com.yome.dildiy.networking

import android.content.Context
import android.util.Log
import androidx.room.Entity
import androidx.room.ForeignKey
import com.yome.dildiy.model.Cart
import com.yome.dildiy.model.Orders
import com.yome.dildiy.util.PreferencesHelper
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import java.math.BigDecimal

class GetCartRepository(private val httpClient: HttpClient) {
    suspend fun getCart(context: Context): Flow<NetWorkResult<CartDTO>> {
        return toResultFlow<CartDTO> {
            try {
                val username = PreferencesHelper.getUsername(context)
                Log.d("GetCartRepository.getCartItem()", "Hit Api 10.0.2.2:8081/api/cart/user/$username with")

                // Perform the API call to get the cart details
                val response: CartDTO = httpClient.get {
                    println("Making request to server... $username")
                    headers.append(HttpHeaders.Authorization, "" + getJwtToken(context))

                    url {
                        // Use the appropriate URL for your Android emulator (10.0.2.2 refers to the host machine)
                        protocol = URLProtocol.HTTP
                        host = "10.0.2.2" // Host is the IP address for localhost from the Android emulator
                        port = 8081 // The port where your backend server is running
                        path("/api/cart/user/$username") // Adjust to the correct endpoint for fetching cart items
                    }
                }.body() // Directly mapping the response to CartDTO

                // Log the successful response
                Log.d("GetCartRepository", "Response: $response") // Print the entire response object

                // Return the successful response
                NetWorkResult.Success(response)
            } catch (e: Exception) {
                // Log the error
                Log.e("GetCartRepository", "Error occurred while fetching cart: ${e.message}", e)

                // Return an error result with the exception message
                NetWorkResult.Error(null, e.message ?: "Unknown error")
            }
        }
    }
}

@Serializable
data class CartItemDTO(
    val id: Long? = null,
    val product: ProductDTO,
    var quantity: Int,
    val username: String,
)

@Serializable
data class ProductDTO(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val image: String,
    val imageList: List<String>,
    val category: String,
    val userId: String
)

@Serializable
data class CartDTO(
    val id: Long,
    val cartItems: List<CartItemDTO>
)

fun <T> toResultFlow2(call: suspend () -> NetWorkResult<T?>): Flow<NetWorkResult<T>> {
    return flow {
        emit(NetWorkResult.Loading(true))  // Emit a loading state

        // Invoke the API call
        val response = call.invoke()

        // Check the response in the 'let' block
        response.let { result ->
            try {
                // Check if response.data is null or not
                if (result.data != null) {
                    // Emit success if data is not null
                    emit(NetWorkResult.Success(result.data))
                } else {
                    // Emit error if data is null (custom error message)
                    emit(NetWorkResult.Error(null, "Data is null"))
                }
            } catch (e: Exception) {
                // Catch any exception and emit the error state
                emit(NetWorkResult.Error(result.data, e.message ?: "Unknown error"))
            }
        }
    }
}
