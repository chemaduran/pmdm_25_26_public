package com.example.tutorial02

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal.
 * 
 * En esta iteración, usamos UiState para representar todos los
 * estados posibles de la UI en un único LiveData.
 * 
 * Ventajas:
 * - Un único punto de verdad para el estado
 * - Estados mutuamente exclusivos (no puede estar loading Y success a la vez)
 * - Fácil de testear
 */
class MainViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "MainViewModel"
    }
    
    // Un único LiveData para todo el estado de la UI
    private val _uiState = MutableLiveData<UiState<List<Usuario>>>()
    val uiState: LiveData<UiState<List<Usuario>>> = _uiState
    
    init {
        // Establecemos el estado inicial
        _uiState.value = UiState.Idle
    }
    
    /**
     * Carga los usuarios desde la API.
     * 
     * El flujo es:
     * 1. Idle/Success/Error → Loading
     * 2. Loading → Success (si todo va bien)
     * 3. Loading → Error (si hay un problema)
     */
    fun cargarUsuarios() {
        viewModelScope.launch {
            // 1. Cambiamos a estado de carga
            _uiState.value = UiState.Loading
            
            try {
                // 2. Intentamos obtener los datos
                val usuarios = ApiSimulada.obtenerUsuarios()
                
                // 3a. Éxito: actualizamos con los datos
                Log.d(TAG, "Usuarios cargados correctamente: ${usuarios.size}")
                _uiState.value = UiState.Success(usuarios)
                
            } catch (e: Exception) {
                // 3b. Error: registramos y mostramos el error
                Log.e(TAG, "Error al cargar usuarios", e)
                _uiState.value = UiState.Error(
                    e.message ?: "Ha ocurrido un error desconocido"
                )
            }
        }
    }
}
