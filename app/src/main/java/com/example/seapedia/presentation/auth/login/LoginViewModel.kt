package com.example.seapedia.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seapedia.core.utils.Resource
import com.example.seapedia.domain.GoogleSignInUseCase
import com.example.seapedia.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToRole: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        val validationError = validate(email, password)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = loginUseCase(email.trim(), password)) {
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

    fun googleSignIn(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = googleSignInUseCase(idToken)) {
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

    private fun validate(email: String, password: String): String? {
        if (email.isBlank()) return "Email tidak boleh kosong"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return "Format email tidak valid"
        if (password.isBlank()) return "Password tidak boleh kosong"
        if (password.length < 6) return "Password minimal 6 karakter"
        return null
    }
}
