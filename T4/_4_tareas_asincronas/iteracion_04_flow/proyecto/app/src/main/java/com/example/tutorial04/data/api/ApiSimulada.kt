package com.example.tutorial04.data.api

import com.example.tutorial04.data.model.Usuario
import kotlinx.coroutines.delay

/**
 * Simulación de una API remota.
 */
object ApiSimulada {
    
    private val usuarios = listOf(
        Usuario(1, "Ana García", "ana@email.com"),
        Usuario(2, "Carlos López", "carlos@email.com"),
        Usuario(3, "María Fernández", "maria@email.com"),
        Usuario(4, "Pedro Martínez", "pedro@email.com"),
        Usuario(5, "Laura Sánchez", "laura@email.com"),
        Usuario(6, "Antonio Ruiz", "antonio@email.com"),
        Usuario(7, "Carmen Díaz", "carmen@email.com"),
        Usuario(8, "Francisco Torres", "francisco@email.com")
    )
    
    private var contadorLlamadas = 0
    
    suspend fun obtenerUsuarios(): List<Usuario> {
        delay(1500)
        
        contadorLlamadas++
        if (contadorLlamadas % 4 == 0) {
            throw Exception("Error de conexión simulado")
        }
        
        return usuarios
    }
    
    suspend fun buscarUsuarios(query: String): List<Usuario> {
        delay(800)
        
        if (query.isBlank()) {
            return usuarios
        }
        
        return usuarios.filter { usuario ->
            usuario.nombre.contains(query, ignoreCase = true) ||
            usuario.email.contains(query, ignoreCase = true)
        }
    }
    
    fun resetear() {
        contadorLlamadas = 0
    }
}
