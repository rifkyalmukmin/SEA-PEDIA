package com.example.seapedia.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.seapedia.R
import com.example.seapedia.core.ui.theme.SeaPrimary

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToRole: (role: String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (val dest = destination) {
            null -> Unit // masih membaca sesi tersimpan
            SplashDestination.Auth -> onNavigateToAuth()
            is SplashDestination.Role -> onNavigateToRole(dest.role)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeaPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.app_name).uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            color = androidx.compose.ui.graphics.Color.White,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 4.sp
        )
    }
}
