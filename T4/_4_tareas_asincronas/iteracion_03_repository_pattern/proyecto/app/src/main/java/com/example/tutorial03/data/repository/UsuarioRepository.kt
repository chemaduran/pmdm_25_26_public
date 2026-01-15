package com.example.tutorial03.data.repository

import com.example.tutorial03.data.model.Usuario

/**
 * Contrato del repositorio de usuarios.
 * 
 * Usar una interface nos permite:
 * - Definir el contrato sin implementación
 * - Crear mocks fácilmente para testing
 * - Cambiar la implementación sin afectar al ViewModel
 * - Seguir el principio de Inversión de Dependencias (SOLID)
 */
interface UsuarioRepository {
    
    /**
     * Obtiene todos los usuarios.
     * 
     * @return Lista de usuarios
     * @throws Exception Si hay un error de red
     */
    suspend fun obtenerUsuarios(): List<Usuario>
    
    /**
     * Obtiene un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return El usuario o null si no existe
     */
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?
    
    /**
     * Busca usuarios por nombre.
     * 
     * @param query Texto a buscar
     * @return Lista de usuarios que coinciden
     */
    suspend fun buscarUsuarios(query: String): List<Usuario>
}
