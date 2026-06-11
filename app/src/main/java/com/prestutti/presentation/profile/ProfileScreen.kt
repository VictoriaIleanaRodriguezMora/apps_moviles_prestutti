package com.prestutti.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.prestutti.ui.theme.PrestuttiPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.toString()?.let { viewModel.onPhotoSelected(it) }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onAccountDeleted()
    }

    // ── Diálogo confirmar eliminación ─────────────────────────────────────────
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteAccountCancel,
            title = { Text("¿Ya te vas?", fontWeight = FontWeight.Bold) },
            text  = {
                Text(
                    "Parece que querés eliminar tu cuenta.\n" +
                    "¿Estás seguro de que querés continuar?\n" +
                    "Una vez que lo hagas, no lo podrás deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::onDeleteAccountConfirm,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Sí, eliminar") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteAccountCancel) { Text("No, cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Foto de perfil ────────────────────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                if (uiState.photoUri != null) {
                    AsyncImage(
                        model = uiState.photoUri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = PrestuttiPurple.copy(alpha = 0.15f)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp),
                            tint = PrestuttiPurple
                        )
                    }
                }
            }

            // Botones de foto
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrestuttiPurple)
                ) {
                    Text("Hacer foto", color = PrestuttiPurple, fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrestuttiPurple)
                ) {
                    Text("Seleccionar del carrete", color = PrestuttiPurple, fontSize = 12.sp)
                }
            }

            HorizontalDivider()

            // ── Campos ────────────────────────────────────────────────────────
            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Campos informativos (solo lectura en esta versión)
            listOf("Apellido" to "", "Email" to uiState.email, "Soy" to "", "Nickname" to "").forEach { (label, value) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = {},
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = label == "Nombre"
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Guardar ───────────────────────────────────────────────────────
            Button(
                onClick = viewModel::onSaveProfile,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrestuttiPurple),
                enabled = !uiState.isSaving
            ) {
                Text("Al guardar", fontWeight = FontWeight.Bold)
            }

            // ── Eliminar cuenta ───────────────────────────────────────────────
            TextButton(
                onClick = viewModel::onDeleteAccountRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión", color = Color.Gray)
            }

            TextButton(
                onClick = viewModel::onDeleteAccountRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar cuenta", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
