package com.mm.iniciokotlin

fun main() {
    // Esto se puede ejecutar normalmente.
    var nombre: String = "Chema"
    println(nombre.get(3))

    // Kotlin tiene protección contra valores nulos, porque son peligrosos. Esto, no lo puedo hacer
    // ni siquiera compila
//    var name2: String = null


    // Puedo forzarlo, y decirle que puedo tener un String (o cualquier otro tipo) que sea nullable,
    // con el operador ?
    var string_nula: String? = null

    // ¿Ahora podría acceder al string?...
//    println(name2.get(3))
    // ... NO ME LO PERMITE!! Intenta protegerte frente a valores nulos. PERO, existe el operador !!
    // con el que le dices que FUERCE el acceso al valor, porque el programador (tú) está seguro de
    // que ese valor no es nulo en ese momento de ejecución.
//     println(name2!![3])  // Esto aquí me lo permite compilar, pero al ejecutar...BOOM!

    // Aquí sin embargo, primero pregunta si es nulo...e intenta acceder, y si no lo es, devuelve valor nulo (no explota)
    println(string_nula?.get(3))

    // Si queremos cambiar el comportamiento, dependiendo de si es nulo o no un valor, debemos usar el operador "Elvis"

    println(string_nula?.get(3) ?: "la cadena es nula")

}