package com.prestutti.domain.model

import java.util.Date

enum class LoanType { LENT, BORROWED }

data class Loan(
    val id: Long = 0,
    val personName: String,
    val item: String,
    val description: String = "",
    val category: String? = null,
    val date: Date = Date(),
    val dueDate: Date? = null,
    val isReturned: Boolean = false,
    val type: LoanType   // LENT = "Presté" | BORROWED = "Me prestaron"
)