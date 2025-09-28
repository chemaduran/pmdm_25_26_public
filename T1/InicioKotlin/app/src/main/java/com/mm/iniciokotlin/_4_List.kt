package com.mm.iniciokotlin

fun main() {
    mutableList()
}

fun mutableList() {
    val weekDays: MutableList<String> =
        mutableListOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    weekDays.add(3, "Chércoles")
    println(weekDays)

    if (weekDays.isEmpty()) {
        println("Está la lista vacía")
    } else {
        weekDays.forEach { println(it) }
    }

    if (weekDays.isNotEmpty()) {
        weekDays.forEach { println(it) }
    }

    weekDays.last()

    for (valor in weekDays) {
        println(valor)
    }

    weekDays.forEach { it } // Aquí tiene que ser "it" obligatoriamente
}

fun inmutableList() {
    val readOnly: List<String> =
        listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    println(readOnly.size)
    println(readOnly)
    println(readOnly[0])
    println(readOnly.last())
    println(readOnly.first())

    // Filtrar
    val example = readOnly.filter { it.contains("a") }
    println(example)

    // Iterar
    readOnly.forEach { weekDay -> println(weekDay) }
}