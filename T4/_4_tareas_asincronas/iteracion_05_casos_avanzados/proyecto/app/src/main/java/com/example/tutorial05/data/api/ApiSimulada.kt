package com.example.tutorial05.data.api

import com.example.tutorial05.data.model.Producto
import com.example.tutorial05.data.model.Usuario
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Simulación de una API remota con errores frecuentes.
 * Diseñada para demostrar retry y timeout.
 */
object ApiSimulada {
    
    private val usuarios = listOf(
        Usuario(1, "Ana García", "ana@email.com"),
        Usuario(2, "Carlos López", "carlos@email.com"),
        Usuario(3, "María Fernández", "maria@email.com"),
        Usuario(4, "Pedro Martínez", "pedro@email.com"),
        Usuario(5, "Laura Sánchez", "laura@email.com")
    )
    
    private val productos = listOf(
        Producto(1, "Laptop HP", 899.99),
        Producto(2, "Mouse Logitech", 29.99),
        Producto(3, "Teclado Mecánico", 79.99),
        Producto(4, "Monitor 27\"", 349.99),
        Producto(5, "Webcam HD", 59.99)
    )
    
    // Contadores para simular errores
    private var contadorUsuarios = 0
    private var contadorProductos = 0
    
    /**
     * Obtiene usuarios con alta probabilidad de fallo (para probar retry).
     * Falla 70% de las veces.
     */
    suspend fun obtenerUsuariosInestable(): List<Usuario> {
        delay(1000) // Simula latencia
        
        contadorUsuarios++
        
        // 70% de probabilidad de fallo
        if (Random.nextFloat() < 0.7f) {
            throw Exception("Error de red simulado (intento #$contadorUsuarios)")
        }
        
        return usuarios
    }
    
    /**
     * Obtiene usuarios de forma más estable.
     * Falla solo 25% de las veces.
     */
    suspend fun obtenerUsuarios(): List<Usuario> {
        delay(1500)
        
        if (Random.nextFloat() < 0.25f) {
            throw Exception("Error de conexión esporádico")
        }
        
        return usuarios
    }
    
    /**
     * Obtiene productos (para operaciones paralelas).
     */
    suspend fun obtenerProductos(): List<Producto> {
        delay(1200)
        
        contadorProductos++
        
        if (contadorProductos % 5 == 0) {
            throw Exception("Error al obtener productos")
        }
        
        return productos
    }
    
    /**
     * Operación muy lenta (para probar timeout).
     */
    suspend fun operacionMuyLenta(): List<Usuario> {
        delay(15000) // 15 segundos
        return usuarios
    }
    
    /**
     * Busca usuarios por nombre.
     */
    suspend fun buscarUsuarios(query: String): List<Usuario> {
        delay(500)
        
        if (query.isBlank()) {
            return usuarios
        }
        
        return usuarios.filter { 
            it.nombre.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true)
        }
    }
    
    fun resetear() {
        contadorUsuarios = 0
        contadorProductos = 0
    }
}
