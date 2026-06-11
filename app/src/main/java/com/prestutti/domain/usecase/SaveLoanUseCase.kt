package com.prestutti.domain.usecase

import com.prestutti.domain.model.Loan
import com.prestutti.domain.repository.LoanRepository
import javax.inject.Inject

class SaveLoanUseCase @Inject constructor(
    private val repository: LoanRepository
) {
    suspend operator fun invoke(loan: Loan): Result<Long> = runCatching {
        require(loan.personName.isNotBlank()) { "El nombre de la persona no puede estar vacío" }
        require(loan.item.isNotBlank()) { "El ítem prestado/recibido no puede estar vacío" }
        repository.saveLoan(loan)
    }
}
