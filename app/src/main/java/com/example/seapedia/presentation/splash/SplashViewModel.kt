package com.example.seapedia.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seapedia.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Tujuan navigasi setelah splash selesai membaca sesi tersimpan. */
sealed interface SplashDestination {
    data object Auth : SplashDestination
    data class Role(val role: String) : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            // Jaga splash tetap tampil minimal untuk branding, tapi jangan
            // menambah delay di atas pembacaan sesi yang lambat.
            val minDelay = async { kotlinx.coroutines.delay(SPLASH_MIN_DURATION_MS) }
            val user = authRepository.getAuthState().first()
            minDelay.await()

            _destination.value = if (user == null) {
                SplashDestination.Auth
            } else {
                SplashDestination.Role(user.role)
            }
        }
    }

    private companion object {
        const val SPLASH_MIN_DURATION_MS = 1500L
    }
}
