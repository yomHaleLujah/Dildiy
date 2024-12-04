//package com.yome.dildiy.networking
//
//// In shared code, e.g., `shared/src/commonMain/kotlin`
//
//import com.yome.dildiy.remote.dto.Product
//import io.ktor.client.*
//import io.ktor.client.request.*
//import io.ktor.client.engine.cio.*
//import io.ktor.http.*
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.decodeFromString
//
//
//// Ktor client to fetch products
//suspend fun fetchProducts(): List<Product> {
//    val client = HttpClient(CIO) {
//        // Configure your client if needed
//    }
//
//    return try {
//        // Call the API to get the list of products
//        val response: String = client.get("http://10.0.2.2:8080/products").toString()
//
//        // Deserialize JSON response to List<Product>
//        Json.decodeFromString(response)
//    } catch (e: Exception) {
//        emptyList() // Handle error gracefully (return empty list in case of failure)
//    } finally {
//        client.close()
//    }
//}
