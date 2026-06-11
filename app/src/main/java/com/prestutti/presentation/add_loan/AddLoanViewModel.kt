package com.prestutti.presentation.add_loan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.domain.model.Loan
import com.prestutti.domain.model.LoanType
import com.prestutti.domain.usecase.SaveLoanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

// Categorías del Figma
enum class LoanCategory(val label: String) {
    ROPA("Ropa"),
    LIBROS("Libros"),
    ELECTRONICA("Electrónica"),
    HERRAMIENTAS("Herramientas"),
    COCINA("Cocina"),
    DEPORTE("Deporte"),
    OTRO("Otro")
}

data class AddLoanUiState(
    val isLent: Boolean = true,           // true = Presté, false = Me prestaron
    val item: String = "",
    val description: String = "",
    val personName: String = "",
    val date: Date = Date(),
    val dueDate: Date? = null,
    val category: LoanCategory? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class AddLoanViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveLoanUseCase: SaveLoanUseCase
) : ViewModel() {

    // isLent se pasa como arg de navegación: addLoan/{isLent}
    private val isLent: Boolean = savedStateHandle.get<Boolean>("isLent") ?: true

    private val _uiState = MutableStateFlow(AddLoanUiState(isLent = isLent))
    val uiState: StateFlow<AddLoanUiState> = _uiState.asStateFlow()

    fun onItemChange(value: String)       { update { copy(item = value) } }
    fun onDescriptionChange(value: String){ update { copy(description = value) } }
    fun onPersonNameChange(value: String) { update { copy(personName = value) } }
    fun onDateChange(date: Date)          { update { copy(date = date) } }
    fun onDueDateChange(date: Date?)      { update { copy(dueDate = date) } }
    fun onCategorySelected(cat: LoanCategory) { update { copy(category = cat) } }

    fun onSaveClick() {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        if (state.item.isBlank())       errors["item"] = "Indicá qué se presta"
        if (state.personName.isBlank()) errors["person"] = "Indicá a quién"

        if (errors.isNotEmpty()) {
            update { copy(fieldErrors = errors) }
            return
        }

        viewModelScope.launch {
            update { copy(isSaving = true, fieldErrors = emptyMap()) }
            val loan = Loan(
                personName  = state.personName,
                item        = state.item,
                description = state.description,
                date        = state.date,
                dueDate     = state.dueDate,
                type        = if (state.isLent) LoanType.LENT else LoanType.BORROWED
            )
            saveLoanUseCase(loan)
                .onSuccess { update { copy(isSaving = false, isSaved = true) } }
                .onFailure { e -> update { copy(isSaving = false, error = e.message) } }
        }
    }

    private fun update(block: AddLoanUiState.() -> AddLoanUiState) {
        _uiState.value = _uiState.value.block()
    }
}
