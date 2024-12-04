package com.yome.dildiy.networking

import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.remote.dto.User
import com.yome.dildiy.util.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

class LoginRepository(private val httpClient: HttpClient) {
    suspend fun login(user: User): Flow<NetWorkResult<UserResponse>> {
        return toResultFlow {

            val response =   httpClient.post("http://10.0.2.2:8080/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(user) // Ktor automatically serializes the User object to JSON
            }.body<UserResponse>()
            NetWorkResult.Success(response)
        }

    }
}

@Serializable
data class UserResponse(
    val user: User?,       // The User object
    val jwtToken: String  // The JWT token string
)