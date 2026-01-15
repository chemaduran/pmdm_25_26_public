package com.example.tutorial02

import kotlinx.coroutines.delay

/**
 * Simulación de una API remota con posibles errores.
 * 
 * Esta versión simula errores de red para demostrar
 * el manejo de errores en la aplicación.
 */
object ApiSimulada {
    
    // Contador para simular errores periódicos
    private var contadorLlamadas = 0
    
    /**
     * Simula la obtención de usuarios desde un servidor.
     * 
     * IMPORTANTE: Esta función puede lanzar excepciones para simular
     * errores de red. El código que la llame debe manejar estos errores.
     * 
     * @return Lista de usuarios "obtenidos del servidor"
     * @throws Exception Cada 3 llamadas para simular errores de red
     */
    suspend fun obtenerUsuarios(): List<Usuario> {
        // Simulamos latencia de red (2 segundos)
        delay(2000)
        
        contadorLlamadas++
        
        // Simulamos un error cada 3 llamadas
        // Esto nos permite probar el manejo de errores
        if (contadorLlamadas % 3 == 0) {
            throw Exception("Error de conexión: No se pudo conectar con el servidor")
        }
        
        // Devolvemos datos simulados
        return listOf(
            Usuario(1, "Ana García", "ana@email.com"),
            Usuario(2, "Carlos López", "carlos@email.com"),
            Usuario(3, "María Fernández", "maria@email.com"),
            Usuario(4, "Pedro Martínez", "pedro@email.com"),
            Usuario(5, "Laura Sánchez", "laura@email.com")
        )
    }
    
    /**
     * Resetea el contador de llamadas.
     * Útil para testing.
     */
    fun resetear() {
        contadorLlamadas = 0
    }
}
