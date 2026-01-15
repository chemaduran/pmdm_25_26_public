# Iteración 02: Estados de UI

## 🎯 Objetivos de esta iteración

- Entender el patrón de estados de UI (UiState)
- Manejar estados: Loading, Success, Error
- Usar `sealed class` para representar estados
- Implementar manejo de errores en corrutinas

---

## 📚 Conceptos Teóricos

### ¿Por qué necesitamos Estados de UI?

En la iteración anterior, teníamos:
- Un `LiveData` para los usuarios
- Un `LiveData` para el estado de carga
- ¿Y si hay un error? Necesitaríamos otro `LiveData`

**Problema**: Múltiples LiveData dificultan la gestión del estado.

**Solución**: Un único estado que representa todas las posibilidades.

### Sealed Class para Estados

Una `sealed class` es perfecta para representar estados porque:
- ✅ Define un conjunto cerrado de posibilidades
- ✅ El compilador verifica que manejemos todos los casos
- ✅ Cada estado puede tener datos diferentes

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val mensaje: String) : UiState<Nothing>()
}
```

### Diagrama de Estados

```
┌─────────────┐
│   IDLE      │ ← Estado inicial
└──────┬──────┘
       │ cargarDatos()
       ▼
┌─────────────┐
│  LOADING    │ ← Mostramos ProgressBar
└──────┬──────┘
       │
   ┌───┴───┐
   │       │
   ▼       ▼
┌──────┐ ┌──────┐
│SUCCESS│ │ERROR │
└──────┘ └──────┘
```

### Manejo de Errores con try-catch

```kotlin
viewModelScope.launch {
    _uiState.value = UiState.Loading
    
    try {
        val datos = api.obtenerDatos()
        _uiState.value = UiState.Success(datos)
    } catch (e: Exception) {
        _uiState.value = UiState.Error(e.message ?: "Error desconocido")
    }
}
```

---

## 💻 Implementación

### Estructura del Proyecto

```
app/src/main/java/com/example/tutorial02/
├── MainActivity.kt
├── MainViewModel.kt
├── Usuario.kt
├── ApiSimulada.kt
└── UiState.kt          ← NUEVO
```

### 1. UiState (UiState.kt)

```kotlin
/**
 * Representa los posibles estados de la UI.
 * 
 * Usamos sealed class porque:
 * - Define un conjunto CERRADO de estados posibles
 * - El compilador nos obliga a manejar todos los casos
 * - Cada estado puede contener datos diferentes
 * 
 * @param T El tipo de datos en caso de éxito
 */
sealed class UiState<out T> {
    
    /**
     * Estado inicial o inactivo.
     * No hay operación en curso.
     */
    object Idle : UiState<Nothing>()
    
    /**
     * Operación en curso.
     * Se debe mostrar un indicador de carga.
     */
    object Loading : UiState<Nothing>()
    
    /**
     * Operación completada con éxito.
     * @param data Los datos obtenidos
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Operación fallida.
     * @param mensaje Descripción del error
     */
    data class Error(val mensaje: String) : UiState<Nothing>()
}
```

### 2. API Simulada con Errores (ApiSimulada.kt)

```kotlin
object ApiSimulada {
    
    private var contadorLlamadas = 0
    
    suspend fun obtenerUsuarios(): List<Usuario> {
        delay(2000)
        
        contadorLlamadas++
        
        // Simulamos un error cada 3 llamadas
        if (contadorLlamadas % 3 == 0) {
            throw Exception("Error de conexión simulado")
        }
        
        return listOf(
            Usuario(1, "Ana García", "ana@email.com"),
            Usuario(2, "Carlos López", "carlos@email.com"),
            Usuario(3, "María Fernández", "maria@email.com")
        )
    }
}
```

### 3. ViewModel con UiState (MainViewModel.kt)

```kotlin
class MainViewModel : ViewModel() {
    
    // Un único LiveData para todo el estado de la UI
    private val _uiState = MutableLiveData<UiState<List<Usuario>>>()
    val uiState: LiveData<UiState<List<Usuario>>> = _uiState
    
    init {
        // Estado inicial
        _uiState.value = UiState.Idle
    }
    
    fun cargarUsuarios() {
        viewModelScope.launch {
            // Cambiamos a estado de carga
            _uiState.value = UiState.Loading
            
            try {
                // Intentamos obtener los datos
                val usuarios = ApiSimulada.obtenerUsuarios()
                // Éxito: actualizamos con los datos
                _uiState.value = UiState.Success(usuarios)
                
            } catch (e: Exception) {
                // Error: mostramos el mensaje
                _uiState.value = UiState.Error(
                    e.message ?: "Ha ocurrido un error desconocido"
                )
            }
        }
    }
}
```

### 4. Activity con manejo de estados (MainActivity.kt)

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            // Usamos when para manejar todos los estados
            when (state) {
                is UiState.Idle -> mostrarEstadoInicial()
                is UiState.Loading -> mostrarCargando()
                is UiState.Success -> mostrarUsuarios(state.data)
                is UiState.Error -> mostrarError(state.mensaje)
            }
        }
    }
    
    private fun mostrarEstadoInicial() {
        binding.progressBar.visibility = View.GONE
        binding.tvResultado.text = "Pulsa el botón para cargar usuarios"
        binding.tvError.visibility = View.GONE
        binding.btnCargar.isEnabled = true
    }
    
    private fun mostrarCargando() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvResultado.text = ""
        binding.tvError.visibility = View.GONE
        binding.btnCargar.isEnabled = false
    }
    
    private fun mostrarUsuarios(usuarios: List<Usuario>) {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.btnCargar.isEnabled = true
        
        val texto = usuarios.joinToString("\n\n") { 
            "👤 ${it.nombre}\n   📧 ${it.email}" 
        }
        binding.tvResultado.text = texto
    }
    
    private fun mostrarError(mensaje: String) {
        binding.progressBar.visibility = View.GONE
        binding.tvResultado.text = ""
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = "❌ $mensaje"
        binding.btnCargar.isEnabled = true
    }
    
    private fun setupListeners() {
        binding.btnCargar.setOnClickListener {
            viewModel.cargarUsuarios()
        }
    }
}
```

---

## 🔍 Ventajas de este enfoque

| Antes (Iteración 1) | Ahora (Iteración 2) |
|---------------------|---------------------|
| Múltiples LiveData | Un único LiveData |
| Sin manejo de errores | Errores manejados |
| Estados inconsistentes posibles | Estados siempre coherentes |
| `when` no exhaustivo | `when` exhaustivo (compilador ayuda) |

### El compilador nos ayuda

```kotlin
when (state) {
    is UiState.Loading -> { /* ... */ }
    is UiState.Success -> { /* ... */ }
    // ❌ ERROR: Falta manejar UiState.Idle y UiState.Error
}
```

---

## ⚠️ Errores Comunes

### ❌ Error 1: No manejar todos los estados

```kotlin
when (state) {
    is UiState.Success -> mostrarDatos(state.data)
    else -> { } // ❌ Mala práctica: ignora otros estados
}
```

### ❌ Error 2: Olvidar resetear la UI

```kotlin
private fun mostrarUsuarios(usuarios: List<Usuario>) {
    binding.tvResultado.text = usuarios.toString()
    // ❌ Olvidamos ocultar el progressBar y el error
}
```

### ❌ Error 3: Catch genérico sin logging

```kotlin
try {
    val datos = api.obtenerDatos()
} catch (e: Exception) {
    // ❌ No hacemos nada, el error se pierde silenciosamente
}
```

**Solución**: Siempre registrar el error

```kotlin
} catch (e: Exception) {
    Log.e("MainViewModel", "Error al cargar usuarios", e)
    _uiState.value = UiState.Error(e.message ?: "Error desconocido")
}
```

---

## ✅ Ejercicio Práctico

1. Abre el proyecto en la carpeta `proyecto/`
2. Ejecuta la app y pulsa "Cargar Usuarios" varias veces
3. Observa cómo cada 3 llamadas aparece un error
4. Modifica `UiState` para añadir un estado `Empty` para cuando la lista está vacía
5. Implementa el manejo del nuevo estado en la Activity

---

## 📝 Resumen

| Concepto | Descripción |
|----------|-------------|
| `sealed class` | Define estados cerrados y exhaustivos |
| `UiState` | Patrón para representar estados de UI |
| `try-catch` | Manejo de errores en corrutinas |
| `when` exhaustivo | El compilador verifica todos los casos |

---

## ➡️ Siguiente Iteración

En la siguiente iteración aplicaremos el **Repository Pattern** para separar la lógica de acceso a datos.

👉 [Ir a Iteración 03: Repository Pattern](../iteracion_03_repository_pattern/README.md)

---

[⬅️ Volver al índice](../README.md)
