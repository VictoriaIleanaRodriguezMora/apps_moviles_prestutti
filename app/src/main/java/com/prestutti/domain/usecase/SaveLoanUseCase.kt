package com.prestutti.domain.usecase

import com.prestutti.domain.model.Loan
import com.prestutti.domain.repository.LoanRepository
import javax.inject.Inject

// Regla de negocio
// Las validaciones van acá, no en el ViewModel ni en la UI. Si hay otra pantalla que también guarda préstamos, reusa el mismo UseCase.
class SaveLoanUseCase @Inject constructor(
    private val repository: LoanRepository // ← es una INTERFAZ. No tiene código real, solo promesas:
) {
    suspend operator fun invoke(loan: Loan): Result<Long> = runCatching {
        require(loan.personName.isNotBlank()) { "El nombre de la persona no puede estar vacío" }
        require(loan.item.isNotBlank()) { "El ítem prestado/recibido no puede estar vacío" }
        // Llama a repository.saveLoan(loan)
        // app/src/main/java/com/prestutti/domain/model/LoanRepository.kt
        repository.saveLoan(loan)
    }
}
