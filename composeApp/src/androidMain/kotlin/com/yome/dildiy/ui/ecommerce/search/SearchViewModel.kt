package com.yome.dildiy.ui.ecommerce.search


import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.SearchRepository
import com.yome.dildiy.remote.dto.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SearchViewModel(private val networkRepository: SearchRepository) :
    androidx.lifecycle.ViewModel() {

    // Search criteria
    // Search criteria as StateFlow to trigger UI updates
    val _searchName = MutableStateFlow("")
    val _searchDescription = MutableStateFlow("")
    val _searchCategory = MutableStateFlow("")

    var searchName = _searchName.asStateFlow()
    val searchDescription = _searchDescription.asStateFlow()
    val searchCategory = _searchCategory.asStateFlow()

    private val _productState = MutableStateFlow(SearchState())
    private val _searchViewState: MutableStateFlow<SearchScreenState> =
        MutableStateFlow(SearchScreenState.Loading)
    val searchViewState = _searchViewState.asStateFlow()
    // Use viewModelScope to launch the coroutine
    fun search(context: Context) {
        viewModelScope.launch {
            try {
                networkRepository.search(searchName = _searchName.value, searchDescription = _searchDescription.value, searchCategory = _searchCategory.value, context = context).collect { response ->
                    when (response.status) {
                        ApiStatus.LOADING -> {
                            _productState.update { it.copy(isLoading = true) }
                        }
                        ApiStatus.SUCCESS -> {
                            _productState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "",
                                    responseData = response.data ?: emptyList()
                                )
                            }
                        }
                        ApiStatus.ERROR -> {
                            _productState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = response.message
                                )
                            }
                        }
                    }
                    _searchViewState.value = _productState.value.toUiState()  // Update the UI state
                }
            } catch (e: Exception) {
                _productState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to fetch data"
                    )
                }
                _searchViewState.value = _productState.value.toUiState()  // Update UI on error
            }
        }
    }

    // State representing the UI
    sealed class SearchScreenState {
        object Loading : SearchScreenState()
        data class Error(val errorMessage: String) : SearchScreenState()
        data class Success(val responseData: List<Product>) : SearchScreenState()
    }

    // Internal state representing loading, data, and error
    private data class SearchState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val responseData: List<Product> = emptyList()
    ) {
        fun toUiState(): SearchScreenState {
            return when {
                isLoading -> SearchScreenState.Loading
                errorMessage?.isNotEmpty() == true -> SearchScreenState.Error(errorMessage)
                else -> SearchScreenState.Success(responseData)
            }
        }
    }
}



