package com.example.tutorial05.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorial05.data.repository.DataRepository
import com.example.tutorial05.di.ServiceLocator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel con ejemplos de casos avanzados de corrutinas:
 * - Retry (reintentos automáticos)
 * - Timeout
 * - Operaciones paralelas
 * - Cancelación
 */
class MainViewModel(
    private val repository: DataRepository = ServiceLocator.dataRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "MainViewModel"
    }
    
    private val _uiState = MutableStateFlow<UiState<DatosUI>>(UiState.Idle)
    val uiState: StateFlow<UiState<DatosUI>> = _uiState.asStateFlow()
    
    // Job para poder cancelar la operación actual
    private var jobActual: Job? = null
    
    /**
     * Carga básica (puede fallar).
     */
    fun cargarNormal() {
        cancelarOperacionActual()
        
        jobActual = viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                val usuarios = repository.obtenerUsuarios()
                _uiState.value = UiState.Success(DatosUI.Usuarios(usuarios))
                Log.d(TAG, "Carga normal exitosa: ${usuarios.size} usuarios")
                
            } catch (e: CancellationException) {
                Log.d(TAG, "Carga normal cancelada")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error en carga normal", e)
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
    
    /**
     * Carga con reintentos automáticos.
     * Si falla, reintenta hasta 3 veces con backoff exponencial.
     */
    fun cargarConRetry() {
        cancelarOperacionActual()
        
        jobActual = viewModelScope.launch {
            _uiState.value = UiState.LoadingConInfo("Cargando con reintentos automáticos...")
            
            try {
                val usuarios = repository.obtenerUsuariosConRetry()
                _uiState.value = UiState.Success(DatosUI.Usuarios(usuarios))
                Log.d(TAG, "Carga con retry exitosa después de reintentos")
                
            } catch (e: CancellationException) {
                Log.d(TAG, "Carga con retry cancelada")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error después de todos los reintentos", e)
                _uiState.value = UiState.Error(
                    "Falló después de varios intentos: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Carga con timeout.
     * Falla si la operación tarda más de 3 segundos.
     */
    fun cargarConTimeout() {
        cancelarOperacionActual()
        
        jobActual = viewModelScope.launch {
            _uiState.value = UiState.LoadingConInfo("Cargando (timeout: 3s)...")
            
            try {
                // Este método intenta una operación que tarda 15 segundos
                // pero el timeout está en 3 segundos, así que fallará
                val usuarios = repository.obtenerUsuariosConTimeout(3000)
                _uiState.value = UiState.Success(DatosUI.Usuarios(usuarios))
                
            } catch (e: CancellationException) {
                Log.d(TAG, "Carga con timeout cancelada")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Timeout o error", e)
                _uiState.value = UiState.Error(e.message ?: "Timeout")
            }
        }
    }
    
    /**
     * Carga usuarios y productos en paralelo.
     * Más eficiente que cargar secuencialmente.
     */
    fun cargarEnParalelo() {
        cancelarOperacionActual()
        
        jobActual = viewModelScope.launch {
            _uiState.value = UiState.LoadingConInfo("Cargando en paralelo...")
            
            val tiempoInicio = System.currentTimeMillis()
            
            try {
                val datos = repository.obtenerDatosEnParalelo()
                val tiempoTotal = System.currentTimeMillis() - tiempoInicio
                
                Log.d(TAG, "Carga paralela completada en ${tiempoTotal}ms")
                Log.d(TAG, "Usuarios: ${datos.usuarios.size}, Productos: ${datos.productos.size}")
                
                _uiState.value = UiState.Success(
                    DatosUI.DatosMixtos(datos.copy(tiempoCargaMs = tiempoTotal))
                )
                
            } catch (e: CancellationException) {
                Log.d(TAG, "Carga paralela cancelada")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error en carga paralela", e)
                _uiState.value = UiState.Error(e.message ?: "Error en carga paralela")
            }
        }
    }
    
    /**
     * Cancela la operación actual.
     */
    fun cancelarOperacionActual() {
        jobActual?.let { job ->
            if (job.isActive) {
                job.cancel()
                Log.d(TAG, "Operación cancelada por el usuario")
            }
        }
        jobActual = null
    }
    
    /**
     * Cancela y resetea al estado inicial.
     */
    fun cancelarYResetear() {
        cancelarOperacionActual()
        _uiState.value = UiState.Idle
    }
    
    override fun onCleared() {
        super.onCleared()
        cancelarOperacionActual()
    }
}
