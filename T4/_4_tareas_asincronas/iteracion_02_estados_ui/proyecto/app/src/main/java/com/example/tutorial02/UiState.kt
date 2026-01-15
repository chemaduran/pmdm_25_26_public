package com.example.tutorial02

/**
 * Representa los posibles estados de la UI.
 * 
 * Usamos sealed class porque:
 * - Define un conjunto CERRADO de estados posibles
 * - El compilador nos obliga a manejar todos los casos en un when
 * - Cada estado puede contener datos diferentes
 * 
 * @param T El tipo de datos en caso de éxito
 */
sealed class UiState<out T> {
    
    /**
     * Estado inicial o inactivo.
     * No hay operación en curso, la UI está en reposo.
     */
    data object Idle : UiState<Nothing>()
    
    /**
     * Operación en curso.
     * Se debe mostrar un indicador de carga al usuario.
     */
    data object Loading : UiState<Nothing>()
    
    /**
     * Operación completada con éxito.
     * @param data Los datos obtenidos de la operación
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Operación fallida.
     * @param mensaje Descripción del error para mostrar al usuario
     */
    data class Error(val mensaje: String) : UiState<Nothing>()
}
