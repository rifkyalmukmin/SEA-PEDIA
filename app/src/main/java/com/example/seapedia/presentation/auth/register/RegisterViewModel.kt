package com.example.seapedia.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seapedia.core.utils.Constants
import com.example.seapedia.core.utils.Resource
import com.example.seapedia.domain.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToRole: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String, role: String) {
        val validationError = validate(name, email, password, role)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = registerUseCase(name.trim(), email.trim(), password, role)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isLoading = false, navigateToRole = result.data.role)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigateToRole = null) }
    }

    private fun validate(name: String, email: String, password: String, role: String): String? {
        if (name.isBlank()) return "Nama lengkap tidak boleh kosong"
        if (name.trim().length < 2) return "Nama terlalu pendek"
        if (email.isBlank()) return "Email tidak boleh kosong"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return "Format email tidak valid"
        if (password.isBlank()) return "Password tidak boleh kosong"
        if (password.length < 6) return "Password minimal 6 karakter"
        val validRoles = listOf(Constants.ROLE_BUYER, Constants.ROLE_SELLER, Constants.ROLE_DRIVER)
        if (role !in validRoles) return "Pilih peran akun terlebih dahulu"
        return null
    }
}
