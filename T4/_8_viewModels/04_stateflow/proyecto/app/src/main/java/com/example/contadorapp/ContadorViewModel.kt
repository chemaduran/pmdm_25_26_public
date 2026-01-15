package com.example.contadorapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel con StateFlow
 * 
 * StateFlow es la alternativa moderna a LiveData basada en Kotlin Flow.
 * Es especialmente útil cuando trabajas con Kotlin puro y coroutines.
 */
class ContadorViewModel : ViewModel() {
    
    // MutableStateFlow: se puede modificar (privado)
    // A diferencia de LiveData, StateFlow REQUIERE un valor inicial
    private val _contador = MutableStateFlow(0)
    
    // StateFlow: solo lectura (público)
    // asStateFlow() convierte MutableStateFlow a StateFlow inmutable
    val contador: StateFlow<Int> = _contador.asStateFlow()
    
    /**
     * Incrementa el contador en 1
     * Con StateFlow podemos usar operadores de incremento directamente
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
}
