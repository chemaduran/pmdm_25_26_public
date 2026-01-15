package com.example.tutorial03.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorial03.data.model.Usuario
import com.example.tutorial03.data.repository.UsuarioRepository
import com.example.tutorial03.di.ServiceLocator
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal.
 * 
 * Ahora el ViewModel:
 * - NO conoce la implementación del repository
 * - NO sabe de Dispatchers (el repository los maneja)
 * - Solo conoce la abstracción (interface)
 * 
 * Esto hace el código más limpio y testeable.
 * 
 * @param repository Repositorio de usuarios (inyectable para testing)
 */
class MainViewModel(
    private val repository: UsuarioRepository = ServiceLocator.usuarioRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "MainViewModel"
    }
    
    private val _uiState = MutableLiveData<UiState<List<Usuario>>>()
    val uiState: LiveData<UiState<List<Usuario>>> = _uiState
    
    init {
        _uiState.value = UiState.Idle
    }
    
    /**
     * Carga todos los usuarios.
     * 
     * El ViewModel no sabe:
     * - De dónde vienen los datos (API, BD, caché)
     * - Qué Dispatcher usar
     * - Cómo manejar la red
     * 
     * Solo sabe que puede pedir usuarios al repository.
     */
    fun cargarUsuarios() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                val usuarios = repository.obtenerUsuarios()
                Log.d(TAG, "Usuarios cargados: ${usuarios.size}")
                _uiState.value = UiState.Success(usuarios)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar usuarios", e)
                _uiState.value = UiState.Error(
                    e.message ?: "Error desconocido"
                )
            }
        }
    }
    
    /**
     * Busca usuarios por nombre.
     * 
     * @param query Texto a buscar
     */
    fun buscarUsuarios(query: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                val usuarios = repository.buscarUsuarios(query)
                
                if (usuarios.isEmpty()) {
                    _uiState.value = UiState.Success(emptyList())
                } else {
                    _uiState.value = UiState.Success(usuarios)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al buscar usuarios", e)
                _uiState.value = UiState.Error(
                    e.message ?: "Error desconocido"
                )
            }
        }
    }
}
