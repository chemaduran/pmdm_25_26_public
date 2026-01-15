package com.example.tutorial03.di

import com.example.tutorial03.data.api.ApiSimulada
import com.example.tutorial03.data.repository.UsuarioRepository
import com.example.tutorial03.data.repository.UsuarioRepositoryImpl

/**
 * Service Locator simple para proveer dependencias.
 * 
 * En una aplicación real, usaríamos Hilt o Dagger para
 * inyección de dependencias más robusta.
 * 
 * Este enfoque es suficiente para:
 * - Aplicaciones pequeñas
 * - Aprendizaje
 * - Prototipos
 * 
 * Ventajas:
 * - Simple y fácil de entender
 * - No requiere librerías externas
 * - Permite testing básico
 * 
 * Desventajas:
 * - No es tan flexible como Hilt
 * - Singleton global (cuidado con memoria)
 * - Más difícil de testear que con DI real
 */
object ServiceLocator {
    
    // Lazy initialization: se crea cuando se necesita por primera vez
    private val api: ApiSimulada by lazy { 
        ApiSimulada 
    }
    
    // El repository también se crea lazy
    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepositoryImpl(api)
    }
    
    /**
     * Resetea el Service Locator.
     * Útil para testing.
     */
    fun reset() {
        api.resetear()
    }
}
