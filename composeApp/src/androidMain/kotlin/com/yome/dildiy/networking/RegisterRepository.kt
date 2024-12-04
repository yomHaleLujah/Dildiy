package com.yome.dildiy.networking

import com.yome.dildiy.remote.dto.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow

class RegisterRepository(private val httpClient: HttpClient) {
    suspend fun register(user: User): Flow<NetWorkResult<String>> {
        return toResultFlow {

            val response =   httpClient.post("http://10.0.2.2:8080/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(user) // Ktor automatically serializes the User object to JSON
            }.body<String>()
            NetWorkResult.Success(response)
        }

    }
}