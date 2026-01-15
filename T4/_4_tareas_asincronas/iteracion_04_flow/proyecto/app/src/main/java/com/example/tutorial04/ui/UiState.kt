package com.example.tutorial04.ui

/**
 * Representa los posibles estados de la UI.
 */
sealed class UiState<out T> {
    
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val mensaje: String) : UiState<Nothing>()
}
