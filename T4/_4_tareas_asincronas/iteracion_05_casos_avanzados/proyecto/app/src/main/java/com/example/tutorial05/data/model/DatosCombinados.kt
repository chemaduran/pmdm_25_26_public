package com.example.tutorial05.data.model

/**
 * Datos combinados de usuarios y productos.
 * Resultado de una operación paralela.
 */
data class DatosCombinados(
    val usuarios: List<Usuario>,
    val productos: List<Producto>,
    val tiempoCargaMs: Long = 0
)
