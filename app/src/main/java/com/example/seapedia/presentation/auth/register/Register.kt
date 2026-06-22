package com.example.seapedia.presentation.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.seapedia.R
import com.example.seapedia.core.ui.theme.*

private data class RoleOption(val value: String, val label: String, val icon: Int)

private val roleOptions = listOf(
    RoleOption("buyer", "Pembeli", R.drawable.ic_person),
    RoleOption("seller", "Penjual", R.drawable.ic_orders),
    RoleOption("driver", "Driver", R.drawable.ic_menu)
)

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: (role: String) -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // Navigasi saat register berhasil
    LaunchedEffect(uiState.navigateToRole) {
        uiState.navigateToRole?.let { role ->
            onRegisterSuccess(role)
            viewModel.onNavigated()
        }
    }

    // Tampilkan error via Snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Surface
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Surface, Color.White)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.app_name).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = SeaPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(id = R.string.create_your_account_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Google Sign Up
                            OutlinedButton(
                                onClick = { /* TODO: Google Sign Up */ },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(vertical = 12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                                enabled = !uiState.isLoading
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_google),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Unspecified
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = stringResource(id = R.string.sign_up_with_google),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant.copy(alpha = 0.5f))
                                Text(
                                    text = stringResource(id = R.string.or_sign_up_with_email),
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant.copy(alpha = 0.5f))
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Nama Lengkap
                            RegisterInputField(
                                label = stringResource(id = R.string.full_name),
                                value = fullName,
                                onValueChange = { fullName = it },
                                placeholder = stringResource(id = R.string.jane_doe),
                                leadingIcon = R.drawable.ic_person,
                                enabled = !uiState.isLoading
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Email
                            RegisterInputField(
                                label = stringResource(id = R.string.email_address),
                                value = email,
                                onValueChange = { email = it },
                                placeholder = stringResource(id = R.string.jane_example_com),
                                leadingIcon = R.drawable.ic_email,
                                keyboardType = KeyboardType.Email,
                                enabled = !uiState.isLoading
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Password
                            RegisterInputField(
                                label = stringResource(id = R.string.password),
                                value = password,
                                onValueChange = { password = it },
                                placeholder = "••••••••",
                                leadingIcon = R.drawable.ic_lock,
                                keyboardType = KeyboardType.Password,
                                isPassword = true,
                                passwordVisible = passwordVisible,
                                onPasswordToggle = { passwordVisible = !passwordVisible },
                                enabled = !uiState.isLoading
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Role Selector
                            RoleSelector(
                                selectedRole = selectedRole,
                                onRoleSelected = { selectedRole = it },
                                enabled = !uiState.isLoading
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Tombol Buat Akun
                            Button(
                                onClick = {
                                    viewModel.register(fullName, email, password, selectedRole)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SeaPrimary),
                                enabled = !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = stringResource(id = R.string.create_account),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_arrow_forward),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            TextButton(onClick = onNavigateToLogin) {
                                Text(
                                    text = buildAnnotatedString {
                                        append(stringResource(id = R.string.already_have_an_account))
                                        withStyle(style = SpanStyle(color = SeaPrimary, fontWeight = FontWeight.Bold)) {
                                            append(stringResource(id = R.string.log_in))
                                        }
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    enabled: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Daftar sebagai",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            roleOptions.forEach { option ->
                val isSelected = selectedRole == option.value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) SeaPrimary else OutlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = if (isSelected) SeaPrimary.copy(alpha = 0.08f) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = enabled) { onRoleSelected(option.value) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = option.icon),
                            contentDescription = null,
                            tint = if (isSelected) SeaPrimary else OnSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) SeaPrimary else OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: Int,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = OutlineVariant) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onPasswordToggle) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = OutlineVariant.copy(alpha = 0.5f),
                focusedBorderColor = SeaPrimary,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            singleLine = true,
            enabled = enabled
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    SeaPediaTheme {
        RegisterScreen()
    }
}
