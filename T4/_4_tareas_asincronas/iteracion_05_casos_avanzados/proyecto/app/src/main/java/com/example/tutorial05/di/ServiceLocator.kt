package com.example.tutorial05.di

import com.example.tutorial05.data.api.ApiSimulada
import com.example.tutorial05.data.repository.DataRepository
import com.example.tutorial05.data.repository.DataRepositoryImpl

/**
 * Service Locator para proveer dependencias.
 */
object ServiceLocator {
    
    private val api: ApiSimulada by lazy { ApiSimulada }
    
    val dataRepository: DataRepository by lazy {
        DataRepositoryImpl(api)
    }
    
    fun reset() {
        api.resetear()
    }
}
