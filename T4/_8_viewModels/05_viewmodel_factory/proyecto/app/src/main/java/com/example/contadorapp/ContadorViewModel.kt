package com.example.contadorapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel que acepta un valor inicial como parámetro
 * 
 * Para poder crear ViewModels con parámetros, necesitamos usar una Factory.
 * La Factory le indica al sistema cómo crear instancias del ViewModel.
 */
class ContadorViewModel(valorInicial: Int) : ViewModel() {
    
    // El contador se inicializa con el valor pasado por parámetro
    private val _contador = MutableStateFlow(valorInicial)
    val contador: StateFlow<Int> = _contador.asStateFlow()
    
    /**
     * Incrementa el contador en 1
     */
    fun incrementar() {
        _contador.value++
    }
    
    /**
     * Decrementa el contador en 1
     */
    fun decrementar() {
        _contador.value--
    }
    
    /**
     * Factory para crear el ViewModel con parámetros.
     * 
     * Implementa ViewModelProvider.Factory y sobrescribe create()
     * para proporcionar la lógica de creación del ViewModel.
     */
    class Factory(private val valorInicial: Int) : ViewModelProvider.Factory {
        
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Verificamos que el modelo solicitado sea nuestro ViewModel
            if (modelClass.isAssignableFrom(ContadorViewModel::class.java)) {
                // Creamos y devolvemos el ViewModel con el parámetro
                return ContadorViewModel(valorInicial) as T
            }
            // Si no es el ViewModel esperado, lanzamos una excepción
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
