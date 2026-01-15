package com.example.tutorial05.data.repository

import com.example.tutorial05.data.model.DatosCombinados
import com.example.tutorial05.data.model.Producto
import com.example.tutorial05.data.model.Usuario

/**
 * Contrato del repositorio de datos.
 */
interface DataRepository {
    
    /** Obtiene usuarios de forma normal */
    suspend fun obtenerUsuarios(): List<Usuario>
    
    /** Obtiene usuarios con reintentos automáticos */
    suspend fun obtenerUsuariosConRetry(): List<Usuario>
    
    /** Obtiene usuarios con timeout */
    suspend fun obtenerUsuariosConTimeout(timeoutMs: Long): List<Usuario>
    
    /** Obtiene productos */
    suspend fun obtenerProductos(): List<Producto>
    
    /** Obtiene usuarios y productos en paralelo */
    suspend fun obtenerDatosEnParalelo(): DatosCombinados
    
    /** Busca usuarios */
    suspend fun buscarUsuarios(query: String): List<Usuario>
}
