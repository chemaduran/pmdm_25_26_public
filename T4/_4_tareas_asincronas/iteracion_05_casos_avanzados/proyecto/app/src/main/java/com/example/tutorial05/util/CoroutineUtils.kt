package com.example.tutorial05.util

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Utilidades para manejo avanzado de corrutinas.
 */
object CoroutineUtils {
    
    private const val TAG = "CoroutineUtils"
    
    /**
     * Ejecuta un bloque con reintentos automáticos usando backoff exponencial.
     * 
     * El backoff exponencial significa que cada reintento espera más tiempo:
     * - Intento 1: espera 1s
     * - Intento 2: espera 2s
     * - Intento 3: espera 4s
     * - etc.
     * 
     * @param intentos Número máximo de intentos (por defecto 3)
     * @param delayInicial Delay inicial en milisegundos (por defecto 1000)
     * @param factor Factor multiplicador para cada reintento (por defecto 2.0)
     * @param onReintento Callback llamado en cada reintento con el número de intento
     * @param bloque Bloque suspend a ejecutar
     * @return Resultado del bloque
     * @throws Exception Si todos los intentos fallan
     */
    suspend fun <T> conReintentos(
        intentos: Int = 3,
        delayInicial: Long = 1000L,
        factor: Double = 2.0,
        onReintento: ((intento: Int, error: Exception) -> Unit)? = null,
        bloque: suspend () -> T
    ): T {
        var intentoActual = 0
        var delayActual = delayInicial
        var ultimaExcepcion: Exception? = null
        
        while (intentoActual < intentos) {
            try {
                return bloque()
            } catch (e: Exception) {
                ultimaExcepcion = e
                intentoActual++
                
                Log.w(TAG, "Intento $intentoActual de $intentos fallido: ${e.message}")
                onReintento?.invoke(intentoActual, e)
                
                if (intentoActual < intentos) {
                    Log.d(TAG, "Esperando ${delayActual}ms antes del siguiente intento...")
                    delay(delayActual)
                    delayActual = (delayActual * factor).toLong()
                }
            }
        }
        
        Log.e(TAG, "Todos los $intentos intentos fallaron")
        throw ultimaExcepcion ?: Exception("Reintentos agotados")
    }
    
    /**
     * Ejecuta un bloque con timeout, devolviendo null si excede el tiempo.
     * 
     * @param tiempoMs Tiempo máximo en milisegundos
     * @param bloque Bloque a ejecutar
     * @return Resultado o null si hay timeout
     */
    suspend fun <T> conTimeoutONull(
        tiempoMs: Long,
        bloque: suspend () -> T
    ): T? {
        return withTimeoutOrNull(tiempoMs) {
            bloque()
        }
    }
    
    /**
     * Ejecuta un bloque con timeout, lanzando excepción si excede el tiempo.
     * 
     * @param tiempoMs Tiempo máximo en milisegundos
     * @param mensajeError Mensaje para la excepción de timeout
     * @param bloque Bloque a ejecutar
     * @return Resultado
     * @throws Exception Si hay timeout
     */
    suspend fun <T> conTimeout(
        tiempoMs: Long,
        mensajeError: String = "La operación excedió el tiempo límite",
        bloque: suspend () -> T
    ): T {
        return withTimeoutOrNull(tiempoMs) {
            bloque()
        } ?: throw Exception("$mensajeError (${tiempoMs}ms)")
    }
    
    /**
     * Combina retry y timeout.
     * 
     * @param intentos Número máximo de intentos
     * @param timeoutPorIntento Timeout para cada intento
     * @param delayInicial Delay inicial entre intentos
     * @param bloque Bloque a ejecutar
     * @return Resultado
     */
    suspend fun <T> conReintentosYTimeout(
        intentos: Int = 3,
        timeoutPorIntento: Long = 5000L,
        delayInicial: Long = 1000L,
        bloque: suspend () -> T
    ): T {
        return conReintentos(
            intentos = intentos,
            delayInicial = delayInicial
        ) {
            conTimeout(timeoutPorIntento) {
                bloque()
            }
        }
    }
}
