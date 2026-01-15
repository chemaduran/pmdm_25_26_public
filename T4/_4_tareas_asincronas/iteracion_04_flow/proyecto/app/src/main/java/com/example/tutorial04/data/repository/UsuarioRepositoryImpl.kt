package com.example.tutorial04.data.repository

import com.example.tutorial04.data.api.ApiSimulada
import com.example.tutorial04.data.model.Usuario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación del repositorio de usuarios.
 */
class UsuarioRepositoryImpl(
    private val api: ApiSimulada = ApiSimulada,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsuarioRepository {
    
    override suspend fun obtenerUsuarios(): List<Usuario> {
        return withContext(dispatcher) {
            api.obtenerUsuarios()
        }
    }
    
    override suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return withContext(dispatcher) {
            api.obtenerUsuarios().find { it.id == id }
        }
    }
    
    override suspend fun buscarUsuarios(query: String): List<Usuario> {
        return withContext(dispatcher) {
            api.buscarUsuarios(query)
        }
    }
}
