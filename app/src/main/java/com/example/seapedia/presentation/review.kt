package com.example.seapedia.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.seapedia.core.components.RatingBar
import com.example.seapedia.core.components.ReviewCard
import com.example.seapedia.core.components.SeaButton
import com.example.seapedia.core.components.SeaTextField
import com.example.seapedia.core.ui.theme.SeaPediaTheme
import com.example.seapedia.domain.AppReview
import kotlinx.coroutines.launch

/**
 * Ulasan aplikasi publik (Level 1). Guest pun boleh mengirim:
 * nama, rating 1–5, komentar. Disimpan di state (dummy) untuk demo.
 * Komentar dirender sebagai teks biasa via ReviewCard (aman dari markup).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    onBack: () -> Unit = {},
) {
    val reviews = remember { mutableStateListOf<AppReview>().apply { addAll(DummyData.reviews) } }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    val nameError = showErrors && name.isBlank()
    val ratingError = showErrors && rating == 0
    val commentError = showErrors && comment.isBlank()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Ulasan Aplikasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Bagikan pengalaman Anda",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(
                            text = "Tanpa perlu transaksi — siapa pun boleh memberi ulasan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.size(16.dp))

                        SeaTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nama",
                            placeholder = "Nama Anda",
                            isError = nameError,
                            errorMessage = "Nama wajib diisi"
                        )
                        Spacer(Modifier.size(12.dp))

                        Text(
                            text = "Rating",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.size(4.dp))
                        RatingBar(
                            rating = rating,
                            starSize = 32,
                            onRatingChange = { rating = it }
                        )
                        if (ratingError) {
                            Text(
                                text = "Pilih rating 1–5",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(Modifier.size(12.dp))

                        Text(
                            text = "Komentar",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            placeholder = { Text("Tulis pengalaman Anda…") },
                            shape = RoundedCornerShape(8.dp),
                            minLines = 3,
                            isError = commentError
                        )
                        if (commentError) {
                            Text(
                                text = "Komentar wajib diisi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(Modifier.size(16.dp))

                        SeaButton(
                            text = "Kirim Ulasan",
                            onClick = {
                                if (name.isBlank() || rating == 0 || comment.isBlank()) {
                                    showErrors = true
                                } else {
                                    reviews.add(
                                        0,
                                        AppReview(
                                            id = "local_${reviews.size}",
                                            reviewerName = name.trim(),
                                            rating = rating,
                                            comment = comment.trim()
                                        )
                                    )
                                    name = ""; rating = 0; comment = ""; showErrors = false
                                    scope.launch { snackbar.showSnackbar("Terima kasih atas ulasan Anda!") }
                                }
                            }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Ulasan Pengguna (${reviews.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(reviews, key = { it.id }) { review ->
                ReviewCard(
                    reviewerName = review.reviewerName,
                    rating = review.rating,
                    comment = review.comment
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewScreenPreview() {
    SeaPediaTheme { ReviewScreen() }
}
