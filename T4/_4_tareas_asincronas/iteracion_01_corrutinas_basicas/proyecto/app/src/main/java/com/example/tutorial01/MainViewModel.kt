package com.example.tutorial01

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal.
 * 
 * El ViewModel:
 * - Sobrevive a cambios de configuración (rotación de pantalla)
 * - Contiene la lógica de presentación
 * - Expone datos a la UI mediante LiveData
 * 
 * Usamos viewModelScope porque:
 * - Se cancela automáticamente cuando el ViewModel se destruye
 * - Evita memory leaks
 * - Evita crashes por actualizar UI después de destruir la Activity
 */
class MainViewModel : ViewModel() {
    
    // MutableLiveData privado - solo el ViewModel puede modificarlo
    private val _usuarios = MutableLiveData<List<Usuario>>()
    // LiveData público - la UI solo puede observarlo (no modificarlo)
    val usuarios: LiveData<List<Usuario>> = _usuarios
    
    // Estado de carga
    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> = _cargando
    
    /**
     * Carga los usuarios desde la API simulada.
     * 
     * Este método:
     * 1. Muestra el indicador de carga
     * 2. Llama a la API (que tarda 2 segundos)
     * 3. Actualiza la lista de usuarios
     * 4. Oculta el indicador de carga
     */
    fun cargarUsuarios() {
        // viewModelScope.launch inicia una corrutina
        // Esta corrutina se ejecuta en el Main Thread por defecto
        // pero las suspend functions internas pueden cambiar de hilo
        viewModelScope.launch {
            // Mostramos el indicador de carga
            _cargando.value = true
            
            // Llamamos a la API (suspend function)
            // Esta línea NO bloquea el Main Thread
            // La corrutina se "pausa" mientras espera
            val listaUsuarios = ApiSimulada.obtenerUsuarios()
            
            // Cuando la API responde, la corrutina se "reanuda"
            // y continuamos en el Main Thread
            _usuarios.value = listaUsuarios
            
            // Ocultamos el indicador de carga
            _cargando.value = false
        }
    }
}
