package com.prestutti.presentation.loan_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prestutti.domain.model.LoanType
import com.prestutti.ui.theme.PrestuttiPink
import com.prestutti.ui.theme.PrestuttiPurple
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: LoanDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val loan = uiState.loan
    val accentColor = if (loan?.type == LoanType.LENT) PrestuttiPurple else PrestuttiPink

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(loan?.item ?: "Detalle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentColor)
                }
            }
            loan == null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Préstamo no encontrado")
                }
            }
            else -> {
                Column(
                    modifier = Modifier.padding(padding).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tipo badge
                    Badge(containerColor = accentColor) {
                        Text(
                            if (loan.type == LoanType.LENT) "Presté" else "Me prestaron",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    DetailRow(label = "Ítem", value = loan.item)
                    if (loan.description.isNotBlank())
                        DetailRow(label = "Descripción", value = loan.description)
                    loan.category?.let {
                        DetailRow(label = "Categoría", value = it)
                    }
                    DetailRow(
                        label = if (loan.type == LoanType.LENT) "Prestado a" else "Prestado por",
                        value = loan.personName
                    )
                    DetailRow(label = "Fecha", value = dateFormat.format(loan.date))
                    loan.dueDate?.let {
                        DetailRow(label = "Devolver antes de", value = dateFormat.format(it))
                    }
                    DetailRow(label = "Estado", value = if (loan.isReturned) "Devuelto ✓" else "Pendiente")

                    Spacer(modifier = Modifier.weight(1f))

                    // Botón marcar como devuelto
                    if (!loan.isReturned) {
                        Button(
                            onClick = viewModel::onMarkAsReturned,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("Marcar como devuelto", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = viewModel::onDeleteLoan,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Eliminar préstamo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFEEEEEE))
    }
}