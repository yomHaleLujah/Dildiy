package com.yome.dildiy.networking

import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.util.Result
import com.yome.dildiy.remote.dto.User
import io.ktor.client.HttpClient
import com.yome.dildiy.util.NetworkError
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class DildiyClient(private val httpClient: HttpClient) {


    @OptIn(InternalAPI::class)
    suspend fun registerUser(user: User): com.yome.dildiy.util.Result<User, NetworkError> {

        val response =  try {
            httpClient.post("http://10.0.2.2:8080/api/users/register") {
                contentType(ContentType.Application.Json)
                setBody(user) // Ktor automatically serializes the User object to JSON
            }
        } catch(e: UnresolvedAddressException) {
            return com.yome.dildiy.util.Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            return com.yome.dildiy.util.Result.Error(NetworkError.SERIALIZATION)
        }
        return when(response.status.value) {
            in 200..299 -> {
                val user = response.body<User>()
                Result.Success(user)
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun loginUser(user: User): com.yome.dildiy.util.Result<User, NetworkError> {

        val response =  try {
            httpClient.post("http://10.0.2.2:8080/api/users/login") {
                contentType(ContentType.Application.Json)
                setBody(user) // Ktor automatically serializes the User object to JSON
            }
        } catch(e: UnresolvedAddressException) {
            return com.yome.dildiy.util.Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            return com.yome.dildiy.util.Result.Error(NetworkError.SERIALIZATION)
        }
        return when(response.status.value) {
            in 200..299 -> {
                val user = response.body<User>()
                Result.Success(user)
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    // Ktor client to fetch products
    suspend fun fetchProducts(): List<Product> {

        return try {
            // Call the API to get the list of products
            val response: String = httpClient.get("http://10.0.2.2:8081/products").toString()

            // Deserialize JSON response to List<Product>
            Json.decodeFromString(response)
        } catch (e: Exception) {
            emptyList() // Handle error gracefully (return empty list in case of failure)
        } finally {
            httpClient.close()
        }
    }


}