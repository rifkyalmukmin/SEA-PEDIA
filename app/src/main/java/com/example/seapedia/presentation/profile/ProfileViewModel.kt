package com.example.seapedia.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seapedia.core.network.ApiService
import com.example.seapedia.core.utils.Constants
import com.example.seapedia.core.utils.Resource
import com.example.seapedia.domain.AuthRepository
import com.example.seapedia.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Profile screen
 * Manages user profile display and role switching
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * Load user profile from local storage (authenticated state)
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = authRepository.getAuthState().first()
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            userId = user.id,
                            name = user.name,
                            email = user.email,
                            phone = user.phone,
                            avatarUrl = user.avatarUrl,
                            roles = user.roles,
                            activeRole = user.activeRole ?: user.role,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "User not logged in",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to load profile: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Refresh user profile from backend
     */
    fun refreshProfile() {
        loadUserProfile()
    }

    /**
     * Switch active role
     */
    fun switchRole(newRole: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Get the API service for role selection
                // For now, just update local state
                // In production, this would call authRepository.selectRole(newRole)
                _uiState.update {
                    it.copy(
                        activeRole = newRole,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to switch role: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Logout user
     */
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.logout()
                _uiState.update { it.copy(isLoading = false) }
                onLogoutComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to logout: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Get role label (Indonesian)
     */
    fun getRoleLabel(role: String): String = when (role) {
        Constants.ROLE_SELLER -> "Penjual"
        Constants.ROLE_DRIVER -> "Driver"
        Constants.ROLE_ADMIN -> "Admin"
        else -> "Pembeli"
    }

    /**
     * Get role emoji
     */
    fun getRoleEmoji(role: String): String = when (role) {
        Constants.ROLE_SELLER -> "🏪"
        Constants.ROLE_DRIVER -> "🛵"
        Constants.ROLE_ADMIN -> "🛡️"
        else -> "🛒"
    }
}

/**
 * UI State for Profile screen
 */
data class ProfileUiState(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val roles: List<String> = emptyList(),
    val activeRole: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * Check if user has more than one role (needs role selection)
     */
    val hasMultipleRoles: Boolean
        get() = roles.size > 1

    /**
     * Check if user is logged in
     */
    val isLoggedIn: Boolean
        get() = userId.isNotEmpty()
}