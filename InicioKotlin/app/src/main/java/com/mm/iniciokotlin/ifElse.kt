package com.mm.iniciokotlin

fun main() {
//    ifBasico()
//    ifAnidado()
//    ifBoolean()
//    ifInt()
}

fun ifBasico() {
    val name = "Chema"

    if (name == "Chema") {
        println("Eres Chema!")
    } else {
        println("No eres Chema!")
    }
}

fun ifAnidado() {
    val animal = "Perrete"

    if (animal == "Perrete") {
        println("Eres un perrete!")
    } else if (animal == "Gatete") {
        println("Eres un gatete")
    } else if (animal == "Pajarete") {
        println("Eres un pajarete")
    } else {
        println("No eres nada")
    }

}

fun ifBoolean() {
    var claseDivertida: Boolean = false

    if (!claseDivertida) {
        println("Esta clase está siendo aburrida")
    }
}

fun ifInt() {
    var edad: Int = 43

    if (edad >= 18) {
        println("Eres mayor de edad")
    } else {
        println("Eres un yogurín")
    }
}

fun ifMultiple() {
    var edad: Int = 16
    var permisoTutor: Boolean = true

    if (edad <= 18 && permisoTutor) {
        println("Ok, puedes ir a la excursión")
    }
}