# Iteración 1: Sin ViewModel - El Problema

## 📖 Introducción

En esta primera iteración vamos a crear una aplicación de **contador simple** sin usar ViewModel. El objetivo es **demostrar el problema** que ocurre cuando no gestionamos correctamente el estado de la UI.

## 🎯 Objetivo

Entender por qué necesitamos ViewModels observando cómo se pierde el estado de la aplicación.

## 📱 La aplicación

Crearemos un contador simple con:
- Un `TextView` que muestra el número actual
- Un `Button` para incrementar el contador
- Un `Button` para decrementar el contador

## 🔍 El problema

Cuando el usuario **rota el dispositivo**, Android destruye la Activity y la vuelve a crear. Esto significa que:

1. Se llama a `onDestroy()` de la Activity actual
2. Se crea una nueva instancia de la Activity
3. Se llama a `onCreate()` de la nueva Activity
4. **¡Todas las variables se reinician!** 😱

```
┌─────────────────┐         ┌─────────────────┐
│    Activity     │         │    Activity     │
│   contador = 5  │  ROTA   │   contador = 0  │
│                 │ ──────► │                 │
│  onDestroy()    │         │   onCreate()    │
└─────────────────┘         └─────────────────┘
        ↓                           ↓
    Se destruye              Se crea nueva
```

## 💻 Código

### MainActivity.kt

```kotlin
package com.example.contadorapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.contadorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Variable que guarda el contador
    private var contador = 0

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar el valor inicial
        actualizarContador()

        // Configurar los botones
        binding.btnIncrementar.setOnClickListener {
            contador++
            actualizarContador()
        }

        binding.btnDecrementar.setOnClickListener {
            contador--
            actualizarContador()
        }
    }

    private fun actualizarContador() {
        binding.tvContador.text = contador.toString()
    }
}
```

### activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/tvContador"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="0"
        android:textSize="72sp"
        android:textStyle="bold"
        app:layout_constraintBottom_toTopOf="@id/btnIncrementar"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_chainStyle="packed" />

    <Button
        android:id="@+id/btnIncrementar"
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:text="+ Incrementar"
        app:layout_constraintBottom_toTopOf="@id/btnDecrementar"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvContador" />

    <Button
        android:id="@+id/btnDecrementar"
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="- Decrementar"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/btnIncrementar" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## 🧪 Prueba el problema

1. Ejecuta la aplicación
2. Pulsa varias veces el botón "Incrementar" hasta llegar a 5
3. **Rota el dispositivo** (Ctrl+F11 en el emulador)
4. Observa cómo el contador vuelve a 0 😢

## ❓ ¿Por qué ocurre esto?

El problema está en el **ciclo de vida de la Activity**:

```kotlin
private var contador = 0  // ← Esta variable vive en la Activity
```

Cuando la Activity se destruye (al rotar), esta variable desaparece con ella. Al crear la nueva Activity, `contador` se inicializa de nuevo a 0.

## 🔧 Soluciones posibles (pero no ideales)

### Opción 1: onSaveInstanceState

```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt("contador", contador)
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // ...
    contador = savedInstanceState?.getInt("contador") ?: 0
}
```

**Problema**: Solo funciona para datos simples y pequeños. No es escalable.

### Opción 2: Bloquear la rotación

```xml
<activity android:configChanges="orientation|screenSize">
```

**Problema**: Mala práctica. No soluciona el problema real, solo lo oculta.

## ✅ La solución correcta: ViewModel

En la siguiente iteración veremos cómo **ViewModel** resuelve este problema de forma elegante y escalable.

---

**Siguiente**: [Iteración 2: Primer ViewModel](../02_primer_viewmodel/)
