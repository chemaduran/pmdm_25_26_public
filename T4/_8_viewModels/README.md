# Tutorial: ViewModels en Android con Kotlin

## 📚 Introducción

Este tutorial te guiará paso a paso en el aprendizaje de **ViewModels** en Android, uno de los componentes más importantes de la arquitectura moderna de aplicaciones Android.

## 🎯 Objetivos de aprendizaje

Al finalizar este tutorial serás capaz de:

- Entender qué es un ViewModel y por qué es necesario
- Crear y utilizar ViewModels en tus aplicaciones
- Implementar LiveData para observar cambios en los datos
- Usar StateFlow como alternativa moderna a LiveData
- Crear ViewModels con parámetros usando ViewModelFactory

## 📋 Requisitos previos

- Conocimientos básicos de Kotlin
- Android Studio instalado
- Conocimientos básicos de Activities y su ciclo de vida

## 🗂️ Estructura del tutorial

El tutorial está organizado en **5 iteraciones progresivas**, cada una construyendo sobre la anterior:

| Iteración | Tema | Descripción |
|-----------|------|-------------|
| [01_sin_viewmodel](./01_sin_viewmodel/) | Sin ViewModel | El problema: pérdida de estado al rotar |
| [02_primer_viewmodel](./02_primer_viewmodel/) | Primer ViewModel | Solución básica con ViewModel |
| [03_livedata](./03_livedata/) | LiveData | Observando cambios reactivamente |
| [04_stateflow](./04_stateflow/) | StateFlow | Alternativa moderna con Kotlin Flow |
| [05_viewmodel_factory](./05_viewmodel_factory/) | ViewModelFactory | ViewModels con parámetros |

## 🚀 Cómo usar este tutorial

1. **Lee el README.md** de cada iteración para entender los conceptos
2. **Examina el código** del proyecto de ejemplo
3. **Ejecuta la aplicación** para ver el comportamiento
4. **Experimenta** modificando el código

## 📱 Proyecto de ejemplo

A lo largo de todas las iteraciones trabajaremos con una **aplicación de contador** que irá evolucionando:

- **Iteración 1**: Contador simple que pierde el valor al rotar
- **Iteración 2**: Contador que mantiene el valor con ViewModel
- **Iteración 3**: Contador con actualización reactiva usando LiveData
- **Iteración 4**: Contador moderno con StateFlow
- **Iteración 5**: Contador con valor inicial configurable

## 💡 Consejos

> **Tip**: Prueba a rotar el dispositivo (Ctrl+F11 en el emulador) en cada iteración para ver la diferencia de comportamiento.

---

**Autor**: Tutorial para alumnos de 2º DAM  
**Fecha**: Enero 2026
