package com.prestutti.domain.usecase

import com.prestutti.domain.model.Loan
import com.prestutti.domain.repository.LoanRepository // la interfaz, capa de abstraccion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Es llamado en /presentation/home/lent_list/LentListViewModel.kt
class GetLentLoansUseCase @Inject constructor(
    // /domain/model/LoanRepository.kt
    private val repository: LoanRepository // la interfaz, capa de abstraccion
) {
    operator fun invoke(): Flow<List<Loan>> = repository.getLentLoans() // la interfaz, capa de abstraccion.
}
