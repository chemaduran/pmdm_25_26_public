package com.mm.iniciokotlin


fun main() {
//    getMonth(13)
//    println(getSemestreDevolviendo(7))
 result(false)
}

fun result(cualquierValor: Any) {
    when(cualquierValor) {
        is Int -> println(cualquierValor + cualquierValor)
        is String -> println(cualquierValor)
        is Boolean -> if (cualquierValor) println("Está el Any a true")
    }
}


fun getSemestreDevolviendo(month: Int) = when (month) {
    in 1..6 -> "primer semestre"
    in 7..12 -> "segundo semestre"
    else -> "no es un mes válido"
}


fun getSemestre(month: Int) {
    when (month) {
        in 1..6 -> println("primer semestre")
        in 7..12 -> println("segundo semestre")
        else -> println("no es un mes válido")
    }
}

fun getTrimestre(month: Int) {
    when (month) {
        1, 2, 3 -> println("primer trimestre")
        4, 5, 6 -> println("segundo trimestre")
        7, 8, 9 -> println("tercer trimestre")
        10, 11, 12 -> println("cuarto trimestre")
        else -> println("no es un mes válido")
    }
}

fun getMonth(month: Int) {
    when (month) {
        1 -> println("enero")
        2 -> println("febrero")
        3 -> println("marzo")
        4 -> println("abril")
        5 -> println("mayo")
        6 -> println("junio")
        7 -> println("julio")
        8 -> println("agosto")
        9 -> println("septiembre")
        10 -> println("octubre")
        11 -> println("noviembre")
        12 -> println("diciembre")
        else -> println("no es un mes válido")
    }


}