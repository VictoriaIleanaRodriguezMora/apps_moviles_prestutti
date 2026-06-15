package com.prestutti.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prestutti.R
import com.prestutti.ui.theme.PrestuttiPurple

@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) onNavigateToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()          // ← respeta cámara, notch y barras del sistema
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header morado ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrestuttiPurple)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "¡Únete a Prestutti!",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Logo ─────────────────────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.ic_prestutti_logo),
                contentDescription = "Prestutti Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Email ────────────────────────────────────────────────────────
            RegisterField(
                label = "Ingresa tu email",
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                onClear = { viewModel.onEmailChange("") },
                error = uiState.emailError,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Contraseña ───────────────────────────────────────────────────
            RegisterField(
                label = "Ingresa tu contraseña",
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                onClear = { viewModel.onPasswordChange("") },
                error = uiState.passwordError,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Repetir contraseña ───────────────────────────────────────────
            RegisterField(
                label = "Repite tu contraseña",
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                onClear = { viewModel.onConfirmPasswordChange("") },
                error = uiState.confirmError,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Botón Registrar ──────────────────────────────────────────────
            Button(
                onClick = viewModel::onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrestuttiPurple),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Registrar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Link a Login ─────────────────────────────────────────────────
            Row {
                Text("¿Ya tienes una cuenta? ", fontSize = 13.sp, color = Color.DarkGray)
                Text(
                    text = "Inicia sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Campo de formulario con label flotante + botón limpiar ────────────────────

@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    error: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 12.sp) },
            singleLine = true,
            isError = error != null,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Cancel, contentDescription = "Limpiar", tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp)
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}