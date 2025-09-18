package com.mm.iniciokotlin

import java.util.Arrays

fun main() {
    val diasSemana =
        arrayOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo")

    println(diasSemana[2])
    println(diasSemana.get(0))

    println(diasSemana.size)

    println(diasSemana.contentToString())

    for (dia in diasSemana) {
        println(dia)
    }

    for (ind in diasSemana.indices) {
        println(diasSemana[ind])
    }

    for ((indice, valor) in diasSemana.withIndex()) {
        println("La posición $indice, contiene el valor $valor")
    }

    diasSemana.forEach {
        println(it)
    }


}

