    package com.yome.dildiy.ui.ecommerce.ProductScreen

    import android.content.Context
    import androidx.lifecycle.viewModelScope
    import com.yome.dildiy.networking.ProductRepository
    import com.yome.dildiy.remote.dto.Product
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch


    class ProductViewModel(private val networkRepository: ProductRepository) :
        androidx.lifecycle.ViewModel() {

        private val _productState = MutableStateFlow(ProductState())
        private val _productViewState: MutableStateFlow<ProductScreenState> =
            MutableStateFlow(ProductScreenState.Loading)
        val productViewState = _productViewState.asStateFlow()

        // Use viewModelScope to launch the coroutine
        suspend fun getProducts(context : Context) {
            viewModelScope.launch {
                try {
                    networkRepository.getProducts(context = context).collect { response ->
                        when (response.status) {
                            com.yome.dildiy.networking.ApiStatus.LOADING -> {
                                _productState.update { it.copy(isLoading = true) }
                            }
                            com.yome.dildiy.networking.ApiStatus.SUCCESS -> {
                                _productState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = "",
                                        responseData = response.data ?: emptyList()
                                    )
                                }
                            }
                            com.yome.dildiy.networking.ApiStatus.ERROR -> {
                                _productState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = response.message
                                    )
                                }
                            }
                        }
                        _productViewState.value = _productState.value.toUiState()  // Update the UI state
                    }
                } catch (e: Exception) {
                    _productState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to fetch data"
                        )
                    }
                }
            }
        }

        // State representing the UI
        sealed class ProductScreenState {
            object Loading : ProductScreenState()
            data class Error(val errorMessage: String) : ProductScreenState()
            data class Success(val responseData: List<Product>) : ProductScreenState()
        }

        // Internal state representing loading, data, and error
        private data class ProductState(
            val isLoading: Boolean = false,
            val errorMessage: String? = null,
            val responseData: List<Product> = emptyList()
        ) {
            fun toUiState(): ProductScreenState {
                return when {
                    isLoading -> ProductScreenState.Loading
                    errorMessage?.isNotEmpty() == true -> ProductScreenState.Error(errorMessage)
                    else -> ProductScreenState.Success(responseData)
                }
            }
        }
    }


/*
class EcommerceViewModel: ViewModel(), KoinComponent {


    private val productClient: DildiyClient by inject()  // Inject DildiyClient using Koin

    private val _productState = mutableStateOf<ProductState>(ProductState.Idle)
    val productState: State<ProductState> = _productState

    fun fetchProduct(dildiyClient: DildiyClient) {
        println("hiiii")
        _productState.value = ProductState.Loading // Set the state to loading while fetching

        viewModelScope.launch {
            when (val result = dildiyClient.fetchProducts()) {
                is com.yome.dildiy.util.Result.Success<*> -> {
                    // Assuming that the result.data is a List<Product>
                    val productList = result.data as? List<Product>
                    if (productList != null) {
                        // Successfully fetched products, update the UI state
                        _productState.value = ProductState.Success(productList)
                        println("hi successs"+ productList)
                        println(_productState.value)
                    } else {
                        _productState.value = ProductState.Error("Failed to parse products.")
                    }
                }

                is com.yome.dildiy.util.Result.Error<*> -> {
                    // Handle any errors from the API call
                    println("hi error: ${result.error}")
                    _productState.value = ProductState.Error(result.error.toString() ?: "Unknown error occurred")
                }
            }
        }
    }

    // Sealed class to represent different states of products
    sealed class ProductState {
        object Idle : ProductState()
        object Loading : ProductState()
        data class Success(val products: List<Product>) : ProductState()  // Store products here
        data class Error(val error: String) : ProductState()
    }
}
*/
