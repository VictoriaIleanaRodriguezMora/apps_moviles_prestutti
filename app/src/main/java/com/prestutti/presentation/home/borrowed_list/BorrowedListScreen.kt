package com.prestutti.presentation.home.borrowed_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prestutti.presentation.home.lent_list.LoanItemCard
import com.prestutti.ui.theme.PrestuttiPink

@Composable
fun BorrowedListScreen(
    onLoanClick: (Long) -> Unit,
    onAddLoan: () -> Unit,
    viewModel: BorrowedListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrestuttiPink)
            }
            uiState.loans.isEmpty() -> {
                EmptyBorrowedState(onAddLoan = onAddLoan)
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.loans, key = { it.id }) { loan ->
                        LoanItemCard(loan = loan, onClick = { onLoanClick(loan.id) })
                    }
                }
            }
        }
    }
}

// ── Estado vacío ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyBorrowedState(onAddLoan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "¡Tu listado está vacío!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrestuttiPink
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Parece que todavía te prestaron nada.\nComenzá a usar Prestutti con el botón +",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddLoan,
            colors = ButtonDefaults.buttonColors(containerColor = PrestuttiPink),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Agregar préstamo")
        }
    }
}
