package com.example.tutorial04.di

import com.example.tutorial04.data.api.ApiSimulada
import com.example.tutorial04.data.repository.UsuarioRepository
import com.example.tutorial04.data.repository.UsuarioRepositoryImpl

/**
 * Service Locator simple para proveer dependencias.
 */
object ServiceLocator {
    
    private val api: ApiSimulada by lazy { ApiSimulada }
    
    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepositoryImpl(api)
    }
    
    fun reset() {
        api.resetear()
    }
}
