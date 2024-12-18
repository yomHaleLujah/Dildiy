package com.yome.dildiy.ui.ecommerce.orderScreen

import SoldRepository
import com.yome.dildiy.ui.ecommerce.checkout.DeliveryPriceResponse


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yome.dildiy.Product
import com.yome.dildiy.model.OrderItem
import com.yome.dildiy.networking.ApiStatus
import com.yome.dildiy.networking.CartItemDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SoldViewModel(private val soldRepository: SoldRepository) : ViewModel() {

    // Internal state that holds the SoldState
    private val _soldState = MutableStateFlow(SoldState())
    val soldState = _soldState.asStateFlow()

    private val _getSoldState = MutableStateFlow(GetSoldState())
    private val _getSoldViewState: MutableStateFlow<GetSoldScreenState> =
        MutableStateFlow(GetSoldScreenState.Loading)
    val getSoldViewState = _getSoldViewState.asStateFlow()

    fun getSoldItems(context: Context) {
        viewModelScope.launch {
            _getSoldState.update { it.copy(isLoading = true) }
            try {
                soldRepository.getSoldItems(context = context).collect { response ->
                    when (response.status) {
                        ApiStatus.LOADING -> {
                            _getSoldState.update { it.copy(isLoading = true) }
                        }

                        ApiStatus.SUCCESS -> {
                            val soldResponse = response.data

                            _getSoldState.update {
                                it.copy(
                                    isLoading = false,
                                    success = soldResponse,
                                    errorMessage = null
                                )
                            }

                            _getSoldViewState.value = GetSoldScreenState.Success(soldResponse)
                        }

                        ApiStatus.ERROR -> {
                            _getSoldState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = response.message ?: "Unknown error"
                                )
                            }
                            _getSoldViewState.value = GetSoldScreenState.Error(response.message ?: "Unknown error")
                        }
                    }
                }
            } catch (e: Exception) {
                _getSoldState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.localizedMessage}"
                    )
                }
                _getSoldViewState.value = GetSoldScreenState.Error("Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    // Data class representing the internal state of sold items process
    data class SoldState(
        val isLoading: Boolean = false,
        val success: List<Pair<CartItemDTO, Product>>? = null,
        val errorMessage: String? = null
    ) {
        // Convert SoldState to UI state (SoldUiState)
        fun toUiState(): SoldUiState {
            return SoldUiState(
                isLoading = isLoading,
                success = success,
                errorMessage = errorMessage,
                isSuccess = success != null && errorMessage == null
            )
        }
    }

    // Sealed class for different UI states (for display)
    data class GetSoldUiState(
        val isLoading: Boolean = false,
        val success: List<Pair<CartItemDTO, Product>>? = emptyList(),
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )

    sealed class GetSoldScreenState {
        object Loading : GetSoldScreenState()
        data class Error(val errorMessage: String) : GetSoldScreenState()
        data class Success(val responseData: List<Pair<CartItemDTO, Product>>?) : GetSoldScreenState()
    }

    data class GetSoldState(
        val isLoading: Boolean = false,
        val success: List<Pair<CartItemDTO, Product>>? = emptyList(),
        val errorMessage: String? = null
    ) {
        // Convert SoldState to UI state (SoldUiState)
        fun toUiState(): GetSoldUiState {
            return GetSoldUiState(
                isLoading = isLoading,
                success = success,
                errorMessage = errorMessage,
                isSuccess = success != null && errorMessage == null
            )
        }
    }

    // Sealed class for different UI states (for display)
    data class SoldUiState(
        val isLoading: Boolean = false,
        val success: List<Pair<CartItemDTO, Product>>? = null,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )

    sealed class SoldScreenState {
        object Loading : SoldScreenState()
        data class Error(val errorMessage: String) : SoldScreenState()
        data class Success(val responseData: List<Pair<CartItemDTO, Product>>?) : SoldScreenState()
    }
}
