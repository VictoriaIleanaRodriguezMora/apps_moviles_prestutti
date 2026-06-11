package com.prestutti.presentation.add_loan

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prestutti.ui.theme.PrestuttiPink
import com.prestutti.ui.theme.PrestuttiPurple
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoanScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddLoanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val context = LocalContext.current
    val accentColor = if (uiState.isLent) PrestuttiPurple else PrestuttiPink

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isLent) "¿Qué prestaste?" else "¿Qué te prestaron?",
                        fontWeight = FontWeight.Bold
                    )
                },
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Qué se presta ─────────────────────────────────────────────────
            SectionLabel(text = if (uiState.isLent) "¿Qué lo prestás?" else "¿Qué te prestaron?")
            OutlinedTextField(
                value = uiState.item,
                onValueChange = viewModel::onItemChange,
                placeholder = { Text("Ej: Cuchillo de cocina") },
                isError = uiState.fieldErrors.containsKey("item"),
                supportingText = { uiState.fieldErrors["item"]?.let { Text(it, color = Color.Red) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // ── Fecha del préstamo ────────────────────────────────────────────
            SectionLabel(text = "¿Cuándo lo prestás?")
            DatePickerField(
                date = uiState.date,
                label = "Fecha",
                accentColor = accentColor,
                dateFormat = dateFormat,
                context = context,
                onDateSelected = { date -> date?.let(viewModel::onDateChange) }
            )

            // ── Fecha de devolución ───────────────────────────────────────────
            SectionLabel(text = "¿Cuándo te lo devuelven?")
            DatePickerField(
                date = uiState.dueDate,
                label = "Fecha de devolución (opcional)",
                accentColor = accentColor,
                dateFormat = dateFormat,
                context = context,
                onDateSelected = viewModel::onDueDateChange
            )

            // ── A quién ───────────────────────────────────────────────────────
            SectionLabel(text = if (uiState.isLent) "¿A quién lo prestás?" else "¿Quién te lo presta?")
            OutlinedTextField(
                value = uiState.personName,
                onValueChange = viewModel::onPersonNameChange,
                placeholder = { Text("Ej: Paulina") },
                isError = uiState.fieldErrors.containsKey("person"),
                supportingText = { uiState.fieldErrors["person"]?.let { Text(it, color = Color.Red) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // ── Categoría ────────────────────────────────────────────────────
            SectionLabel(text = "ELEGÍ UNA CATEGORÍA:")
            CategoryChipGroup(
                selected = uiState.category,
                accentColor = accentColor,
                onSelected = viewModel::onCategorySelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Error global ─────────────────────────────────────────────────
            uiState.error?.let {
                Text(it, color = Color.Red, fontSize = 13.sp)
            }

            // ── Botón guardar ────────────────────────────────────────────────
            Button(
                onClick = viewModel::onSaveClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Agregar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.DarkGray)
}

@Composable
private fun DatePickerField(
    date: Date?,
    label: String,
    accentColor: Color,
    dateFormat: SimpleDateFormat,
    context: android.content.Context,
    onDateSelected: (Date?) -> Unit
) {
    val calendar = Calendar.getInstance().apply { date?.let { time = it } }

    OutlinedTextField(
        value = date?.let { dateFormat.format(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        placeholder = { Text(label) },
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val cal = Calendar.getInstance().apply { set(year, month, day) }
                        onDateSelected(cal.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha", tint = accentColor)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChipGroup(
    selected: LoanCategory?,
    accentColor: Color,
    onSelected: (LoanCategory) -> Unit
) {
    val rows = LoanCategory.values().toList().chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    val isSelected = selected == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelected(cat) },
                        label = { Text(cat.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = accentColor,
                            borderColor = Color.LightGray
                        )
                    )
                }
            }
        }
    }
}
