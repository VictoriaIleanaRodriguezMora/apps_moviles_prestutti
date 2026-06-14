package com.prestutti.domain.model

import java.util.Date

enum class LoanType { LENT, BORROWED }

data class Loan(
    val id: Long = 0,
    val personName: String, // ¿A quién se lo prestaste?
    val item: String, // ¿Qué prestaste?
    val description: String = "",
    val category: String? = null,
    val date: Date = Date(),
    val dueDate: Date? = null,
    val isReturned: Boolean = false,
    val type: LoanType   // LENT = "Presté" | BORROWED = "Me prestaron"
)