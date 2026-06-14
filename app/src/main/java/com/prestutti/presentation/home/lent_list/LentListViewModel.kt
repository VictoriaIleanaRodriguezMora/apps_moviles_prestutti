package com.prestutti.presentation.home.lent_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.domain.model.Loan
import com.prestutti.domain.usecase.GetLentLoansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
/*
El ViewModel:
- No hace lógica de datos (eso es del UseCase/Repository)
- No hace UI (eso es de la Screen)
- Solo transforma datos en "estado de pantalla" (UiState)
- Sobrevive a las rotaciones de pantalla
 */
data class LentListUiState(
    val loans: List<Loan> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class LentListViewModel @Inject constructor(
    getLentLoans: GetLentLoansUseCase
) : ViewModel() {
    // LentListViewModel se conecta directamente al Flow de Room
    // LentListViewModel recibe List<Loan>
    // LentListScreen se redibuja
    val uiState: StateFlow<LentListUiState> = getLentLoans() // UseCase/GetLoanUseCase
        .map { loans -> LentListUiState(loans = loans, isLoading = false) }
        .catch { e -> emit(LentListUiState(isLoading = false, error = e.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LentListUiState()
        )
}
