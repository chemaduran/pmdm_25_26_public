# Iteración 05: Casos Avanzados

## 🎯 Objetivos de esta iteración

- Implementar **retry** (reintentos automáticos)
- Usar **timeout** para limitar tiempo de espera
- Ejecutar **operaciones en paralelo**
- Cancelar corrutinas correctamente
- Combinar múltiples Flows

---

## 📚 Conceptos Teóricos

### 1. Retry (Reintentos)

Cuando una operación falla, a veces queremos reintentar automáticamente:

```kotlin
suspend fun <T> conReintentos(
    intentos: Int = 3,
    delayInicial: Long = 1000,
    factor: Double = 2.0,
    bloque: suspend () -> T
): T {
    var intentoActual = 0
    var delayActual = delayInicial
    
    while (true) {
        try {
            return bloque()
        } catch (e: Exception) {
            intentoActual++
            if (intentoActual >= intentos) throw e
            
            delay(delayActual)
            delayActual = (delayActual * factor).toLong()
        }
    }
}
```

### 2. Timeout

Limitar el tiempo máximo de una operación:

```kotlin
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

// Lanza TimeoutCancellationException si excede el tiempo
val resultado = withTimeout(5000) {
    api.obtenerDatos()
}

// Devuelve null si excede el tiempo (no lanza excepción)
val resultadoONull = withTimeoutOrNull(5000) {
    api.obtenerDatos()
}
```

### 3. Operaciones en Paralelo

#### Con async/await

```kotlin
viewModelScope.launch {
    val deferredUsuarios = async { repository.obtenerUsuarios() }
    val deferredProductos = async { repository.obtenerProductos() }
    
    // Ambas llamadas se ejecutan en paralelo
    val usuarios = deferredUsuarios.await()
    val productos = deferredProductos.await()
    
    // Combinamos los resultados
    _uiState.value = UiState.Success(DatosCombinados(usuarios, productos))
}
```

#### Diferencia: launch vs async

| launch | async |
|--------|-------|
| No devuelve resultado | Devuelve `Deferred<T>` |
| Para "fire and forget" | Para obtener un valor |
| Lanza excepciones inmediatamente | Excepciones en `.await()` |

### 4. Cancelación

Las corrutinas son **cooperativas** - deben verificar la cancelación:

```kotlin
viewModelScope.launch {
    while (isActive) {  // Verifica si fue cancelada
        procesarDato()
    }
}

// O usar funciones que ya lo hacen
viewModelScope.launch {
    delay(1000)  // Verifica cancelación automáticamente
    yield()      // Punto de verificación explícito
}
```

### 5. Combinar Flows

#### combine - Cuando cualquiera emite

```kotlin
val busquedaFlow = _queryBusqueda.debounce(300)
val filtroFlow = _filtroActivo

combine(busquedaFlow, filtroFlow) { query, filtro ->
    Pair(query, filtro)
}.collect { (query, filtro) ->
    buscar(query, filtro)
}
```

#### zip - Parejas de emisiones

```kotlin
val flow1 = flowOf(1, 2, 3)
val flow2 = flowOf("A", "B", "C")

flow1.zip(flow2) { num, letra ->
    "$num$letra"
}.collect { println(it) }
// Imprime: 1A, 2B, 3C
```

---

## 💻 Implementación

### Estructura del Proyecto

```
app/src/main/java/com/example/tutorial05/
├── data/
│   ├── api/
│   │   └── ApiSimulada.kt
│   ├── model/
│   │   ├── Usuario.kt
│   │   └── Producto.kt         ← NUEVO
│   └── repository/
│       ├── UsuarioRepository.kt
│       └── UsuarioRepositoryImpl.kt
├── ui/
│   ├── MainActivity.kt
│   ├── MainViewModel.kt
│   └── UiState.kt
├── util/
│   └── CoroutineUtils.kt       ← NUEVO (retry, timeout)
└── di/
    └── ServiceLocator.kt
```

### 1. Utilidades de Corrutinas (CoroutineUtils.kt)

```kotlin
/**
 * Ejecuta un bloque con reintentos automáticos usando backoff exponencial.
 * 
 * @param intentos Número máximo de intentos
 * @param delayInicial Delay inicial en milisegundos
 * @param factor Factor multiplicador para cada reintento
 * @param bloque Bloque a ejecutar
 * @return Resultado del bloque
 * @throws Exception Si todos los intentos fallan
 */
suspend fun <T> conReintentos(
    intentos: Int = 3,
    delayInicial: Long = 1000L,
    factor: Double = 2.0,
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
            
            if (intentoActual < intentos) {
                Log.d("Retry", "Intento $intentoActual fallido. Reintentando en ${delayActual}ms")
                delay(delayActual)
                delayActual = (delayActual * factor).toLong()
            }
        }
    }
    
    throw ultimaExcepcion ?: Exception("Reintentos agotados")
}

/**
 * Ejecuta un bloque con timeout, devolviendo null si excede el tiempo.
 */
suspend fun <T> conTimeout(
    tiempoMs: Long,
    bloque: suspend () -> T
): T? {
    return withTimeoutOrNull(tiempoMs) {
        bloque()
    }
}
```

### 2. Repository con Retry y Timeout

```kotlin
class UsuarioRepositoryImpl(
    private val api: ApiSimulada,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsuarioRepository {
    
    companion object {
        private const val TIMEOUT_MS = 10_000L
        private const val MAX_REINTENTOS = 3
    }
    
    override suspend fun obtenerUsuariosConRetry(): List<Usuario> {
        return withContext(dispatcher) {
            conReintentos(
                intentos = MAX_REINTENTOS,
                delayInicial = 1000L
            ) {
                conTimeout(TIMEOUT_MS) {
                    api.obtenerUsuarios()
                } ?: throw Exception("Timeout: La operación tardó demasiado")
            }
        }
    }
}
```

### 3. ViewModel con Operaciones Paralelas

```kotlin
class MainViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {
    
    /**
     * Carga usuarios y productos en paralelo.
     */
    fun cargarDatosEnParalelo() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                // Lanzamos ambas operaciones en paralelo
                val deferredUsuarios = async { repository.obtenerUsuarios() }
                val deferredProductos = async { repository.obtenerProductos() }
                
                // Esperamos a que ambas terminen
                val usuarios = deferredUsuarios.await()
                val productos = deferredProductos.await()
                
                _uiState.value = UiState.Success(
                    DatosCombinados(usuarios, productos)
                )
                
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
    
    /**
     * Carga con reintentos automáticos.
     */
    fun cargarConReintentos() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                val usuarios = repository.obtenerUsuariosConRetry()
                _uiState.value = UiState.Success(usuarios)
                
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    "Falló después de varios intentos: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Cancela la operación actual.
     */
    private var jobActual: Job? = null
    
    fun cargarCancelable() {
        // Cancelamos cualquier operación anterior
        jobActual?.cancel()
        
        jobActual = viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                val usuarios = repository.obtenerUsuarios()
                _uiState.value = UiState.Success(usuarios)
            } catch (e: CancellationException) {
                Log.d(TAG, "Operación cancelada")
                _uiState.value = UiState.Idle
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
    
    fun cancelarOperacion() {
        jobActual?.cancel()
        _uiState.value = UiState.Idle
    }
}
```

### 4. Combinar Flows

```kotlin
class MainViewModel : ViewModel() {
    
    private val _queryBusqueda = MutableStateFlow("")
    private val _filtroCategoria = MutableStateFlow<String?>(null)
    private val _ordenamiento = MutableStateFlow(Ordenamiento.NOMBRE)
    
    init {
        configurarBusquedaAvanzada()
    }
    
    private fun configurarBusquedaAvanzada() {
        viewModelScope.launch {
            // Combinamos tres flows
            combine(
                _queryBusqueda.debounce(300),
                _filtroCategoria,
                _ordenamiento
            ) { query, categoria, orden ->
                Triple(query, categoria, orden)
            }
            .distinctUntilChanged()
            .collect { (query, categoria, orden) ->
                buscarConFiltros(query, categoria, orden)
            }
        }
    }
    
    private suspend fun buscarConFiltros(
        query: String,
        categoria: String?,
        orden: Ordenamiento
    ) {
        _uiState.value = UiState.Loading
        
        try {
            var resultados = repository.buscarUsuarios(query)
            
            // Aplicar filtro
            if (categoria != null) {
                resultados = resultados.filter { it.categoria == categoria }
            }
            
            // Aplicar orden
            resultados = when (orden) {
                Ordenamiento.NOMBRE -> resultados.sortedBy { it.nombre }
                Ordenamiento.EMAIL -> resultados.sortedBy { it.email }
                Ordenamiento.ID -> resultados.sortedBy { it.id }
            }
            
            _uiState.value = UiState.Success(resultados)
            
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "Error")
        }
    }
}
```

---

## 📊 Diagrama: Retry con Backoff Exponencial

```
Intento 1 ──── FALLA ──── espera 1s
                              │
Intento 2 ──── FALLA ──── espera 2s
                              │
Intento 3 ──── FALLA ──── espera 4s
                              │
Intento 4 ──── ÉXITO ──── devuelve resultado
```

---

## 📊 Diagrama: Operaciones en Paralelo

```
┌──────────────────────────────────────────────────┐
│                  viewModelScope                   │
│                                                   │
│   launch {                                        │
│       ┌────────────────────────────────────────┐ │
│       │  async { obtenerUsuarios() }            │ │──┐
│       │         ↓                               │ │  │ En paralelo
│       │  async { obtenerProductos() }           │ │──┘
│       │         ↓                               │ │
│       │  await() + await()                      │ │
│       │         ↓                               │ │
│       │  combinar resultados                    │ │
│       └────────────────────────────────────────┘ │
│   }                                              │
└──────────────────────────────────────────────────┘

Tiempo: |-----|-----|-----|-----|-----|
        
Secuencial:
Usuarios  ████████████
Productos              ████████████
Total:    ████████████████████████  (24 unidades)

Paralelo:
Usuarios  ████████████
Productos ████████████
Total:    ████████████  (12 unidades) ✅ 50% más rápido
```

---

## ⚠️ Errores Comunes

### ❌ Error 1: No manejar CancellationException

```kotlin
try {
    val resultado = operacionLarga()
} catch (e: Exception) {
    // ❌ Captura también CancellationException
    mostrarError(e.message)
}
```

**Solución**: Relanzar CancellationException

```kotlin
try {
    val resultado = operacionLarga()
} catch (e: CancellationException) {
    throw e  // ✅ Relanzamos
} catch (e: Exception) {
    mostrarError(e.message)
}
```

### ❌ Error 2: Bloquear con runBlocking

```kotlin
fun obtenerDatos(): List<Usuario> {
    return runBlocking {  // ❌ NUNCA en Main Thread
        repository.obtenerUsuarios()
    }
}
```

### ❌ Error 3: Olvidar await()

```kotlin
viewModelScope.launch {
    val deferred = async { obtenerDatos() }
    // ❌ Nunca llamamos await(), el resultado se pierde
    _uiState.value = UiState.Success(emptyList())
}
```

---

## 🔧 Patrones Avanzados

### Resultado Sealed Class

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

suspend fun <T> safeCall(call: suspend () -> T): Result<T> {
    return try {
        Result.Success(call())
    } catch (e: Exception) {
        Result.Error(e)
    }
}

// Uso
val resultado = safeCall { repository.obtenerUsuarios() }
when (resultado) {
    is Result.Success -> mostrar(resultado.data)
    is Result.Error -> mostrarError(resultado.exception)
    is Result.Loading -> mostrarCargando()
}
```

### SupervisorJob

```kotlin
// Si una corrutina hija falla, las demás continúan
viewModelScope.launch {
    supervisorScope {
        launch { operacion1() }  // Si falla, las demás continúan
        launch { operacion2() }
        launch { operacion3() }
    }
}
```

---

## ✅ Ejercicio Práctico

1. Abre el proyecto en la carpeta `proyecto/`
2. Prueba el botón "Cargar con Retry" y observa los logs
3. Modifica el número de reintentos a 5
4. Implementa un botón "Cancelar" que detenga la carga
5. Añade una operación paralela que cargue usuarios y un contador

---

## 📝 Resumen

| Concepto | Descripción |
|----------|-------------|
| `retry` | Reintentar operaciones fallidas |
| `withTimeout` | Limitar tiempo de operación |
| `async/await` | Operaciones en paralelo |
| `Job.cancel()` | Cancelar corrutinas |
| `combine` | Combinar múltiples Flows |
| `supervisorScope` | Aislar fallos de corrutinas hijas |

---

## 🎓 Conclusión del Tutorial

¡Felicidades! Has completado el tutorial de Tareas Asíncronas en Android. Ahora conoces:

1. ✅ **Corrutinas básicas** con `viewModelScope`
2. ✅ **Manejo de estados** con `UiState`
3. ✅ **Repository Pattern** para separar capas
4. ✅ **Flow y StateFlow** para datos reactivos
5. ✅ **Casos avanzados**: retry, timeout, paralelo

### Próximos pasos recomendados

- [ ] Aprender Hilt para inyección de dependencias
- [ ] Implementar Room con Flow para persistencia
- [ ] Explorar Jetpack Compose con StateFlow
- [ ] Añadir tests unitarios con kotlinx-coroutines-test

---

[⬅️ Volver al índice](../README.md)
