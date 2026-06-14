package com.prestutti.presentation.forgot_password

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.prestutti.R
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prestutti.ui.theme.PrestuttiPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Recuperar contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (uiState.isEmailSent) {
                // --- PANTALLA DE ÉXITO ---
                Image(
                    painter = painterResource(id = R.drawable.ic_prestutti_logo),
                    contentDescription = "Prestutti Logo",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Prestutti",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrestuttiPurple
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = PrestuttiPurple.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¡Correo enviado!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrestuttiPurple,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Enviamos las instrucciones de recuperación a:\n${uiState.email}",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrestuttiPurple)
                ) {
                    Text("Volver al Login", fontWeight = FontWeight.Bold)
                }

            } else {
                // --- FORMULARIO ---
                Image(
                    painter = painterResource(id = R.drawable.ic_prestutti_logo),
                    contentDescription = "Prestutti Logo",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Prestutti",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrestuttiPurple
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Ingresá tu email de registro para recibir un enlace de recuperación.",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp),
                )

                if (uiState.error != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0E0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.error!!,
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email de recuperación") },
                    isError = uiState.emailError != null,
                    supportingText = {
                        if (uiState.emailError != null) {
                            Text(text = uiState.emailError!!, color = Color.Red)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = viewModel::onSendResetLinkClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrestuttiPurple),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Enviar instrucciones", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}