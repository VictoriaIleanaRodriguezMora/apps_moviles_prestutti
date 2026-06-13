package com.prestutti.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val item: String,
    val description: String = "",
    val category: String? = null,
    val dateMillis: Long,
    val dueDateMillis: Long? = null,
    val isReturned: Boolean = false,
    val type: String   // "LENT" | "BORROWED"
)