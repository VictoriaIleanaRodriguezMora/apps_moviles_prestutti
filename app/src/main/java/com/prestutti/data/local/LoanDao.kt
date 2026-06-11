package com.prestutti.data.local

import androidx.room.*
import com.prestutti.data.local.entity.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Query("SELECT * FROM loans WHERE type = 'LENT' ORDER BY dateMillis DESC")
    fun getLentLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE type = 'BORROWED' ORDER BY dateMillis DESC")
    fun getBorrowedLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getLoanById(id: Long): LoanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)
}
