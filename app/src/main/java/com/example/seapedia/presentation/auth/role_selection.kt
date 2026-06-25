package com.example.seapedia.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seapedia.core.components.SeaButton
import com.example.seapedia.core.ui.theme.SeaPediaTheme
import com.example.seapedia.core.utils.Constants

private data class RoleOption(val value: String, val label: String, val desc: String, val icon: String)

private val roleCatalog = mapOf(
    Constants.ROLE_BUYER to RoleOption(Constants.ROLE_BUYER, "Pembeli", "Belanja, isi keranjang, dan checkout pesanan.", "🛒"),
    Constants.ROLE_SELLER to RoleOption(Constants.ROLE_SELLER, "Penjual", "Kelola toko, produk, dan pesanan masuk.", "🏪"),
    Constants.ROLE_DRIVER to RoleOption(Constants.ROLE_DRIVER, "Driver", "Ambil job pengiriman dan catat penghasilan.", "🛵"),
)

/**
 * Layar pemilihan active role untuk user dengan >1 role non-admin (Level 1).
 * Otorisasi mengikuti role aktif yang dipilih di sini.
 */
@Composable
fun RoleSelectionScreen(
    roles: List<String>,
    onRoleSelected: (String) -> Unit = {},
) {
    var selected by remember { mutableStateOf(roles.firstOrNull()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Spacer(Modifier.size(32.dp))
        Text(
            text = "Pilih Peran",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Akun Anda memiliki beberapa peran. Pilih peran yang ingin digunakan untuk sesi ini.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.size(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            roles.mapNotNull { roleCatalog[it] }.forEach { option ->
                RoleCard(
                    option = option,
                    selected = selected == option.value,
                    onClick = { selected = option.value }
                )
            }
        }

        Spacer(Modifier.weight(1f))
        SeaButton(
            text = "Lanjutkan",
            enabled = selected != null,
            onClick = { selected?.let(onRoleSelected) }
        )
        Spacer(Modifier.size(16.dp))
    }
}

@Composable
private fun RoleCard(option: RoleOption, selected: Boolean, onClick: () -> Unit) {
    val border = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(if (selected) 2.dp else 1.dp, border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(option.icon, fontSize = 24.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = option.desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selected) {
                Text("✓", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleSelectionPreview() {
    SeaPediaTheme {
        RoleSelectionScreen(roles = listOf(Constants.ROLE_BUYER, Constants.ROLE_SELLER, Constants.ROLE_DRIVER))
    }
}
