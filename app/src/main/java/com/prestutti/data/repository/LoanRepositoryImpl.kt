package com.prestutti.data.repository

import com.prestutti.data.local.LoanDao
import com.prestutti.data.local.entity.LoanEntity
import com.prestutti.domain.model.Loan
import com.prestutti.domain.model.LoanType
import com.prestutti.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class LoanRepositoryImpl @Inject constructor(
    private val dao: LoanDao
) : LoanRepository {

    override fun getLentLoans(): Flow<List<Loan>> =
        dao.getLentLoans().map { list -> list.map { it.toDomain() } }

    override fun getBorrowedLoans(): Flow<List<Loan>> =
        dao.getBorrowedLoans().map { list -> list.map { it.toDomain() } }

    override suspend fun saveLoan(loan: Loan): Long =
        dao.insertLoan(loan.toEntity())

    override suspend fun updateLoan(loan: Loan) =
        dao.updateLoan(loan.toEntity())

    override suspend fun deleteLoan(loan: Loan) =
        dao.deleteLoan(loan.toEntity())

    // ── Mappers ──────────────────────────────────────────────────────────────

    private fun LoanEntity.toDomain() = Loan(
        id          = id,
        personName  = personName,
        item        = item,
        description = description,
        category    = category,
        date        = Date(dateMillis),
        dueDate     = dueDateMillis?.let { Date(it) },
        isReturned  = isReturned,
        type        = LoanType.valueOf(type)
    )

    private fun Loan.toEntity() = LoanEntity(
        id            = id,
        personName    = personName,
        item          = item,
        description   = description,
        category      = category,
        dateMillis    = date.time,
        dueDateMillis = dueDate?.time,
        isReturned    = isReturned,
        type          = type.name
    )
}