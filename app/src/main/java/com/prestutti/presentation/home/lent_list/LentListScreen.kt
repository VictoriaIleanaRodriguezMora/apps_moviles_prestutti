package com.prestutti.presentation.home.lent_list

import androidx.compose.foundation.clickable
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
import com.prestutti.domain.model.Loan
import com.prestutti.domain.model.LoanType
import com.prestutti.ui.theme.PrestuttiPink
import com.prestutti.ui.theme.PrestuttiPurple
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LentListScreen(
    onLoanClick: (Long) -> Unit,
    onAddLoan: () -> Unit,
    viewModel: LentListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrestuttiPurple)
            }
            uiState.loans.isEmpty() -> {
                EmptyLentState(onAddLoan = onAddLoan)
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
private fun EmptyLentState(onAddLoan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("¡Tu listado está vacío!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrestuttiPurple)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Parece que todavía no prestaste nada.\nComenzá a usar Prestutti con el botón +",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddLoan,
            colors = ButtonDefaults.buttonColors(containerColor = PrestuttiPurple),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Agregar préstamo")
        }
    }
}

// ── Card de ítem ─────────────────────────────────────────────────────────────

@Composable
fun LoanItemCard(loan: Loan, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val accentColor = if (loan.type == LoanType.LENT) PrestuttiPurple else PrestuttiPink

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con inicial de la persona
            Surface(
                modifier = Modifier.size(48.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = accentColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = loan.personName.first().uppercaseChar().toString(),
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = loan.item, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = loan.personName, color = Color.Gray, fontSize = 13.sp)
                loan.dueDate?.let {
                    Text(
                        text = "Vence: ${dateFormat.format(it)}",
                        color = accentColor,
                        fontSize = 12.sp
                    )
                }
            }

            if (loan.isReturned) {
                Badge(containerColor = Color(0xFF4CAF50)) {
                    Text(
                        "Devuelto",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}
