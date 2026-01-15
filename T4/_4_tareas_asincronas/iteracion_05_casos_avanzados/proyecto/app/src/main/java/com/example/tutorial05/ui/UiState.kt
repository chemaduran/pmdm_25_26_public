package com.example.tutorial05.ui

/**
 * Representa los posibles estados de la UI.
 */
sealed class UiState<out T> {
    
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    
    /**
     * Estado de carga con información adicional.
     * Útil para mostrar progreso de reintentos.
     */
    data class LoadingConInfo(val mensaje: String) : UiState<Nothing>()
    
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val mensaje: String) : UiState<Nothing>()
}
