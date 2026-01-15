# Tutorial: Tareas Asíncronas en Android con Kotlin

## 📚 Índice del Tutorial

Este tutorial te guiará paso a paso por el mundo de las tareas asíncronas en Android, utilizando Kotlin y las mejores prácticas actuales.

### Iteraciones

| Iteración | Tema | Dificultad | Descripción |
|-----------|------|------------|-------------|
| [01](./iteracion_01_corrutinas_basicas/README.md) | Corrutinas Básicas | 🟢 Principiante | Introducción a las corrutinas y `viewModelScope` |
| [02](./iteracion_02_estados_ui/README.md) | Estados de UI | 🟢 Principiante | Manejo de estados (Loading, Success, Error) |
| [03](./iteracion_03_repository_pattern/README.md) | Repository Pattern | 🟡 Intermedio | Arquitectura limpia con repositorios |
| [04](./iteracion_04_flow/README.md) | Flow y StateFlow | 🟡 Intermedio | Flujos reactivos de datos |
| [05](./iteracion_05_casos_avanzados/README.md) | Casos Avanzados | 🔴 Avanzado | Retry, timeout, múltiples llamadas |

---

## 🎯 Objetivos del Tutorial

Al completar este tutorial, serás capaz de:

1. **Entender** qué son las corrutinas y por qué son importantes
2. **Implementar** tareas asíncronas de forma correcta y segura
3. **Manejar** estados de UI durante operaciones asíncronas
4. **Aplicar** patrones de arquitectura limpia (Repository, UseCase)
5. **Utilizar** Flow para flujos de datos reactivos
6. **Resolver** casos de uso avanzados (retry, timeout, operaciones paralelas)

---

## 🛠️ Requisitos Previos

- Android Studio Hedgehog (2023.1.1) o superior
- Conocimientos básicos de Kotlin
- Conocimientos básicos de Android (Activities, ViewModels)
- JDK 17 o superior

---

## 📖 Conceptos Clave

### ¿Por qué Tareas Asíncronas?

En Android, el **hilo principal (Main Thread)** es responsable de:
- Renderizar la interfaz de usuario
- Procesar eventos del usuario (toques, gestos)

Si ejecutamos operaciones largas en el hilo principal:
- La UI se congela ❄️
- El sistema muestra el diálogo "La aplicación no responde" (ANR)
- Mala experiencia de usuario 😞

### Solución: Corrutinas de Kotlin

Las corrutinas son la solución moderna y recomendada por Google para manejar tareas asíncronas en Android.

```kotlin
// ❌ MAL: Bloquea el hilo principal
fun cargarDatos() {
    val datos = api.obtenerDatos() // Operación larga
    mostrarDatos(datos)
}

// ✅ BIEN: No bloquea el hilo principal
fun cargarDatos() {
    viewModelScope.launch {
        val datos = api.obtenerDatos() // Se ejecuta en background
        mostrarDatos(datos) // Se ejecuta en Main Thread
    }
}
```

---

## 🚀 ¡Comienza el Tutorial!

👉 [Ir a la Iteración 1: Corrutinas Básicas](./iteracion_01_corrutinas_basicas/README.md)

---

## 📝 Proyecto de Ejemplo

A lo largo del tutorial, construiremos una aplicación que:

1. **Simula** llamadas a una API
2. **Muestra** una lista de usuarios
3. **Maneja** estados de carga y errores
4. **Implementa** búsqueda en tiempo real

Cada iteración añade nuevas funcionalidades y mejora la arquitectura.

---

## 📚 Recursos Adicionales

- [Documentación oficial de Corrutinas](https://kotlinlang.org/docs/coroutines-overview.html)
- [Guía de Android sobre Corrutinas](https://developer.android.com/kotlin/coroutines)
- [Flow en Android](https://developer.android.com/kotlin/flow)

---

*Tutorial creado para PMDM 2025-2026*
