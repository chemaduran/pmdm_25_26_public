# Iteración 5: ViewModelFactory

## 📖 Introducción

Hasta ahora, nuestros ViewModels no recibían parámetros en el constructor. Pero, ¿qué pasa si necesitamos inicializar el contador con un valor diferente a 0? ¿O si necesitamos pasar un repositorio al ViewModel?

Para esto necesitamos un **ViewModelFactory**.

## 🎯 Objetivo

Aprender a crear ViewModels que reciben parámetros en el constructor usando ViewModelFactory.

## 🧠 ¿Por qué necesitamos ViewModelFactory?

Por defecto, el sistema de ViewModels solo sabe crear instancias con **constructores vacíos**:

```kotlin
// ✅ Esto funciona
class ContadorViewModel : ViewModel()

// ❌ Esto NO funciona directamente
class ContadorViewModel(valorInicial: Int) : ViewModel()
```

Para poder pasar parámetros, necesitamos proporcionar una **Factory** que sepa cómo crear el ViewModel.

## 💻 Código

### ContadorViewModel.kt

```kotlin
package com.example.contadorapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel que acepta un valor inicial como parámetro
 */
class ContadorViewModel(valorInicial: Int) : ViewModel() {
    
    private val _contador = MutableStateFlow(valorInicial)
    val contador: StateFlow<Int> = _contador.asStateFlow()
    
    fun incrementar() {
        _contador.value++
    }
    
    fun decrementar() {
        _contador.value--
    }
    
    /**
     * Factory para crear el ViewModel con parámetros
     */
    class Factory(private val valorInicial: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContadorViewModel::class.java)) {
                return ContadorViewModel(valorInicial) as T
            }
            throw IllegalArgumentException("ViewModel desconocido")
        }
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
 * MainActivity que usa un ViewModel con parámetros
 */
class MainActivity : AppCompatActivity() {

    // Usamos la Factory para pasar el valor inicial
    private val viewModel: ContadorViewModel by viewModels {
        ContadorViewModel.Factory(valorInicial = 10)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contador.collectLatest { valor ->
                    binding.tvContador.text = valor.toString()
                }
            }
        }

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

### 1. ViewModel con parámetro

```kotlin
class ContadorViewModel(valorInicial: Int) : ViewModel() {
    private val _contador = MutableStateFlow(valorInicial)
    // ...
}
```

Ahora podemos configurar el valor inicial del contador.

### 2. ViewModelProvider.Factory

```kotlin
class Factory(private val valorInicial: Int) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContadorViewModel::class.java)) {
            return ContadorViewModel(valorInicial) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
```

La Factory implementa `ViewModelProvider.Factory` y sobrescribe el método `create()` para crear el ViewModel con los parámetros necesarios.

### 3. Usar la Factory

```kotlin
private val viewModel: ContadorViewModel by viewModels {
    ContadorViewModel.Factory(valorInicial = 10)
}
```

Pasamos la Factory como lambda al delegado `viewModels()`.

## 🎁 Patrón alternativo: Factory como companion object

Otra forma común de organizar la Factory:

```kotlin
class ContadorViewModel(valorInicial: Int) : ViewModel() {
    
    private val _contador = MutableStateFlow(valorInicial)
    val contador: StateFlow<Int> = _contador.asStateFlow()
    
    fun incrementar() { _contador.value++ }
    fun decrementar() { _contador.value-- }
    
    companion object {
        fun factory(valorInicial: Int): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContadorViewModel(valorInicial) as T
                }
            }
        }
    }
}

// Uso:
private val viewModel: ContadorViewModel by viewModels {
    ContadorViewModel.factory(valorInicial = 10)
}
```

## 📦 Caso de uso real: Inyectar un Repository

En aplicaciones reales, usamos ViewModelFactory para inyectar dependencias:

```kotlin
class ContadorViewModel(
    private val repository: ContadorRepository
) : ViewModel() {
    
    private val _contador = MutableStateFlow(0)
    val contador: StateFlow<Int> = _contador.asStateFlow()
    
    init {
        // Cargar valor inicial desde el repositorio
        viewModelScope.launch {
            _contador.value = repository.obtenerContador()
        }
    }
    
    fun incrementar() {
        _contador.value++
        viewModelScope.launch {
            repository.guardarContador(_contador.value)
        }
    }
    
    class Factory(private val repository: ContadorRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ContadorViewModel(repository) as T
        }
    }
}
```

## 🆕 Alternativa moderna: viewModelFactory DSL

A partir de lifecycle 2.5.0, podemos usar un DSL más elegante:

```kotlin
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

private val viewModel: ContadorViewModel by viewModels {
    viewModelFactory {
        initializer {
            ContadorViewModel(valorInicial = 10)
        }
    }
}
```

## 🧪 Prueba el código

1. Ejecuta la aplicación
2. Observa que el contador empieza en **10** (no en 0)
3. Incrementa y decrementa
4. Rota el dispositivo
5. El valor se mantiene ✓

## 📊 Resumen de todas las iteraciones

| Iteración | Concepto | Problema que resuelve |
|-----------|----------|----------------------|
| 1 | Sin ViewModel | Muestra el problema |
| 2 | Primer ViewModel | Mantiene estado al rotar |
| 3 | LiveData | Actualización automática de UI |
| 4 | StateFlow | Alternativa moderna con Flow |
| 5 | ViewModelFactory | ViewModels con parámetros |

## 📝 Resumen

En esta iteración hemos aprendido:

- ✅ Por qué necesitamos ViewModelFactory
- ✅ Cómo crear una Factory para ViewModels con parámetros
- ✅ Diferentes patrones para organizar la Factory
- ✅ Casos de uso reales (inyección de dependencias)

## 🚀 Siguientes pasos

Una vez dominados estos conceptos, puedes explorar:

1. **Hilt/Dagger**: Inyección de dependencias automática
2. **SavedStateHandle**: Guardar estado que sobrevive al cierre de la app
3. **Compose**: ViewModels con Jetpack Compose
4. **Testing**: Pruebas unitarias de ViewModels

---

**Anterior**: [Iteración 4: StateFlow](../04_stateflow/)  
**Inicio**: [README principal](../README.md)
