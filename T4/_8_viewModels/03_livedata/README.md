# Iteración 3: LiveData

## 📖 Introducción

En la iteración anterior teníamos que llamar manualmente a `actualizarContador()` cada vez que modificábamos el estado. Esto puede llevar a errores y a inconsistencias en la UI.

**LiveData** es un contenedor de datos observable que nos permite **reaccionar automáticamente** a los cambios de datos.

## 🎯 Objetivo

Aprender a usar LiveData para que la UI se actualice automáticamente cuando cambian los datos.

## 🧠 ¿Qué es LiveData?

**LiveData** es un contenedor de datos observable que:

1. **Es consciente del ciclo de vida**: Solo notifica a observadores activos
2. **Evita memory leaks**: Se limpia automáticamente cuando el lifecycle owner se destruye
3. **Siempre muestra datos actualizados**: Los observadores reciben el último valor al suscribirse

### Patrón Observer

```
┌─────────────────────────────────────────────────────────────┐
│                      ViewModel                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │   LiveData<Int>                                      │   │
│  │   ┌─────┐                                            │   │
│  │   │  5  │  ← Valor actual                            │   │
│  │   └─────┘                                            │   │
│  │      │                                               │   │
│  │      │ Notifica cambios                              │   │
│  └──────┼───────────────────────────────────────────────┘   │
│         │                                                   │
└─────────┼───────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────┐
│                      Activity                               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │   Observer                                           │   │
│  │   observe(this) { valor ->                           │   │
│  │       tvContador.text = valor.toString()             │   │
│  │   }                                                  │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 📦 Dependencia necesaria

Añade esta dependencia en el archivo `build.gradle.kts`:

```kotlin
dependencies {
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
}
```

## 💻 Código

### ContadorViewModel.kt

```kotlin
package com.example.contadorapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel con LiveData
 * 
 * Ahora el estado es observable, la UI se actualiza automáticamente
 * cuando cambia el valor del contador.
 */
class ContadorViewModel : ViewModel() {
    
    // MutableLiveData: se puede modificar (privado)
    private val _contador = MutableLiveData(0)
    
    // LiveData: solo lectura (público)
    val contador: LiveData<Int> = _contador
    
    fun incrementar() {
        _contador.value = (_contador.value ?: 0) + 1
    }
    
    fun decrementar() {
        _contador.value = (_contador.value ?: 0) - 1
    }
}
```

### MainActivity.kt

```kotlin
package com.example.contadorapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.contadorapp.databinding.ActivityMainBinding

/**
 * MainActivity con observación de LiveData
 * 
 * Ya no necesitamos llamar a actualizarContador() manualmente.
 * La UI se actualiza automáticamente cuando cambia el LiveData.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: ContadorViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observar cambios en el contador
        // Cuando el valor cambie, se ejecutará automáticamente el bloque
        viewModel.contador.observe(this) { valor ->
            binding.tvContador.text = valor.toString()
        }

        // Configurar los botones
        // ¡Ya no necesitamos llamar a actualizarContador()!
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

### 1. MutableLiveData vs LiveData

```kotlin
// Privado y modificable
private val _contador = MutableLiveData(0)

// Público y solo lectura
val contador: LiveData<Int> = _contador
```

Este patrón (llamado **backing property**) asegura que:
- Dentro del ViewModel podemos modificar el valor
- Fuera del ViewModel solo se puede observar

### 2. Observar cambios

```kotlin
viewModel.contador.observe(this) { valor ->
    // Este código se ejecuta cada vez que cambia el valor
    binding.tvContador.text = valor.toString()
}
```

El parámetro `this` es el **LifecycleOwner** (la Activity), que permite a LiveData saber cuándo debe enviar notificaciones.

### 3. Modificar el valor

```kotlin
// Establecer un nuevo valor
_contador.value = nuevoValor

// O usando postValue (seguro desde cualquier hilo)
_contador.postValue(nuevoValor)
```

## 📊 Comparación: Sin LiveData vs Con LiveData

### Sin LiveData (Iteración 2)

```kotlin
// En el ViewModel
var contador = 0

fun incrementar() {
    contador++
}

// En la Activity
binding.btnIncrementar.setOnClickListener {
    viewModel.incrementar()
    actualizarContador()  // ← Hay que acordarse de llamar
}
```

### Con LiveData (Iteración 3)

```kotlin
// En el ViewModel
private val _contador = MutableLiveData(0)
val contador: LiveData<Int> = _contador

fun incrementar() {
    _contador.value = (_contador.value ?: 0) + 1
}

// En la Activity
viewModel.contador.observe(this) { valor ->
    binding.tvContador.text = valor.toString()  // ← Automático
}

binding.btnIncrementar.setOnClickListener {
    viewModel.incrementar()
    // ¡No hay que hacer nada más!
}
```

## 🎁 Ventajas de LiveData

| Ventaja | Descripción |
|---------|-------------|
| **Automático** | La UI se actualiza sola cuando cambian los datos |
| **Seguro** | No hay crashes por actualizar UI cuando la Activity está destruida |
| **Sin memory leaks** | Los observadores se limpian automáticamente |
| **Datos actualizados** | Siempre muestra el último valor |

## 🧪 Prueba el código

1. Ejecuta la aplicación
2. Incrementa el contador varias veces
3. Rota el dispositivo
4. Observa cómo:
   - El contador mantiene el valor ✓
   - La UI se actualiza automáticamente ✓

## ⚠️ Consideraciones

- `value` puede ser `null`, por eso usamos el operador Elvis `?:`
- `value` solo funciona desde el hilo principal
- Para otros hilos, usar `postValue()`

## 📝 Resumen

En esta iteración hemos aprendido:

- ✅ Qué es LiveData y para qué sirve
- ✅ La diferencia entre MutableLiveData y LiveData
- ✅ Cómo observar cambios en LiveData
- ✅ El patrón backing property

---

**Anterior**: [Iteración 2: Primer ViewModel](../02_primer_viewmodel/)  
**Siguiente**: [Iteración 4: StateFlow](../04_stateflow/)
