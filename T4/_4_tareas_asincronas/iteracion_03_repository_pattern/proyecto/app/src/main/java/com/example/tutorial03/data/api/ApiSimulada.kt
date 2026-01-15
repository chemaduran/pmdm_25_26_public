package com.example.tutorial03.data.api

import com.example.tutorial03.data.model.Usuario
import kotlinx.coroutines.delay

/**
 * Simulación de una API remota.
 * 
 * En una aplicación real, aquí usaríamos Retrofit.
 */
object ApiSimulada {
    
    private var contadorLlamadas = 0
    
    /**
     * Simula la obtención de usuarios desde un servidor.
     * 
     * @return Lista de usuarios
     * @throws Exception Cada 3 llamadas para simular errores
     */
    suspend fun obtenerUsuarios(): List<Usuario> {
        delay(2000) // Simulamos latencia de red
        
        contadorLlamadas++
        
        // Simulamos un error cada 3 llamadas
        if (contadorLlamadas % 3 == 0) {
            throw Exception("Error de conexión: No se pudo conectar con el servidor")
        }
        
        return listOf(
            Usuario(1, "Ana García", "ana@email.com"),
            Usuario(2, "Carlos López", "carlos@email.com"),
            Usuario(3, "María Fernández", "maria@email.com"),
            Usuario(4, "Pedro Martínez", "pedro@email.com"),
            Usuario(5, "Laura Sánchez", "laura@email.com")
        )
    }
    
    /**
     * Busca usuarios por nombre.
     * 
     * @param query Texto a buscar en el nombre
     * @return Lista de usuarios que coinciden
     */
    suspend fun buscarUsuarios(query: String): List<Usuario> {
        delay(1000) // Simulamos latencia
        
        return obtenerUsuarios().filter { usuario ->
            usuario.nombre.contains(query, ignoreCase = true)
        }
    }
    
    fun resetear() {
        contadorLlamadas = 0
    }
}
