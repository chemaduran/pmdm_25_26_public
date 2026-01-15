# Iteración 2: Primer ViewModel

## 📖 Introducción

En esta iteración vamos a solucionar el problema de la pérdida de estado utilizando **ViewModel**, un componente de Android Jetpack diseñado específicamente para almacenar y gestionar datos relacionados con la UI.

## 🎯 Objetivo

Aprender a crear y utilizar un ViewModel básico para mantener el estado de la aplicación.

## 🧠 ¿Qué es un ViewModel?

Un **ViewModel** es una clase que:

1. **Sobrevive a los cambios de configuración** (como la rotación)
2. **Almacena datos de la UI** de forma separada de la Activity
3. **Sigue el principio de separación de responsabilidades**

### Ciclo de vida del ViewModel

```
                    Activity                          ViewModel
                    ────────                          ─────────
    onCreate() ─────────────────────────────────────► Se crea
    
    [Usuario rota el dispositivo]
    
    onDestroy() ───────────────────────────────────► Sigue vivo ✓
    onCreate() ─────────────────────────────────────► Mismo ViewModel
    
    [Usuario cierra la app]
    
    onDestroy() ───────────────────────────────────► onCleared()
                                                     Se destruye
```

> **Importante**: El ViewModel vive más tiempo que la Activity durante los cambios de configuración.

## 📦 Dependencia necesaria

Añade esta dependencia en el archivo `build.gradle.kts` del módulo `app`:

```kotlin
dependencies {
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
}
```

## 💻 Código

### ContadorViewModel.kt

```kotlin
package com.example.contadorapp

import androidx.lifecycle.ViewModel

/**
 * ViewModel que almacena el estado del contador.
 * 
 * Esta clase sobrevive a los cambios de configuración (rotación),
 * por lo que el valor del contador no se pierde.
 */
class ContadorViewModel : ViewModel() {
    
    // El contador ahora vive en el ViewModel
    var contador = 0
        private set  // Solo se puede modificar desde dentro del ViewModel
    
    fun incrementar() {
        contador++
    }
    
    fun decrementar() {
        contador--
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
 * MainActivity CON ViewModel
 * 
 * Ahora el contador sobrevive a la rotación porque está
 * almacenado en el ViewModel, no en la Activity.
 */
class MainActivity : AppCompatActivity() {

    // Obtener el ViewModel usando el delegado viewModels()
    private val viewModel: ContadorViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar el valor actual del ViewModel
        actualizarContador()

        // Configurar los botones
        binding.btnIncrementar.setOnClickListener {
            viewModel.incrementar()
            actualizarContador()
        }

        binding.btnDecrementar.setOnClickListener {
            viewModel.decrementar()
            actualizarContador()
        }
    }

    private fun actualizarContador() {
        // Leemos el valor del ViewModel, no de una variable local
        binding.tvContador.text = viewModel.contador.toString()
    }
}
```

## 🔑 Conceptos clave

### 1. Delegado `by viewModels()`

```kotlin
private val viewModel: ContadorViewModel by viewModels()
```

Este delegado:
- Crea el ViewModel automáticamente si no existe
- Reutiliza el ViewModel existente si ya fue creado
- Se encarga del ciclo de vida automáticamente

### 2. Encapsulamiento del estado

```kotlin
var contador = 0
    private set  // Solo lectura desde fuera
```

Es buena práctica hacer que el estado solo se pueda modificar a través de métodos del ViewModel.

### 3. Separación de responsabilidades

| Componente | Responsabilidad |
|------------|----------------|
| **Activity** | Mostrar UI, capturar eventos |
| **ViewModel** | Almacenar datos, lógica de negocio |

## 🧪 Prueba la solución

1. Ejecuta la aplicación
2. Pulsa varias veces el botón "Incrementar" hasta llegar a 5
3. **Rota el dispositivo** (Ctrl+F11 en el emulador)
4. ¡Observa cómo el contador mantiene el valor 5! 🎉

## 📊 Comparación: Antes vs Después

| Aspecto | Sin ViewModel | Con ViewModel |
|---------|--------------|---------------|
| Almacenamiento del estado | En la Activity | En el ViewModel |
| Rotación del dispositivo | Se pierde el estado | Se mantiene el estado |
| Separación de código | Todo mezclado | Lógica separada |
| Testabilidad | Difícil | Fácil |

## ⚠️ Limitación actual

Aunque el ViewModel funciona, hay un problema:

```kotlin
viewModel.incrementar()
actualizarContador()  // ← Tenemos que llamar manualmente
```

Cada vez que modificamos el estado, tenemos que acordarnos de actualizar la UI. Esto puede llevar a errores.

**Solución**: En la siguiente iteración veremos **LiveData**, que nos permite observar cambios automáticamente.

## 📝 Resumen

En esta iteración hemos aprendido:

- ✅ Qué es un ViewModel y por qué es útil
- ✅ Cómo crear un ViewModel básico
- ✅ Cómo obtener el ViewModel en la Activity con `by viewModels()`
- ✅ El ciclo de vida del ViewModel

---

**Anterior**: [Iteración 1: Sin ViewModel](../01_sin_viewmodel/)  
**Siguiente**: [Iteración 3: LiveData](../03_livedata/)
