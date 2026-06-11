package com.prestutti.domain.usecase

import com.prestutti.domain.model.Loan
import com.prestutti.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLentLoansUseCase @Inject constructor(
    private val repository: LoanRepository
) {
    operator fun invoke(): Flow<List<Loan>> = repository.getLentLoans()
}
