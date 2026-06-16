package com.prestutti.data.remote.dto

// DTO significa "Data Transfer Object" (Objeto de Transferencia de Datos).
// Los nombres de estas variables (id, name, email) deben ser EXACTAMENTE iguales
// a los que manda la API de JSONPlaceholder en internet.
data class ContactDto(
    val id: Int,
    val name: String,
    val email: String
)