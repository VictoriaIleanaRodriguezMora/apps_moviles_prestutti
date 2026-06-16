package com.prestutti.presentation.add_loan

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    if (uiState.showAddCategoryDialog) {
        AddCategoryDialog(
            accentColor = accentColor,
            onDismiss = { viewModel.onShowAddCategoryDialog(false) },
            onConfirm = viewModel::onAddCategory
        )
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

            // Inicio Bloque API
            if (uiState.networkError != null) {
                // Manejo visual de error: Se quedó sin internet
                Text(
                    text = uiState.networkError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            } else if (uiState.suggestedContacts.isNotEmpty()) {
                // Carrusel de contactos sugeridos (JSONPlaceholder)
                Text("Contactos sugeridos (Nube):", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    items(uiState.suggestedContacts) { contact ->
                        AssistChip(
                            onClick = {
                                // ¡Magia!: Autocompletar el campo de texto con el nombre de la API
                                viewModel.onPersonNameChange(contact.name)
                            },
                            label = { Text(contact.name) }
                        )
                    }
                }
            }

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
                categories = uiState.categories,
                selected = uiState.category,
                accentColor = accentColor,
                onSelected = viewModel::onCategorySelected,
                onAddNewClick = { viewModel.onShowAddCategoryDialog(true) }
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

// ── Helpers - Fns auxiliares ───────────────────────────────────────────────────────────────────

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
    categories: List<String>,
    selected: String?,
    accentColor: Color,
    onSelected: (String) -> Unit,
    onAddNewClick: () -> Unit
) {
    val rows = (categories + "NUEVA_CHIP_PLACEHOLDER").chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    if (cat == "NUEVA_CHIP_PLACEHOLDER") {
                        AssistChip(
                            onClick = onAddNewClick,
                            label = { Text("Nueva", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, accentColor)
                        )
                    } else {
                        val isSelected = selected == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelected(cat) },
                            label = { Text(cat, fontSize = 12.sp) },
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
}

// ?
@Composable
private fun AddCategoryDialog(
    accentColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                    Text(
                        text = "Nueva categoría",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (name.isNotBlank()) onConfirm(name)
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = { onConfirm(name) },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Agregar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}