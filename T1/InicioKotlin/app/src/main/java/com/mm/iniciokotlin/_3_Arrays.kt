package com.mm.iniciokotlin

fun main() {
    // Índice 0-7
    // Tamaño 8
    val weekDays = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    // Imprimir un valor concreto del array
    println(weekDays[0])

    // Imprimir el tamaño del array
    println(weekDays.size)

    // Tamaños
    if (weekDays.size >= 8) {
        println(weekDays[7])
    } else {
        println("No hay mas valores en el array")
    }

    // Modificar valores
    weekDays[0] = "Feliz lunes"
    println(weekDays.get(0))  // es lo mismo que weekDays[0]


    // Recorrer arrays. Obtenemos solamente el índice.
    for(position in weekDays.indices){
       println(weekDays[position])
    }

    // Recorrer arrays. Obtenemos solamente tanto el índice como el valor de cada uno de los elementos
    for((position, value) in weekDays.withIndex()){
        println("La posición $position, contiene $value")
    }


    // Recorrer arrays. Solamente con los valores
    for (valor in weekDays){
        println("Ahora es $valor")
    }

    // También podemos recorrer con un forEach de manera funcional
    weekDays.forEach { it }
}