package com.example.tutorial05.data.repository

import com.example.tutorial05.data.api.ApiSimulada
import com.example.tutorial05.data.model.DatosCombinados
import com.example.tutorial05.data.model.Producto
import com.example.tutorial05.data.model.Usuario
import com.example.tutorial05.util.CoroutineUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

/**
 * Implementación del repositorio con técnicas avanzadas:
 * - Retry con backoff exponencial
 * - Timeout
 * - Operaciones paralelas
 */
class DataRepositoryImpl(
    private val api: ApiSimulada = ApiSimulada,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataRepository {
    
    companion object {
        private const val TIMEOUT_DEFAULT_MS = 10_000L
        private const val MAX_REINTENTOS = 3
        private const val DELAY_INICIAL_MS = 1000L
    }
    
    override suspend fun obtenerUsuarios(): List<Usuario> {
        return withContext(dispatcher) {
            api.obtenerUsuarios()
        }
    }
    
    /**
     * Obtiene usuarios con reintentos automáticos.
     * Si la primera llamada falla, reintenta hasta 3 veces con backoff exponencial.
     */
    override suspend fun obtenerUsuariosConRetry(): List<Usuario> {
        return withContext(dispatcher) {
            CoroutineUtils.conReintentos(
                intentos = MAX_REINTENTOS,
                delayInicial = DELAY_INICIAL_MS,
                onReintento = { intento, error ->
                    // Aquí podríamos emitir un evento para mostrar en UI
                    // Por ahora solo logueamos
                }
            ) {
                api.obtenerUsuariosInestable()
            }
        }
    }
    
    /**
     * Obtiene usuarios con timeout.
     * Si la operación tarda más del tiempo especificado, lanza excepción.
     */
    override suspend fun obtenerUsuariosConTimeout(timeoutMs: Long): List<Usuario> {
        return withContext(dispatcher) {
            CoroutineUtils.conTimeout(
                tiempoMs = timeoutMs,
                mensajeError = "Timeout al obtener usuarios"
            ) {
                api.operacionMuyLenta()
            }
        }
    }
    
    override suspend fun obtenerProductos(): List<Producto> {
        return withContext(dispatcher) {
            api.obtenerProductos()
        }
    }
    
    /**
     * Obtiene usuarios y productos en paralelo.
     * 
     * Esto es más eficiente que hacerlo secuencialmente:
     * - Secuencial: tiempo = tiempoUsuarios + tiempoProductos
     * - Paralelo: tiempo = max(tiempoUsuarios, tiempoProductos)
     */
    override suspend fun obtenerDatosEnParalelo(): DatosCombinados {
        return withContext(dispatcher) {
            var tiempoCarga: Long = 0
            
            val resultado = measureTimeMillis {
                // async lanza la corrutina inmediatamente
                val deferredUsuarios = async { api.obtenerUsuarios() }
                val deferredProductos = async { api.obtenerProductos() }
                
                // await espera los resultados
                val usuarios = deferredUsuarios.await()
                val productos = deferredProductos.await()
                
                DatosCombinados(usuarios, productos)
            }.let { tiempo ->
                tiempoCarga = tiempo
                
                // Volvemos a ejecutar para obtener los datos reales
                val deferredUsuarios = async { api.obtenerUsuarios() }
                val deferredProductos = async { api.obtenerProductos() }
                
                DatosCombinados(
                    usuarios = deferredUsuarios.await(),
                    productos = deferredProductos.await(),
                    tiempoCargaMs = tiempoCarga
                )
            }
            
            resultado
        }
    }
    
    override suspend fun buscarUsuarios(query: String): List<Usuario> {
        return withContext(dispatcher) {
            api.buscarUsuarios(query)
        }
    }
}
