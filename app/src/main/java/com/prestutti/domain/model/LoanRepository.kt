package com.prestutti.domain.repository

import com.prestutti.domain.model.Loan
import kotlinx.coroutines.flow.Flow
// Esta interfaz es lo que el dominio conoce. No sabe si los datos vienen de Room, de internet, o de la nada. Eso es el punto.
// Lo que conoce es getLentLoans(), esos metodos. Si cambia el contenido de estos, la UI no se entera

// LoanRepository es una interfaz. No tiene código real, solo promesas, no implementación
interface LoanRepository {
    fun getLentLoans(): Flow<List<Loan>>
    fun getBorrowedLoans(): Flow<List<Loan>>
    suspend fun saveLoan(loan: Loan): Long // es llamado por prestutti/domain/usecase/SaveLoanUseCase.kt
    suspend fun updateLoan(loan: Loan)
    suspend fun deleteLoan(loan: Loan)
}
