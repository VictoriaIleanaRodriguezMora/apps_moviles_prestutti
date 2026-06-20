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

// Implementacion real. traducción.
class LoanRepositoryImplementation @Inject constructor(
    private val dao: LoanDao // ← Hilt también inyecta esto. es una interfaz igual que LoanRepository
    // /data/local/LoanDao.kt
) : LoanRepository {

    // Room ejecuta el SQL
    override fun getLentLoans(): Flow<List<Loan>> = // Room emite la lista
        dao.getLentLoans().map { list -> list.map { it.toDomain() } } // LoanEntity → Loan
    // LentListViewModel recibe List<Loan>

    override fun getBorrowedLoans(): Flow<List<Loan>> =
        dao.getBorrowedLoans().map { list -> list.map { it.toDomain() } }

    override suspend fun saveLoan(loan: Loan): Long =
        dao.insertLoan(loan.toEntity()) // 1. convierte Loan → LoanEntity, 2. llama al DAO
    // loan.toEntity() es una función de extensión definida en el mismo archivo:

    override suspend fun updateLoan(loan: Loan) =
        dao.updateLoan(loan.toEntity())

    override suspend fun deleteLoan(loan: Loan) =
        dao.deleteLoan(loan.toEntity())

    // ── Mappers ──────────────────────────────────────────────────────────────
    // ¿Por qué hay que convertir? Porque Room solo entiende tipos simples como Long, String, Boolean. No entiende Date ni enums.

    // De Room → a la app (LoanEntity → Loan)
    private fun LoanEntity.toDomain() = Loan(
        id          = id,
        personName  = personName,
        item        = item,
        description = description,
        category    = category,
        date        = Date(dateMillis), // Long → Date
        dueDate     = dueDateMillis?.let { Date(it) },
        isReturned  = isReturned,
        type        = LoanType.valueOf(type) // String → enum
    )

    // De la app → Room (Loan → LoanEntity)
    private fun Loan.toEntity() = LoanEntity(
        id            = id,
        personName    = personName,
        item          = item,
        description   = description,
        category      = category,
        dateMillis    = date.time, // Date → Long
        dueDateMillis = dueDate?.time,
        isReturned    = isReturned,
        type          = type.name // enum → String
    )
}