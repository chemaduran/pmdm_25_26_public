# Iteración 03: Repository Pattern

## 🎯 Objetivos de esta iteración

- Entender el patrón Repository
- Separar la lógica de acceso a datos
- Implementar Dispatchers correctamente
- Usar `withContext` para cambiar de hilo
- Preparar el código para testing

---

## 📚 Conceptos Teóricos

### ¿Qué es el Repository Pattern?

El **Repository** es una capa de abstracción entre la lógica de negocio (ViewModel) y las fuentes de datos (API, Base de datos, etc.).

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  ViewModel  │ ──▶ │ Repository  │ ──▶ │ Data Source │
└─────────────┘     └─────────────┘     └─────────────┘
                           │
                           ├──▶ API Remota
                           ├──▶ Base de Datos Local
                           └──▶ Caché en Memoria
```

### Ventajas del Repository Pattern

| Sin Repository | Con Repository |
|----------------|----------------|
| ViewModel conoce detalles de la API | ViewModel solo pide datos |
| Difícil de testear | Fácil de mockear |
| Código duplicado si hay varias pantallas | Lógica centralizada |
| Difícil cambiar de fuente de datos | Cambio transparente |

### Dispatchers y withContext

Cuando trabajamos con operaciones de IO (red, base de datos), debemos usar `Dispatchers.IO`:

```kotlin
class UsuarioRepository {
    
    suspend fun obtenerUsuarios(): List<Usuario> {
        // Cambiamos al hilo de IO para la operación de red
        return withContext(Dispatchers.IO) {
            api.obtenerUsuarios()
        }
    }
}
```

### ¿Por qué withContext?

```kotlin
// En el ViewModel (Main Thread)
viewModelScope.launch {           // Dispatcher: Main
    val usuarios = repository.obtenerUsuarios()  // Dispatcher: IO (internamente)
    _uiState.value = UiState.Success(usuarios)   // Dispatcher: Main
}

// En el Repository
suspend fun obtenerUsuarios(): List<Usuario> {
    return withContext(Dispatchers.IO) {  // Cambia a IO
        api.obtenerUsuarios()              // Se ejecuta en IO
    }                                      // Vuelve a Main automáticamente
}
```

---

## 💻 Implementación

### Estructura del Proyecto

```
app/src/main/java/com/example/tutorial03/
├── data/
│   ├── api/
│   │   └── ApiSimulada.kt
│   ├── model/
│   │   └── Usuario.kt
│   └── repository/
│       └── UsuarioRepository.kt    ← NUEVO
├── ui/
│   ├── MainActivity.kt
│   ├── MainViewModel.kt
│   └── UiState.kt
└── di/
    └── ServiceLocator.kt           ← NUEVO (Inyección simple)
```

### 1. Interface del Repository

Primero definimos una interface para poder mockear en tests:

```kotlin
/**
 * Contrato del repositorio de usuarios.
 * 
 * Usar una interface nos permite:
 * - Definir el contrato sin implementación
 * - Crear mocks para testing
 * - Cambiar la implementación sin afectar al ViewModel
 */
interface UsuarioRepository {
    suspend fun obtenerUsuarios(): List<Usuario>
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?
}
```

### 2. Implementación del Repository

```kotlin
/**
 * Implementación del repositorio de usuarios.
 * 
 * El Repository:
 * - Abstrae la fuente de datos del ViewModel
 * - Maneja los Dispatchers internamente
 * - Puede combinar múltiples fuentes de datos
 * - Es el lugar correcto para implementar caché
 */
class UsuarioRepositoryImpl(
    private val api: ApiSimulada,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsuarioRepository {
    
    /**
     * Obtiene la lista de usuarios.
     * 
     * Nota: withContext cambia al dispatcher de IO para la operación
     * de red, y automáticamente vuelve al dispatcher original al terminar.
     */
    override suspend fun obtenerUsuarios(): List<Usuario> {
        return withContext(dispatcher) {
            api.obtenerUsuarios()
        }
    }
    
    override suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return withContext(dispatcher) {
            api.obtenerUsuarios().find { it.id == id }
        }
    }
}
```

### 3. Service Locator (Inyección simple)

Para no complicar con Hilt/Dagger, usamos un Service Locator simple:

```kotlin
/**
 * Service Locator simple para proveer dependencias.
 * 
 * En una aplicación real, usaríamos Hilt o Dagger.
 * Este enfoque es suficiente para aprender y para apps pequeñas.
 */
object ServiceLocator {
    
    private val api: ApiSimulada by lazy { ApiSimulada }
    
    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepositoryImpl(api)
    }
}
```

### 4. ViewModel actualizado

```kotlin
class MainViewModel(
    private val repository: UsuarioRepository = ServiceLocator.usuarioRepository
) : ViewModel() {
    
    private val _uiState = MutableLiveData<UiState<List<Usuario>>>()
    val uiState: LiveData<UiState<List<Usuario>>> = _uiState
    
    init {
        _uiState.value = UiState.Idle
    }
    
    fun cargarUsuarios() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                // El ViewModel no sabe de dónde vienen los datos
                // ni qué Dispatcher usar - eso es responsabilidad del Repository
                val usuarios = repository.obtenerUsuarios()
                _uiState.value = UiState.Success(usuarios)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar usuarios", e)
                _uiState.value = UiState.Error(
                    e.message ?: "Error desconocido"
                )
            }
        }
    }
}
```

---

## 🔍 Comparación

### Antes (Iteración 2)

```kotlin
class MainViewModel : ViewModel() {
    
    fun cargarUsuarios() {
        viewModelScope.launch {
            // ❌ El ViewModel conoce directamente la API
            val usuarios = ApiSimulada.obtenerUsuarios()
        }
    }
}
```

### Después (Iteración 3)

```kotlin
class MainViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {
    
    fun cargarUsuarios() {
        viewModelScope.launch {
            // ✅ El ViewModel solo conoce la abstracción
            val usuarios = repository.obtenerUsuarios()
        }
    }
}
```

---

## 🧪 Testing

Con el Repository Pattern, testear es mucho más fácil:

```kotlin
class MainViewModelTest {
    
    @Test
    fun `cuando cargar usuarios exitoso, estado es Success`() = runTest {
        // Arrange: Creamos un mock del repository
        val mockRepository = object : UsuarioRepository {
            override suspend fun obtenerUsuarios() = listOf(
                Usuario(1, "Test", "test@test.com")
            )
            override suspend fun obtenerUsuarioPorId(id: Int) = null
        }
        
        val viewModel = MainViewModel(mockRepository)
        
        // Act
        viewModel.cargarUsuarios()
        
        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
    }
}
```

---

## ⚠️ Errores Comunes

### ❌ Error 1: Olvidar withContext

```kotlin
class UsuarioRepositoryImpl : UsuarioRepository {
    
    override suspend fun obtenerUsuarios(): List<Usuario> {
        // ❌ Se ejecuta en Main Thread - MALO
        return api.obtenerUsuarios()
    }
}
```

### ❌ Error 2: Usar Dispatchers.Main en Repository

```kotlin
override suspend fun obtenerUsuarios(): List<Usuario> {
    return withContext(Dispatchers.Main) {  // ❌ NUNCA
        api.obtenerUsuarios()
    }
}
```

### ❌ Error 3: Hardcodear el Dispatcher

```kotlin
class UsuarioRepositoryImpl : UsuarioRepository {
    
    override suspend fun obtenerUsuarios(): List<Usuario> {
        return withContext(Dispatchers.IO) {  // ❌ Difícil de testear
            api.obtenerUsuarios()
        }
    }
}
```

**Solución**: Inyectar el Dispatcher

```kotlin
class UsuarioRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO  // ✅
) : UsuarioRepository
```

---

## 📐 Principios SOLID aplicados

| Principio | Aplicación |
|-----------|------------|
| **S** - Single Responsibility | Repository solo maneja acceso a datos |
| **O** - Open/Closed | Abierto a extensión (nuevas fuentes), cerrado a modificación |
| **L** - Liskov Substitution | Cualquier implementación de UsuarioRepository es intercambiable |
| **I** - Interface Segregation | Interface pequeña y específica |
| **D** - Dependency Inversion | ViewModel depende de abstracción, no de implementación |

---

## ✅ Ejercicio Práctico

1. Abre el proyecto en la carpeta `proyecto/`
2. Añade un método `buscarPorNombre(query: String)` al Repository
3. Implementa la búsqueda en `UsuarioRepositoryImpl`
4. Añade un campo de búsqueda en la UI
5. Conecta todo y prueba que funciona

---

## 📝 Resumen

| Concepto | Descripción |
|----------|-------------|
| Repository Pattern | Abstrae el acceso a datos |
| Interface | Define el contrato, permite mocking |
| `withContext` | Cambia el Dispatcher temporalmente |
| `Dispatchers.IO` | Para operaciones de red/disco |
| Service Locator | Patrón simple de inyección de dependencias |

---

## ➡️ Siguiente Iteración

En la siguiente iteración introduciremos **Flow y StateFlow** para flujos de datos reactivos.

👉 [Ir a Iteración 04: Flow y StateFlow](../iteracion_04_flow/README.md)

---

[⬅️ Volver al índice](../README.md)
