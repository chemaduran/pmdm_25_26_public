package com.example.tutorial04.data.repository

import com.example.tutorial04.data.model.Usuario

/**
 * Contrato del repositorio de usuarios.
 */
interface UsuarioRepository {
    suspend fun obtenerUsuarios(): List<Usuario>
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?
    suspend fun buscarUsuarios(query: String): List<Usuario>
}
