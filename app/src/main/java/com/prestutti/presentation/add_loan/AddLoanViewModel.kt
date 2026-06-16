package com.prestutti.presentation.add_loan

import android.content.Context
import android.net.http.HttpException
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.data.remote.dto.ContactDto
import com.prestutti.data.remote.dto.PrestuttiApiService
import com.prestutti.domain.model.Loan
import com.prestutti.domain.model.LoanType
import com.prestutti.domain.usecase.SaveLoanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Date
import javax.inject.Inject

data class AddLoanUiState(
    val isLent: Boolean = true,           // true = Presté, false = Me prestaron
    val item: String = "",
    val description: String = "",
    val personName: String = "",
    val date: Date = Date(),
    val dueDate: Date? = null,
    val categories: List<String> = emptyList(),
    val category: String? = null,
    val showAddCategoryDialog: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap(),
    val error: String? = null,
    val suggestedContacts: List<ContactDto> = emptyList(),
    val networkError: String? = null
)

private const val PREFS_NAME = "prestutti_prefs"
private const val KEY_CUSTOM_CATEGORIES = "custom_categories"

private val defaultCategories = listOf(
    "Herramientas", "Cocina", "Dinero", "Libros", "Ropa", "Apuntes", "Juegos", "Otros"
)

@HiltViewModel
class AddLoanViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveLoanUseCase: SaveLoanUseCase,
    private val apiService: PrestuttiApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // isLent se pasa como arg de navegación: addLoan/{isLent}
    private val isLent: Boolean = savedStateHandle.get<Boolean>("isLent") ?: true

    private val _uiState = MutableStateFlow(
        AddLoanUiState(isLent = isLent, categories = loadCategories())
    )
    val uiState: StateFlow<AddLoanUiState> = _uiState.asStateFlow()

    init {
        // Llamamos a internet apenas inicia el ViewModel
        fetchSuggestedContacts()
    }

    private fun loadCategories(): List<String> {
        val custom = prefs.getStringSet(KEY_CUSTOM_CATEGORIES, emptySet()) ?: emptySet()
        return defaultCategories + custom.sorted()
    }

    // FUNCIÓN NUEVA: Llamada asincrónica a Internet con "Manejo de Errores"
    private fun fetchSuggestedContacts() {
        viewModelScope.launch {
            try {
                // 1. Limpiamos cualquier error previo
                update { copy(networkError = null) }

                // 2. Hacemos la llamada al servidor JSONPlaceholder a través de Retrofit
                val contacts = apiService.getSuggestedContacts()

                // 3. Si tiene éxito, guardamos los datos en tu UiState centralizado
                update { copy(suggestedContacts = contacts) }

            } catch (e: IOException) {
                // CATCH 1: Manejo de errores de conectividad (Regla del Hito 2)
                update { copy(networkError = "Sin conexión a internet. Mostrando solo tus contactos locales.") }

            } catch (e: Exception) {
                // CATCH 3: Por si ocurre cualquier otro error imprevisto
                update { copy(networkError = "Ocurrió un error al cargar las sugerencias.") }
            }
        }
    }

    fun onItemChange(value: String)        { update { copy(item = value) } }
    fun onDescriptionChange(value: String) { update { copy(description = value) } }
    fun onPersonNameChange(value: String)  { update { copy(personName = value) } }
    fun onDateChange(date: Date)           { update { copy(date = date) } }
    fun onDueDateChange(date: Date?)       { update { copy(dueDate = date) } }
    fun onCategorySelected(value: String)  { update { copy(category = value) } }

    fun onShowAddCategoryDialog(show: Boolean) {
        update { copy(showAddCategoryDialog = show) }
    }

    // Se llama cuando el usuario crea una categoria
    fun onAddCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return

        val current = _uiState.value.categories
        val alreadyExists = current.any { it.equals(trimmed, ignoreCase = true) }

        if (!alreadyExists) {
            val custom = prefs.getStringSet(KEY_CUSTOM_CATEGORIES, emptySet())?.toMutableSet()
                ?: mutableSetOf()
            custom.add(trimmed)
            prefs.edit().putStringSet(KEY_CUSTOM_CATEGORIES, custom).apply()
        }

        update {
            copy(
                categories = if (alreadyExists) categories else categories + trimmed,
                category = if (alreadyExists) {
                    current.first { it.equals(trimmed, ignoreCase = true) }
                } else {
                    trimmed
                },
                showAddCategoryDialog = false
            )
        }
    }

    // Se llama cuando el usuario toca "guardar" un prestamo
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
                category    = state.category,
                date        = state.date,
                dueDate     = state.dueDate,
                type        = if (state.isLent) LoanType.LENT else LoanType.BORROWED
            )
            // Llama a SaveLoanUseCase()
            // app/src/main/java/com/prestutti/domain/usecase/SaveLoanUseCase.kt
            saveLoanUseCase(loan)
                .onSuccess { update { copy(isSaving = false, isSaved = true) } }
                .onFailure { e -> update { copy(isSaving = false, error = e.message) } }
        }
    }

    private fun update(block: AddLoanUiState.() -> AddLoanUiState) {
        _uiState.value = _uiState.value.block()
    }
}
