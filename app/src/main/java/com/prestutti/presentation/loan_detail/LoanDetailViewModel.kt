package com.prestutti.presentation.loan_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.domain.model.Loan
import com.prestutti.domain.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanDetailUiState(
    val loan: Loan? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false
)

@HiltViewModel
class LoanDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanRepository
) : ViewModel() {

    private val loanId: Long = checkNotNull(savedStateHandle["loanId"])

    private val _uiState = MutableStateFlow(LoanDetailUiState())
    val uiState: StateFlow<LoanDetailUiState> = _uiState.asStateFlow()

    init { loadLoan() }

    private fun loadLoan() {
        viewModelScope.launch {
            // Buscamos en la lista observable filtrando por id
            repository.getLentLoans().collect { loans ->
                val found = loans.firstOrNull { it.id == loanId }
                    ?: run {
                        // Buscar en borrowed si no estaba en lent
                        null
                    }
                _uiState.value = LoanDetailUiState(loan = found, isLoading = false)
            }
        }
    }

    fun onMarkAsReturned() {
        val loan = _uiState.value.loan ?: return
        viewModelScope.launch {
            repository.updateLoan(loan.copy(isReturned = true))
            _uiState.value = _uiState.value.copy(loan = loan.copy(isReturned = true))
        }
    }

    fun onDeleteLoan() {
        val loan = _uiState.value.loan ?: return
        viewModelScope.launch {
            repository.deleteLoan(loan)
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }
}
