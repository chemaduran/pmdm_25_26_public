package com.example.contadorapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel con LiveData
 * 
 * Ahora el estado es observable, la UI se actualiza automáticamente
 * cuando cambia el valor del contador.
 */
class ContadorViewModel : ViewModel() {
    
    // MutableLiveData: se puede modificar desde el ViewModel (privado)
    // El valor inicial es 0
    private val _contador = MutableLiveData(0)
    
    // LiveData: solo lectura desde fuera del ViewModel (público)
    // Este es el que exponemos a la Activity
    val contador: LiveData<Int> = _contador
    
    /**
     * Incrementa el contador en 1
     */
    fun incrementar() {
        // value puede ser null, por eso usamos el operador Elvis (?:)
        _contador.value = (_contador.value ?: 0) + 1
    }
    
    /**
     * Decrementa el contador en 1
     */
    fun decrementar() {
        _contador.value = (_contador.value ?: 0) - 1
    }
}
