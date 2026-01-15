# Iteración 4: StateFlow

## 📖 Introducción

**StateFlow** es una alternativa moderna a LiveData que forma parte de **Kotlin Flow**. Es especialmente útil cuando trabajas con Kotlin puro y quieres una solución más idiomática.

## 🎯 Objetivo

Aprender a usar StateFlow como alternativa a LiveData para observar cambios en los datos.

## 🧠 StateFlow vs LiveData

| Característica | LiveData | StateFlow |
|----------------|----------|-----------|
| Lenguaje | Java/Kotlin | Kotlin puro |
| Valor inicial | Opcional | Obligatorio |
| Null safety | Puede ser null | Configurable |
| Integración Coroutines | Limitada | Total |
| Operadores | Pocos | Muchos (map, filter, combine...) |

### ¿Cuándo usar cada uno?

- **LiveData**: Proyectos legacy, cuando no usas coroutines
- **StateFlow**: Proyectos nuevos, cuando usas Kotlin y coroutines

## 📦 Dependencia necesaria

```kotlin
dependencies {
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    // Para collectAsStateWithLifecycle (opcional pero recomendado)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}
```

## 💻 Código

### ContadorViewModel.kt

```kotlin
package com.example.contadorapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel con StateFlow
 * 
 * StateFlow es la alternativa moderna a LiveData basada en Kotlin Flow.
 */
class ContadorViewModel : ViewModel() {
    
    // MutableStateFlow: se puede modificar (privado)
    // Requiere un valor inicial obligatorio
    private val _contador = MutableStateFlow(0)
    
    // StateFlow: solo lectura (público)
    val contador: StateFlow<Int> = _contador.asStateFlow()
    
    fun incrementar() {
        _contador.value++
    }
    
    fun decrementar() {
        _contador.value--
    }
}
```

### MainActivity.kt

```kotlin
package com.example.contadorapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.contadorapp.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * MainActivity con observación de StateFlow
 * 
 * Usamos coroutines para recolectar los valores del StateFlow
 * de forma segura respecto al ciclo de vida.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: ContadorViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observar cambios en el contador con StateFlow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contador.collectLatest { valor ->
                    binding.tvContador.text = valor.toString()
                }
            }
        }

        // Configurar los botones
        binding.btnIncrementar.setOnClickListener {
            viewModel.incrementar()
        }

        binding.btnDecrementar.setOnClickListener {
            viewModel.decrementar()
        }
    }
}
```

## 🔑 Conceptos clave

### 1. MutableStateFlow vs StateFlow

```kotlin
// Privado y modificable (requiere valor inicial)
private val _contador = MutableStateFlow(0)

// Público y solo lectura
val contador: StateFlow<Int> = _contador.asStateFlow()
```

Similar al patrón de LiveData, pero con tipos de Flow.

### 2. Valor inicial obligatorio

```kotlin
// ✅ Correcto: StateFlow siempre necesita valor inicial
private val _contador = MutableStateFlow(0)

// ❌ Error: No se puede crear sin valor inicial
// private val _contador = MutableStateFlow<Int>()
```

### 3. Recolectar de forma segura

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.contador.collectLatest { valor ->
            // Solo se ejecuta cuando la Activity está STARTED o superior
            binding.tvContador.text = valor.toString()
        }
    }
}
```

| Componente | Función |
|------------|---------|
| `lifecycleScope` | Coroutine scope ligado al ciclo de vida |
| `repeatOnLifecycle` | Reinicia la recolección cuando el lifecycle está activo |
| `collectLatest` | Recoge los valores, cancelando recolecciones anteriores |

### 4. Modificar el valor

```kotlin
// Con StateFlow, podemos usar operadores de incremento directamente
_contador.value++

// O asignar un nuevo valor
_contador.value = nuevoValor
```

## 📊 Comparación: LiveData vs StateFlow

### Con LiveData

```kotlin
// ViewModel
private val _contador = MutableLiveData(0)
val contador: LiveData<Int> = _contador

fun incrementar() {
    _contador.value = (_contador.value ?: 0) + 1
}

// Activity
viewModel.contador.observe(this) { valor ->
    binding.tvContador.text = valor.toString()
}
```

### Con StateFlow

```kotlin
// ViewModel
private val _contador = MutableStateFlow(0)
val contador: StateFlow<Int> = _contador.asStateFlow()

fun incrementar() {
    _contador.value++  // Más simple, no hay nulls
}

// Activity
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.contador.collectLatest { valor ->
            binding.tvContador.text = valor.toString()
        }
    }
}
```

## 🎁 Ventajas de StateFlow

| Ventaja | Descripción |
|---------|-------------|
| **Null safety** | Valor inicial obligatorio, menos nulls |
| **Operadores** | map, filter, combine, debounce... |
| **Coroutines** | Integración nativa con coroutines |
| **Multiplataforma** | Funciona en KMM (Kotlin Multiplatform) |

## ⚠️ Consideraciones

1. **Más verboso para observar**: Requiere `lifecycleScope` y `repeatOnLifecycle`
2. **Requiere conocimiento de coroutines**: Es importante entender los conceptos básicos
3. **Emite el valor actual inmediatamente**: Al suscribirse, recibe el valor actual

## 🧪 Prueba el código

1. Ejecuta la aplicación
2. Incrementa el contador varias veces
3. Rota el dispositivo
4. Observa que todo funciona igual que con LiveData ✓

## 📝 Resumen

En esta iteración hemos aprendido:

- ✅ Qué es StateFlow y cómo se compara con LiveData
- ✅ Cómo crear un StateFlow en el ViewModel
- ✅ Cómo observar StateFlow de forma segura con `repeatOnLifecycle`
- ✅ Las ventajas de usar StateFlow en proyectos modernos

---

**Anterior**: [Iteración 3: LiveData](../03_livedata/)  
**Siguiente**: [Iteración 5: ViewModelFactory](../05_viewmodel_factory/)
