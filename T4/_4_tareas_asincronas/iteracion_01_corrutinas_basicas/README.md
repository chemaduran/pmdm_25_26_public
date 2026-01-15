# Iteración 01: Corrutinas Básicas

## 🎯 Objetivos de esta iteración

- Entender qué son las corrutinas
- Configurar las dependencias necesarias
- Usar `viewModelScope` para lanzar corrutinas
- Entender `suspend functions`
- Simular una llamada a API

---

## 📚 Conceptos Teóricos

### ¿Qué son las Corrutinas?

Las **corrutinas** son una forma de escribir código asíncrono de manera secuencial y legible. Piensa en ellas como "funciones que pueden pausarse y reanudarse".

```kotlin
// Esto parece código síncrono...
suspend fun obtenerUsuario(): Usuario {
    val datos = api.llamar()  // Se pausa aquí, no bloquea
    return procesarDatos(datos)
}
```

### Componentes Principales

| Componente | Descripción |
|------------|-------------|
| `suspend fun` | Función que puede pausarse sin bloquear el hilo |
| `CoroutineScope` | Define el ciclo de vida de las corrutinas |
| `launch` | Inicia una corrutina que no devuelve resultado |
| `async` | Inicia una corrutina que devuelve un resultado |
| `Dispatchers` | Define en qué hilo se ejecuta la corrutina |

### Dispatchers

| Dispatcher | Uso |
|------------|-----|
| `Dispatchers.Main` | Operaciones de UI |
| `Dispatchers.IO` | Operaciones de red, base de datos, archivos |
| `Dispatchers.Default` | Cálculos intensivos de CPU |

### viewModelScope

En Android, usamos `viewModelScope` porque:
- ✅ Se cancela automáticamente cuando el ViewModel se destruye
- ✅ No hay memory leaks
- ✅ No hay crashes por actualizaciones de UI en ViewModels destruidos

```kotlin
class MiViewModel : ViewModel() {
    fun cargarDatos() {
        viewModelScope.launch {
            // Esta corrutina se cancela automáticamente
            // cuando el ViewModel se destruye
        }
    }
}
```

---

## 🛠️ Configuración del Proyecto

### Paso 1: Dependencias

Añade en `build.gradle.kts` (Module: app):

```kotlin
dependencies {
    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
}
```

---

## 💻 Implementación

### Estructura del Proyecto

```
app/src/main/java/com/example/tutorial01/
├── MainActivity.kt
├── MainViewModel.kt
├── Usuario.kt
└── ApiSimulada.kt
```

### 1. Modelo de Datos (Usuario.kt)

```kotlin
data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String
)
```

### 2. API Simulada (ApiSimulada.kt)

Simulamos una llamada a API con un `delay`:

```kotlin
import kotlinx.coroutines.delay

object ApiSimulada {
    
    suspend fun obtenerUsuarios(): List<Usuario> {
        // Simulamos latencia de red (2 segundos)
        delay(2000)
        
        return listOf(
            Usuario(1, "Ana García", "ana@email.com"),
            Usuario(2, "Carlos López", "carlos@email.com"),
            Usuario(3, "María Fernández", "maria@email.com")
        )
    }
}
```

> 💡 **Nota**: `delay()` es una función `suspend` que pausa la corrutina sin bloquear el hilo.

### 3. ViewModel (MainViewModel.kt)

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    // LiveData para observar desde la Activity
    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios
    
    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> = _cargando
    
    fun cargarUsuarios() {
        viewModelScope.launch {
            _cargando.value = true
            
            // Esta llamada NO bloquea el hilo principal
            val listaUsuarios = ApiSimulada.obtenerUsuarios()
            
            _usuarios.value = listaUsuarios
            _cargando.value = false
        }
    }
}
```

### 4. Activity (MainActivity.kt)

```kotlin
class MainActivity : AppCompatActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.usuarios.observe(this) { usuarios ->
            mostrarUsuarios(usuarios)
        }
        
        viewModel.cargando.observe(this) { estaCargando ->
            progressBar.visibility = if (estaCargando) View.VISIBLE else View.GONE
            btnCargar.isEnabled = !estaCargando
        }
    }
    
    private fun setupListeners() {
        btnCargar.setOnClickListener {
            viewModel.cargarUsuarios()
        }
    }
    
    private fun mostrarUsuarios(usuarios: List<Usuario>) {
        val texto = usuarios.joinToString("\n") { 
            "${it.nombre} - ${it.email}" 
        }
        tvResultado.text = texto
    }
}
```

---

## 🔍 ¿Qué está pasando?

```
1. Usuario pulsa el botón "Cargar"
           ↓
2. viewModel.cargarUsuarios() se ejecuta
           ↓
3. viewModelScope.launch { } crea una corrutina
           ↓
4. _cargando.value = true (UI muestra ProgressBar)
           ↓
5. ApiSimulada.obtenerUsuarios() se ejecuta
   └── delay(2000) PAUSA la corrutina (no bloquea Main Thread)
   └── El usuario puede seguir interactuando con la app
           ↓
6. Después de 2 segundos, la corrutina se REANUDA
           ↓
7. _usuarios.value = listaUsuarios (UI muestra datos)
           ↓
8. _cargando.value = false (UI oculta ProgressBar)
```

---

## ⚠️ Errores Comunes

### ❌ Error 1: Llamar suspend function sin corrutina

```kotlin
fun cargarDatos() {
    val datos = ApiSimulada.obtenerUsuarios() // ❌ ERROR de compilación
}
```

**Solución**: Usar `viewModelScope.launch`

### ❌ Error 2: Bloquear el hilo principal

```kotlin
fun cargarDatos() {
    runBlocking { // ❌ NUNCA uses runBlocking en Android
        val datos = ApiSimulada.obtenerUsuarios()
    }
}
```

**Solución**: Usar `viewModelScope.launch`

### ❌ Error 3: Usar Thread.sleep() en lugar de delay()

```kotlin
suspend fun obtenerDatos() {
    Thread.sleep(2000) // ❌ Bloquea el hilo
}
```

**Solución**: Usar `delay(2000)`

---

## ✅ Ejercicio Práctico

1. Abre el proyecto Android Studio en la carpeta `proyecto/`
2. Ejecuta la app y pulsa "Cargar Usuarios"
3. Observa cómo el ProgressBar aparece durante 2 segundos
4. Modifica `ApiSimulada` para añadir más usuarios
5. Cambia el delay a 5 segundos y observa el comportamiento

---

## 📝 Resumen

| Concepto | Descripción |
|----------|-------------|
| `suspend fun` | Función que puede pausarse |
| `viewModelScope` | Scope seguro que se cancela con el ViewModel |
| `launch` | Inicia una corrutina |
| `delay()` | Pausa sin bloquear |

---

## ➡️ Siguiente Iteración

En la siguiente iteración aprenderemos a manejar **estados de UI** (Loading, Success, Error) de forma más elegante.

👉 [Ir a Iteración 02: Estados de UI](../iteracion_02_estados_ui/README.md)

---

[⬅️ Volver al índice](../README.md)
