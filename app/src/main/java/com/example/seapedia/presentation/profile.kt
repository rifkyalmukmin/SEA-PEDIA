package com.example.seapedia.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seapedia.core.components.SeaOutlinedButton
import com.example.seapedia.core.ui.theme.SeaPediaTheme
import com.example.seapedia.core.utils.Constants
import com.example.seapedia.core.utils.Formatter

/**
 * Ringkasan profil / dashboard (Level 1).
 * Menampilkan: identitas user, role yang dimiliki, role aktif, dan
 * placeholder saldo lintas peran (wallet/income/earning untuk level berikutnya).
 */
@Composable
fun ProfileScreen(
    name: String = "Pengguna SEAPEDIA",
    email: String = "user@seapedia.id",
    roles: List<String> = listOf(Constants.ROLE_BUYER),
    activeRole: String = Constants.ROLE_BUYER,
    onSwitchRole: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    text = name.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.size(24.dp))

        // Role aktif
        SectionLabel("Peran Aktif")
        Spacer(Modifier.size(8.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(roleEmoji(activeRole), fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = roleLabel(activeRole),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(Modifier.size(20.dp))

        // Daftar peran yang dimiliki
        SectionLabel("Peran yang Dimiliki")
        Spacer(Modifier.size(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(roles, key = { it }) { role ->
                RoleChip(label = roleLabel(role), active = role == activeRole)
            }
        }
        if (roles.size > 1) {
            Spacer(Modifier.size(12.dp))
            SeaOutlinedButton(text = "Ganti Peran", onClick = onSwitchRole)
        }

        Spacer(Modifier.size(24.dp))

        // Ringkasan saldo (placeholder — diisi di level berikutnya)
        SectionLabel("Ringkasan Saldo")
        Spacer(Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BalanceCard("Saldo Dompet", Formatter.rupiah(0), Modifier.weight(1f))
            BalanceCard("Penghasilan", Formatter.rupiah(0), Modifier.weight(1f))
        }
        Text(
            text = "Saldo dompet, pemasukan penjual, dan penghasilan driver akan aktif pada level berikutnya.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.weight(1f))
        SeaOutlinedButton(text = "Keluar", onClick = onLogout)
        Spacer(Modifier.size(8.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
    )
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

private fun roleLabel(role: String): String = when (role) {
    Constants.ROLE_SELLER -> "Penjual"
    Constants.ROLE_DRIVER -> "Driver"
    Constants.ROLE_ADMIN -> "Admin"
    else -> "Pembeli"
}

private fun roleEmoji(role: String): String = when (role) {
    Constants.ROLE_SELLER -> "🏪"
    Constants.ROLE_DRIVER -> "🛵"
    Constants.ROLE_ADMIN -> "🛡️"
    else -> "🛒"
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() {
    SeaPediaTheme {
        ProfileScreen(
            name = "Rifky",
            email = "rifky@seapedia.id",
            roles = listOf(Constants.ROLE_BUYER, Constants.ROLE_SELLER),
            activeRole = Constants.ROLE_BUYER
        )
    }
}
