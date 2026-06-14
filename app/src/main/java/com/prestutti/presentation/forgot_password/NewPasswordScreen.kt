package com.prestutti.presentation.forgot_password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prestutti.R // Asegúrate de importar tu R para los drawables
import com.prestutti.ui.theme.PrestuttiPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreen(
    onNavigateBack: () -> Unit,
    onPasswordResetSuccess: () -> Unit, // Función para enviarlo al Login cuando termine
    viewModel: NewPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Si el guardado fue un éxito, navegamos automáticamente
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onPasswordResetSuccess()
        }
    }

    Scaffold(
        containerColor = Color.White, // Mantenemos el fondo blanco armonioso
        topBar = {
            TopAppBar(
                title = { Text("Crear nueva contraseña", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tu nueva contraseña debe ser diferente a las contraseñas utilizadas anteriormente.",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- CAMPO NUEVA CONTRASEÑA ---
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Nueva contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        val icon = if (uiState.isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                        Icon(painter = painterResource(id = icon), contentDescription = "Ver contraseña")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPO CONFIRMAR CONTRASEÑA ---
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = viewModel::toggleConfirmPasswordVisibility) {
                        val icon = if (uiState.isConfirmPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                        Icon(painter = painterResource(id = icon), contentDescription = "Ver contraseña")
                    }
                }
            )

            // --- MENSAJE DE ERROR ---
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN CONFIRMAR ---
            Button(
                onClick = viewModel::onSubmitNewPassword,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrestuttiPurple),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Guardando..." else "Restablecer contraseña", fontWeight = FontWeight.Bold)
            }
        }
    }
}