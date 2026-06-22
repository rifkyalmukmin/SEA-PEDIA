package com.example.seapedia.presentation.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.seapedia.R
import com.example.seapedia.core.ui.theme.*

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Surface,
                            Color.White
                        )
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
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceContainerLowest
                    ),
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

                        OutlinedButton(
                            onClick = { /* TODO: Google Sign Up */ },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = OnSurface
                            )
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

                        RegisterInputField(
                            label = stringResource(id = R.string.full_name),
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = stringResource(id = R.string.jane_doe),
                            leadingIcon = R.drawable.ic_person
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RegisterInputField(
                            label = stringResource(id = R.string.username),
                            value = username,
                            onValueChange = { username = it },
                            placeholder = stringResource(id = R.string.janedoe),
                            leadingIcon = R.drawable.ic_alternate_email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RegisterInputField(
                            label = stringResource(id = R.string.email_address),
                            value = email,
                            onValueChange = { email = it },
                            placeholder = stringResource(id = R.string.jane_example_com),
                            leadingIcon = R.drawable.ic_email,
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RegisterInputField(
                            label = stringResource(id = R.string.password),
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "••••••••",
                            leadingIcon = R.drawable.ic_lock,
                            keyboardType = KeyboardType.Password,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onPasswordToggle = { passwordVisible = !passwordVisible }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = onRegisterSuccess,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SeaPrimary
                            )
                        ) {
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
    onPasswordToggle: () -> Unit = {}
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
            singleLine = true
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
