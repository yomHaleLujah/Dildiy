package com.yome.dildiy.networking
import android.content.Context
import android.net.Uri
import com.yome.dildiy.remote.dto.Product
import com.yome.dildiy.util.PreferencesHelper
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import java.io.InputStream

class UploadProductRepository(
    private val httpClient: HttpClient,
) {
    suspend fun uploadProduct(product: Product, imageUris: List<Uri>, context: Context): Flow<NetWorkResult<List<String>>> {
        return toResultFlow {
            val response = httpClient.post("http://10.0.2.2:8081/products") {
                headers.append(HttpHeaders.Authorization, ""+getJwtToken(context))

                contentType(ContentType.MultiPart.FormData)

                println("sending api request + "+ imageUris + product)
                setBody(MultiPartFormDataContent(formData {
                    // Append the product object as JSON string
                    append(
                        "product",
                        Json.encodeToString(Product.serializer(), product),
                        Headers.build {
                            append(HttpHeaders.ContentType, "application/json")
                        }
                    )

                    // Append images using URIs
                    imageUris.forEachIndexed { index, uri ->
                        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                        val fileBytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (fileBytes != null) {
                            append(
                                "images",
                                fileBytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/png") // Adjust content type if necessary
                                    append(HttpHeaders.ContentDisposition, "filename=\"image_$index.png\"")
                                }
                            )
                        }
                    }
                }))
            }.body<List<String>>()

            NetWorkResult.Success(response)
        }
    }
}


/*
import com.yome.dildiy.remote.dto.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow

class UploadProductRepository(private val httpClient: HttpClient) {
    suspend fun uploadProduct(product: Product): Flow<NetWorkResult<Product>> {
        return toResultFlow {

                 val response =  httpClient.post("http://10.0.2.2:8081/products") {
                     contentType(ContentType.MultiPart.FormData)


                        setBody(product)
                    }.body<Product>()
             NetWorkResult.Success(response)
            }
        }
    }*/
