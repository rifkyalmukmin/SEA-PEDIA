package com.example.seapedia.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seapedia.core.network.ApiClient
import com.example.seapedia.core.network.ApiService
import com.example.seapedia.domain.Product
import com.example.seapedia.domain.ProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for ProductListScreen that fetches data from API
 */
class ProductListViewModel : ViewModel() {

    private val apiService: ApiService by lazy {
        ApiClient.getApiService()
    }

    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val response = apiService.getProducts().execute()

                if (response.isSuccessful) {
                    response.body()?.let { productResponse ->
                        if (productResponse.success) {
                            _uiState.update {
                                it.copy(
                                    products = productResponse.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Failed to load products: Unknown error"
                                )
                            }
                        }
                    } ?: run {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Invalid response from server"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Server error: ${response.code()} ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Network error: ${e.localizedMessage ?: e.toString()}"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadProducts()
    }

    data class UiState(
        val products: List<Product> = emptyList(),
        val error: String? = null,
        val isLoading: Boolean = false
    ) {
        companion object {
            val Loading = UiState(isLoading = true)
        }
    }
}