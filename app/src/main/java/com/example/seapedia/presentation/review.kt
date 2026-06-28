package com.example.seapedia.presentation

import androidx.activity.viewModels
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.seapedia.core.components.RatingBar
import com.example.seapedia.core.components.ReviewCard
import com.example.seapedia.core.components.SeaButton
import com.example.seapedia.core.ui.theme.SeaPediaTheme
import com.example.seapedia.domain.AppReview
import kotlinx.coroutines.launch

/**
 * Ulasan aplikasi publik (Level 1). Guest pun boleh mengirim:
 * nama, rating 1–5, komentar. Disimpan di backend API.
 * Komentar disanitasi untuk mencegah XSS.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    onBack: () -> Unit = {},
    viewModel: ReviewViewModel = viewModelFactory()
) {
    val uiState by viewModel.uiState.collectAsState()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Review form
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

                    // Name field
                    com.example.seapedia.core.components.SeaTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nama",
                        placeholder = "Nama Anda",
                        isError = nameError,
                        errorMessage = "Nama wajib diisi"
                    )
                    Spacer(Modifier.size(12.dp))

                    // Rating
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

                    // Comment field
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

                    // Submit button
                    SeaButton(
                        text = "Kirim Ulasan",
                        onClick = {
                            if (name.isBlank() || rating == 0 || comment.isBlank()) {
                                showErrors = true
                            } else {
                                viewModel.submitReview(
                                    AppReview(
                                        reviewerName = name.trim(),
                                        rating = rating,
                                        comment = comment.trim()
                                    )
                                )
                                name = ""; rating = 0; comment = ""; showErrors = false
                            }
                        },
                        isLoading = uiState.isSubmitting
                    )
                }
            }

            Spacer(Modifier.size(16.dp))

            // Review list header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ulasan Pengguna (${uiState.reviews.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Reviews list
            when {
                uiState.isLoading -> {
                    // Show loading indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        com.example.seapedia.core.components.CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                uiState.error != null -> {
                    // Show error
                    Text(
                        text = uiState.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                else -> {
                    // Show reviews
                    if (uiState.reviews.isEmpty()) {
                        Text(
                            text = "Belum ada ulasan. Jika. Jadilah yang pertama!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.reviews, key = { it.id }) { review ->
                                ReviewCard(
                                    reviewerName = review.reviewerName,
                                    rating = review.rating,
                                    comment = review.comment
                                )
                            }
                        }
                    }
                }
            }

            // Show success snackbar on review submission
            if (!uiState.isSubmitting && uiState.reviews.size > 0) {
                LaunchedEffect(Unit) {
                    scope.launch {
                        // This will show on every recomposition when not submitting
                        // In a real app, we'd use a more sophisticated approach
                        // For now, we'll rely on the ViewModel to clear the success state
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
fun viewModelFactory(): ReviewViewModel {
    return viewModelFactory(factory = ReviewViewModelFactory())
}

/**
 * ViewModel Factory for ReviewViewModel
 */
private class ReviewViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            return ReviewViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Enhanced ReviewCard to show sanitized content
 */
@Composable
fun ReviewCard(
    reviewerName: String,
    rating: Int,
    comment: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // User avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reviewerName.take(1).uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = reviewerName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Rating stars
                        RepeatContent(
                            count = rating,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                        if (rating < 5) {
                            RepeatContent(
                                count = 5 - rating,
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$rating/5",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.size(12))
            // Comment text (already sanitized in ViewModel)
            Text(
                text = comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * Helper composable to repeat content
 */
@Composable
private fun RepeatContent(
    count: Int,
    content: @Composable () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(count) {
            content()
        }
    }
}

/**
 * Circular progress indicator component
 */
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 2.dp,
    indicatorSize: Dp = 24.dp
) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth,
        indicatorSize = indicatorSize
    )
}

@Preview(showBackground = true)
@Composable
private fun ReviewScreenPreview() {
    SeaPediaTheme { ReviewScreen() }
}