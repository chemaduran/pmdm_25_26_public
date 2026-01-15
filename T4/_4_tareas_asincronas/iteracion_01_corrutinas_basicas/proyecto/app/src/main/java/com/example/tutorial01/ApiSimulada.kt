package com.example.tutorial01

import kotlinx.coroutines.delay

/**
 * Simulación de una API remota.
 * 
 * En una aplicación real, aquí usaríamos Retrofit u otra librería
 * para hacer llamadas HTTP a un servidor.
 * 
 * Usamos 'object' para crear un Singleton (una única instancia).
 */
object ApiSimulada {
    
    /**
     * Simula la obtención de usuarios desde un servidor.
     * 
     * Esta función es 'suspend' porque:
     * - Realiza una operación que tarda tiempo (simulada con delay)
     * - Puede pausarse sin bloquear el hilo
     * - Solo puede ser llamada desde una corrutina u otra suspend function
     * 
     * @return Lista de usuarios "obtenidos del servidor"
     */
    suspend fun obtenerUsuarios(): List<Usuario> {
        // Simulamos latencia de red (2 segundos)
        // delay() es una suspend function que pausa la corrutina
        // SIN bloquear el hilo
        delay(2000)
        
        // Devolvemos datos simulados
        return listOf(
            Usuario(1, "Ana García", "ana@email.com"),
            Usuario(2, "Carlos López", "carlos@email.com"),
            Usuario(3, "María Fernández", "maria@email.com"),
            Usuario(4, "Pedro Martínez", "pedro@email.com"),
            Usuario(5, "Laura Sánchez", "laura@email.com")
        )
    }
}
