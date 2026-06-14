package com.prestutti.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// La tabla de la BD en Room
@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val item: String,
    val description: String = "",
    val category: String? = null,
    val dateMillis: Long, // ← Room no entiende "Date", guarda Long
    val dueDateMillis: Long? = null,
    val isReturned: Boolean = false,
    val type: String   // "LENT" | "BORROWED". Room no entiende "enum", guarda String
)
// Room solo entiende tipos simples. Por eso Date se convierte a Long (milisegundos) y LoanType a String.