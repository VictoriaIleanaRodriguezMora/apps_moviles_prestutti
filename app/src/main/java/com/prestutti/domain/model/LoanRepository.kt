package com.prestutti.domain.repository

import com.prestutti.domain.model.Loan
import kotlinx.coroutines.flow.Flow

interface LoanRepository {
    fun getLentLoans(): Flow<List<Loan>>
    fun getBorrowedLoans(): Flow<List<Loan>>
    suspend fun saveLoan(loan: Loan): Long
    suspend fun updateLoan(loan: Loan)
    suspend fun deleteLoan(loan: Loan)
}
