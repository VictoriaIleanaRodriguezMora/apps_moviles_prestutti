package com.prestutti.data.local

import androidx.room.*
import com.prestutti.data.local.entity.LoanEntity
import kotlinx.coroutines.flow.Flow

// Las consultas SQL
// Flow es clave: Room devuelve un "stream" de datos. Cada vez que alguien inserta o borra un préstamo, Room notifica automáticamente y la UI se actualiza sola.
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
    // solo se escribe la firma. Room genera el código real en tiempo de compilación. Room lee la anotación @Insert y escribe automáticamente el SQL.

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)
}
