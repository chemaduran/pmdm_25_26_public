package com.example.contadorapp

import androidx.lifecycle.ViewModel

/**
 * ViewModel que almacena el estado del contador.
 * 
 * Esta clase sobrevive a los cambios de configuración (rotación),
 * por lo que el valor del contador no se pierde.
 */
class ContadorViewModel : ViewModel() {
    
    // El contador ahora vive en el ViewModel, no en la Activity
    // private set = solo se puede modificar desde dentro del ViewModel
    var contador = 0
        private set
    
    /**
     * Incrementa el contador en 1
     */
    fun incrementar() {
        contador++
    }
    
    /**
     * Decrementa el contador en 1
     */
    fun decrementar() {
        contador--
    }
}
