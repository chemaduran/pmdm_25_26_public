package com.example.tutorial03.ui

/**
 * Representa los posibles estados de la UI.
 * 
 * @param T El tipo de datos en caso de éxito
 */
sealed class UiState<out T> {
    
    /** Estado inicial o inactivo */
    data object Idle : UiState<Nothing>()
    
    /** Operación en curso */
    data object Loading : UiState<Nothing>()
    
    /** Operación completada con éxito */
    data class Success<T>(val data: T) : UiState<T>()
    
    /** Operación fallida */
    data class Error(val mensaje: String) : UiState<Nothing>()
}
