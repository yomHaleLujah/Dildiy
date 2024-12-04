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

class SearchRepository(private val httpClient: HttpClient) {
    suspend fun search(searchName: String, searchDescription: String, searchCategory: String, context: Context): Flow<NetWorkResult<List<Product>>> {
        return toResultFlow {
            println("I am in getProducts")

            val response = httpClient.get {
                headers.append(HttpHeaders.Authorization, ""+getJwtToken(context))

                url {
                    // Use the appropriate URL for your Android emulator (10.0.2.2 refers to the host machine)
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2" // Host is the IP address for localhost from the Android emulator
                    port = 8081 // The port where your backend server is running
                    path("products/search") // The endpoint path (e.g., "/products")
                    // Conditionally add query parameters if they are not null
                    // Append parameters only if they are not null
                    println(searchName +"search name "+searchDescription+"searchdescription")
                    searchName?.let { parameters.append("name", it) }
                    searchDescription?.let { parameters.append("description", it) }
                    searchCategory?.let { parameters.append("category", it) }                }
            }.body<List<Product>>()

            println("   server response..." + response)

            // Wrap the response into a successful result
            NetWorkResult.Success(response)
        }
    }
}

