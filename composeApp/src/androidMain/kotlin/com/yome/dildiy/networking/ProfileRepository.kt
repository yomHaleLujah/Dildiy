package com.yome.dildiy.networking

import android.content.Context
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class ProfileRepository(private val httpClient: HttpClient) {
    suspend fun getAllProductsByUsername(username : String, context : Context): Flow<NetWorkResult<List<Product>>> {
        return toResultFlow {

            val response = httpClient.get {
                headers.append(HttpHeaders.Authorization, ""+ getJwtToken(context))

                url {
                    // Use the appropriate URL for your Android emulator (10.0.2.2 refers to the host machine)
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2" // Host is the IP address for localhost from the Android emulator
                    port = 8081 // The port where your backend server is running
                    path("/products/user/"+username) // The endpoint path (e.g., "/products")
                }
            }.body<List<Product>>()

            // Wrap the response into a successful result
            NetWorkResult.Success(response)
        }
    }
}

