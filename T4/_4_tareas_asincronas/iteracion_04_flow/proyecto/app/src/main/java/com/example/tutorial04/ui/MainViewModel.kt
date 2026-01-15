package com.example.tutorial04.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorial04.data.model.Usuario
import com.example.tutorial04.data.repository.UsuarioRepository
import com.example.tutorial04.di.ServiceLocator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * ViewModel que usa StateFlow en lugar de LiveData.
 * 
 * Ventajas de StateFlow:
 * - Siempre tiene un valor (no nullable)
 * - Operadores de Flow (debounce, map, filter...)
 * - Mejor para testing
 * - Kotlin puro (no depende de Android)
 */
@OptIn(FlowPreview::class)
class MainViewModel(
    private val repository: UsuarioRepository = ServiceLocator.usuarioRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "MainViewModel"
        private const val DEBOUNCE_TIEMPO_MS = 300L
    }
    
    // StateFlow para el estado de la UI
    private val _uiState = MutableStateFlow<UiState<List<Usuario>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Usuario>>> = _uiState.asStateFlow()
    
    // StateFlow para la query de búsqueda
    private val _queryBusqueda = MutableStateFlow("")
    val queryBusqueda: StateFlow<String> = _queryBusqueda.asStateFlow()
    
    init {
        // Configuramos la búsqueda reactiva con debounce
        configurarBusquedaReactiva()
    }
    
    /**
     * Configura la búsqueda reactiva.
     * 
     * El flow de búsqueda:
     * 1. Espera 300ms después del último cambio (debounce)
     * 2. Solo procesa si el valor cambió (distinctUntilChanged)
     * 3. Realiza la búsqueda
     * 
     * Esto evita hacer muchas llamadas mientras el usuario escribe.
     */
    private fun configurarBusquedaReactiva() {
        viewModelScope.launch {
            _queryBusqueda
                // Espera 300ms sin nuevos valores antes de continuar
                .debounce(DEBOUNCE_TIEMPO_MS)
                // Solo continúa si el valor es diferente al anterior
                .distinctUntilChanged()
                // Colectamos y ejecutamos la búsqueda
                .collect { query ->
                    Log.d(TAG, "Buscando: '$query'")
                    ejecutarBusqueda(query)
                }
        }
    }
    
    /**
     * Actualiza la query de búsqueda.
     * El debounce se encarga de no hacer búsquedas innecesarias.
     */
    fun actualizarBusqueda(query: String) {
        _queryBusqueda.value = query
    }
    
    /**
     * Carga todos los usuarios (sin filtro).
     */
    fun cargarTodosLosUsuarios() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                val usuarios = repository.obtenerUsuarios()
                _uiState.value = UiState.Success(usuarios)
                Log.d(TAG, "Cargados ${usuarios.size} usuarios")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar usuarios", e)
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
    
    /**
     * Ejecuta la búsqueda de usuarios.
     */
    private suspend fun ejecutarBusqueda(query: String) {
        _uiState.value = UiState.Loading
        
        try {
            val usuarios = repository.buscarUsuarios(query)
            
            if (usuarios.isEmpty() && query.isNotBlank()) {
                _uiState.value = UiState.Success(emptyList())
            } else {
                _uiState.value = UiState.Success(usuarios)
            }
            
            Log.d(TAG, "Encontrados ${usuarios.size} usuarios para '$query'")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al buscar usuarios", e)
            _uiState.value = UiState.Error(e.message ?: "Error desconocido")
        }
    }
}
