package com.prestutti.presentation.home.borrowed_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.domain.model.Loan
import com.prestutti.domain.usecase.GetBorrowedLoansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class BorrowedListUiState(
    val loans: List<Loan> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class BorrowedListViewModel @Inject constructor(
    getBorrowedLoans: GetBorrowedLoansUseCase
) : ViewModel() {

    val uiState: StateFlow<BorrowedListUiState> = getBorrowedLoans()
        .map { loans -> BorrowedListUiState(loans = loans, isLoading = false) }
        .catch { e -> emit(BorrowedListUiState(isLoading = false, error = e.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BorrowedListUiState()
        )
}
