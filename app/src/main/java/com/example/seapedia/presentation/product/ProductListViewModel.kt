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
                val response = apiService.getProducts()
                if (response.isSuccessful) {
                    response.body()?.let { productResponse ->
                        if (productResponse.success) {
                            _uiState.update { it.copy(products = productResponse.data, error = null) }
                        } else {
                            _uiState.update { it.copy(error = "Failed to load products") }
                        }
                    } ?: run {
                        _uiState.update { it.copy(error = "Invalid response from server") }
                    }
                } else {
                    _uiState.update { it.copy(error = "Server error: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Network error: ${e.message}") }
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