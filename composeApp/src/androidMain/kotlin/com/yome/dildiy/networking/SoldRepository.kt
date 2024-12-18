import android.content.Context
import com.yome.dildiy.Product
import com.yome.dildiy.model.OrderItem
import com.yome.dildiy.networking.CartItemDTO
import com.yome.dildiy.networking.NetWorkResult
import com.yome.dildiy.networking.toResultFlow
import com.yome.dildiy.util.PreferencesHelper
import com.yome.dildiy.util.PreferencesHelper.getJwtToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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

class SoldRepository(private val httpClient: HttpClient) {

    // Fetch sold items for a specific user (can be customized as needed)
    suspend fun getSoldItems(context: Context): Flow<NetWorkResult<List<Pair<CartItemDTO, Product>>>> {
        return toResultFlow {
            println("Fetching sold items...")

            // Retrieve user details (e.g., userId)
            val userId = PreferencesHelper.getUsername(context)

            // Make the GET request to fetch sold items
            val response = httpClient.get {
                headers.append(HttpHeaders.Authorization, "Bearer ${getJwtToken(context)}")

                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2" // Replace with your actual backend host
                    port = 8081 // Your backend port
                    path("api/sold/user/$userId") // The endpoint to fetch sold items
                }
            }.body<List<Pair<CartItemDTO, Product>>>() // Assume SoldItem is the correct data class

            // Log and wrap the result in NetWorkResult.Success
            println("Server response: $response")
            NetWorkResult.Success(response)
        }
    }
}
