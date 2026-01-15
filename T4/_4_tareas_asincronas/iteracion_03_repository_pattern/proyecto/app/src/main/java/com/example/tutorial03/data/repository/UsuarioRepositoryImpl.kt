package com.example.tutorial03.data.repository

import com.example.tutorial03.data.api.ApiSimulada
import com.example.tutorial03.data.model.Usuario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación del repositorio de usuarios.
 * 
 * El Repository:
 * - Abstrae la fuente de datos del ViewModel
 * - Maneja los Dispatchers internamente
 * - Puede combinar múltiples fuentes de datos
 * - Es el lugar correcto para implementar caché
 * 
 * @param api Fuente de datos remota
 * @param dispatcher Dispatcher para operaciones de IO (inyectable para testing)
 */
class UsuarioRepositoryImpl(
    private val api: ApiSimulada = ApiSimulada,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsuarioRepository {
    
    /**
     * Obtiene todos los usuarios desde la API.
     * 
     * Usa withContext para asegurar que la operación de red
     * se ejecute en el dispatcher de IO.
     */
    override suspend fun obtenerUsuarios(): List<Usuario> {
        return withContext(dispatcher) {
            api.obtenerUsuarios()
        }
    }
    
    /**
     * Obtiene un usuario por su ID.
     * 
     * En una aplicación real, podríamos tener un endpoint específico
     * para esto. Aquí filtramos de la lista por simplicidad.
     */
    override suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return withContext(dispatcher) {
            api.obtenerUsuarios().find { it.id == id }
        }
    }
    
    /**
     * Busca usuarios por nombre.
     */
    override suspend fun buscarUsuarios(query: String): List<Usuario> {
        return withContext(dispatcher) {
            if (query.isBlank()) {
                api.obtenerUsuarios()
            } else {
                api.buscarUsuarios(query)
            }
        }
    }
}
