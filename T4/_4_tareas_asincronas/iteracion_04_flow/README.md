# Iteración 04: Flow y StateFlow

## 🎯 Objetivos de esta iteración

- Entender qué es Flow y por qué usarlo
- Diferenciar entre Flow frío y Flow caliente
- Usar StateFlow en lugar de LiveData
- Implementar debounce para búsqueda en tiempo real
- Usar `repeatOnLifecycle` para observar Flows

---

## 📚 Conceptos Teóricos

### ¿Qué es Flow?

**Flow** es una API de Kotlin para manejar **flujos de datos asíncronos**. Piensa en él como una "tubería" por la que pasan datos a lo largo del tiempo.

```kotlin
// Un Flow emite múltiples valores a lo largo del tiempo
flow {
    emit(1)    // Emite 1
    delay(100)
    emit(2)    // Emite 2
    delay(100)
    emit(3)    // Emite 3
}
```

### Flow vs LiveData

| Característica | LiveData | Flow/StateFlow |
|----------------|----------|----------------|
| Plataforma | Android only | Kotlin (multiplataforma) |
| Operadores | Limitados | Muchos (map, filter, debounce...) |
| Backpressure | No | Sí |
| Testing | Más complejo | Más simple |
| Lifecycle aware | Sí | Con `repeatOnLifecycle` |

### Tipos de Flow

#### 1. Cold Flow (Flujo Frío)

Se ejecuta cuando alguien lo **colecciona**. Cada collector recibe todos los valores desde el inicio.

```kotlin
val coldFlow = flow {
    println("Iniciando emisión...")
    emit(1)
    emit(2)
}

// No pasa nada hasta que coleccionamos
coldFlow.collect { valor ->
    println("Recibido: $valor")
}
```

#### 2. Hot Flow / StateFlow (Flujo Caliente)

Emite valores **independientemente** de los collectors. `StateFlow` siempre tiene un valor actual.

```kotlin
// StateFlow siempre tiene un valor inicial
private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// Actualizar el valor
_uiState.value = UiState.Loading
```

### StateFlow vs MutableStateFlow

```kotlin
// MutableStateFlow: podemos cambiar el valor
private val _estado = MutableStateFlow(0)

// StateFlow: solo lectura (exponemos esto)
val estado: StateFlow<Int> = _estado.asStateFlow()

// En el ViewModel
_estado.value = 42  // ✅ OK

// En la Activity
viewModel.estado.value = 42  // ❌ Error: es inmutable
```

### Operadores útiles de Flow

```kotlin
flow
    .map { it * 2 }           // Transforma cada valor
    .filter { it > 10 }       // Filtra valores
    .debounce(300)            // Espera 300ms sin nuevos valores
    .distinctUntilChanged()   // Ignora valores repetidos
    .catch { e -> emit(default) }  // Maneja errores
    .collect { valor -> ... }  // Recibe los valores
```

---

## 💻 Implementación

### Estructura del Proyecto

```
app/src/main/java/com/example/tutorial04/
├── data/
│   ├── api/
│   │   └── ApiSimulada.kt
│   ├── model/
│   │   └── Usuario.kt
│   └── repository/
│       ├── UsuarioRepository.kt
│       └── UsuarioRepositoryImpl.kt
├── ui/
│   ├── MainActivity.kt
│   ├── MainViewModel.kt
│   └── UiState.kt
└── di/
    └── ServiceLocator.kt
```

### 1. ViewModel con StateFlow

```kotlin
class MainViewModel(
    private val repository: UsuarioRepository = ServiceLocator.usuarioRepository
) : ViewModel() {
    
    // StateFlow en lugar de LiveData
    private val _uiState = MutableStateFlow<UiState<List<Usuario>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Usuario>>> = _uiState.asStateFlow()
    
    // Flow para la búsqueda con debounce
    private val _queryBusqueda = MutableStateFlow("")
    
    init {
        // Configuramos la búsqueda reactiva
        configurarBusquedaReactiva()
    }
    
    private fun configurarBusquedaReactiva() {
        viewModelScope.launch {
            _queryBusqueda
                .debounce(300)           // Espera 300ms
                .distinctUntilChanged()  // Solo si cambió
                .collect { query ->
                    buscarUsuarios(query)
                }
        }
    }
    
    fun actualizarBusqueda(query: String) {
        _queryBusqueda.value = query
    }
    
    private suspend fun buscarUsuarios(query: String) {
        _uiState.value = UiState.Loading
        
        try {
            val usuarios = repository.buscarUsuarios(query)
            _uiState.value = UiState.Success(usuarios)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "Error")
        }
    }
}
```

### 2. Activity con repeatOnLifecycle

```kotlin
class MainActivity : AppCompatActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupObservers()
    }
    
    private fun setupObservers() {
        // Forma correcta de observar StateFlow en Activities
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Idle -> mostrarEstadoInicial()
                        is UiState.Loading -> mostrarCargando()
                        is UiState.Success -> mostrarUsuarios(state.data)
                        is UiState.Error -> mostrarError(state.mensaje)
                    }
                }
            }
        }
    }
}
```

### 3. Debounce para búsqueda

El **debounce** espera a que el usuario deje de escribir antes de buscar:

```
Usuario escribe: "Ana"
    ↓
"A" → espera 300ms...
"An" → cancela espera, espera 300ms...
"Ana" → cancela espera, espera 300ms...
    ↓
(300ms sin cambios)
    ↓
Búsqueda: "Ana"
```

```kotlin
_queryBusqueda
    .debounce(300)  // Espera 300ms sin nuevos valores
    .distinctUntilChanged()  // No buscar si el texto es igual
    .collect { query ->
        buscarUsuarios(query)
    }
```

---

## 🔍 Comparación: LiveData vs StateFlow

### Con LiveData (antes)

```kotlin
// ViewModel
private val _uiState = MutableLiveData<UiState>()
val uiState: LiveData<UiState> = _uiState

// Activity
viewModel.uiState.observe(this) { state ->
    // Manejar estado
}
```

### Con StateFlow (ahora)

```kotlin
// ViewModel
private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// Activity
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { state ->
            // Manejar estado
        }
    }
}
```

---

## ⚠️ Errores Comunes

### ❌ Error 1: Olvidar repeatOnLifecycle

```kotlin
// ❌ MAL: El Flow sigue coleccionando en background
lifecycleScope.launch {
    viewModel.uiState.collect { state ->
        // Puede causar crashes si la Activity está destruida
    }
}
```

**Solución**: Usar `repeatOnLifecycle`

### ❌ Error 2: Crear Flow en cada llamada

```kotlin
// ❌ MAL: Crea nuevo Flow cada vez
val uiState: Flow<UiState> get() = flow {
    emit(calcularEstado())
}
```

**Solución**: Usar StateFlow que mantiene el estado

### ❌ Error 3: No usar asStateFlow()

```kotlin
// ❌ MAL: Expone el MutableStateFlow
val uiState: MutableStateFlow<UiState> = _uiState

// ✅ BIEN: Expone solo lectura
val uiState: StateFlow<UiState> = _uiState.asStateFlow()
```

---

## 📊 Diagrama de Flujo

```
┌──────────────────────────────────────────────────────────┐
│                      MainActivity                         │
│  ┌─────────────────────────────────────────────────────┐ │
│  │  EditText (búsqueda)                                │ │
│  │       │                                             │ │
│  │       ▼                                             │ │
│  │  onTextChanged("Ana")                               │ │
│  └───────────────────────│─────────────────────────────┘ │
└──────────────────────────│───────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│                     MainViewModel                         │
│  ┌─────────────────────────────────────────────────────┐ │
│  │  _queryBusqueda.value = "Ana"                       │ │
│  │       │                                             │ │
│  │       ▼                                             │ │
│  │  debounce(300ms)                                    │ │
│  │       │                                             │ │
│  │       ▼                                             │ │
│  │  distinctUntilChanged()                             │ │
│  │       │                                             │ │
│  │       ▼                                             │ │
│  │  buscarUsuarios("Ana")                              │ │
│  │       │                                             │ │
│  │       ▼                                             │ │
│  │  _uiState.value = Success(usuarios)                 │ │
│  └─────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│                     MainActivity                          │
│  ┌─────────────────────────────────────────────────────┐ │
│  │  uiState.collect { state ->                         │ │
│  │      mostrarUsuarios(state.data)                    │ │
│  │  }                                                  │ │
│  └─────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

---

## ✅ Ejercicio Práctico

1. Abre el proyecto en la carpeta `proyecto/`
2. Ejecuta la app y escribe en el buscador
3. Observa cómo la búsqueda espera a que dejes de escribir
4. Modifica el debounce a 1000ms y observa la diferencia
5. Añade un `filter { it.length >= 3 }` para buscar solo con 3+ caracteres

---

## 📝 Resumen

| Concepto | Descripción |
|----------|-------------|
| Flow | Flujo de datos asíncronos |
| StateFlow | Flow con estado, siempre tiene valor |
| `debounce` | Espera antes de emitir |
| `distinctUntilChanged` | Ignora valores repetidos |
| `repeatOnLifecycle` | Observa de forma segura en Activities |

---

## ➡️ Siguiente Iteración

En la siguiente iteración veremos **casos avanzados**: retry, timeout, operaciones paralelas y más.

👉 [Ir a Iteración 05: Casos Avanzados](../iteracion_05_casos_avanzados/README.md)

---

[⬅️ Volver al índice](../README.md)
