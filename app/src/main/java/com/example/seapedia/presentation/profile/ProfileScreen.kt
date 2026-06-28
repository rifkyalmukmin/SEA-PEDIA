package com.example.seapedia.presentation.profile

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.example.seapedia.core.components.SeaOutlinedButton
import com.example.seapedia.core.ui.theme.SeaPediaTheme

/**
 * Ringkasan profil / dashboard (Level 1).
 * Menampilkan: identitas user, role yang dimiliki, role aktif, dan
 * placeholder saldo lintas peran (wallet/income/earning untuk level berikutnya).
 */
@Composable
fun ProfileScreen(
    navController: androidx.navigation.NavController = rememberNavController(),
    viewModel: ProfileViewModel = viewModelFactory()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.TextAlign.Center
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Spacer(Modifier.size(24.dp))

                        // Header identitas
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.name.take(1).uppercase(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = uiState.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = uiState.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.size(24.dp))

                        // Role aktif
                        Text(
                            text = "Peran Aktif",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.size(8.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(viewModel.getRoleEmoji(uiState.activeRole), fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = viewModel.getRoleLabel(uiState.activeRole),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(Modifier.size(20.dp))

                        // Daftar peran yang dimiliki
                        Text(
                            text = "Peran yang Dimiliki",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.size(8.dp))
                        if (uiState.hasMultipleRoles) {
                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = androidx.compose.foundation.lazy.Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.roles, key = { it }) { role ->
                                    RoleChip(
                                        label = viewModel.getRoleLabel(role),
                                        active = role == uiState.activeRole
                                    )
                                }
                            }
                            Spacer(Modifier.size(12.dp))
                            SeaOutlinedButton(
                                text = "Ganti Peran",
                                onClick = {
                                    // TODO: Implement role selection dialog
                                    // For now, just toggle between first two roles
                                    val currentIndex = uiState.roles.indexOf(uiState.activeRole)
                                    val nextIndex = (currentIndex + 1) % uiState.roles.size
                                    viewModel.switchRole(uiState.roles[nextIndex])
                                }
                            )
                        } else {
                            Text(
                                text = "Hanya satu peran: ${uiState.roles.firstOrEmpty()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.size(24.dp))

                        // Ringkasan saldo (placeholder — diisi di level berikutnya)
                        Text(
                            text = "Ringkasan Saldo",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.size(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            BalanceCard("Saldo Dompet", "Rp 0", Modifier.weight(1f))
                            BalanceCard("Penghasilan", "Rp 0", Modifier.weight(1f))
                        }
                        Text(
                            text = "Saldo dompet, pemasukan penjual, dan penghasilan driver akan aktif pada level berikutnya.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(Modifier.weight(1f))
                        SeaOutlinedButton(text = "Keluar", onClick = {
                            viewModel.logout {
                                navController.navigate("auth_graph") {
                                    popUpTo("auth_graph") {
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}

/**
 * Factory function to create ViewModel with proper dependencies
 */
@Composable
fun viewModelFactory(): ProfileViewModel {
    return viewModelFactory(factory = ProfileViewModelFactory())
}

/**
 * ViewModel Factory for ProfileViewModel
 */
private class ProfileViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
private fun BalanceCard(title: String, amount: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RoleChip(label: String, active: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}