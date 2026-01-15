package com.example.tutorial01

/**
 * Modelo de datos que representa un Usuario.
 * 
 * Usamos una data class porque:
 * - Genera automáticamente equals(), hashCode(), toString(), copy()
 * - Es inmutable (val en lugar de var)
 * - Es más segura y predecible
 */
data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String
)
