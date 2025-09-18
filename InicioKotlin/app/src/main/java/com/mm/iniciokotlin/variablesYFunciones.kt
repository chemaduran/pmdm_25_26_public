package com.mm.iniciokotlin

fun main() {
    showMyNameSinParametros()
    showMyNameConParametros("Antonio")
    showMyNameConSalida("Fran")
    println(showMyNameConSalidaSimplificada("Olga"))

    tiposDeKotlin()
}

fun showMyNameSinParametros() {
    // con var, declaro una variable que puedo modificar su valor
    // en las siguientes líneas de código
    var nombre:String = "Chema"
    nombre = "Kevin"
    println("Mi nombre es $nombre")

    // con val, declaro una variable INMUTABLE (es decir, una constante).
    // Una vez que asigno el valor, no puedo modificarlo
    val otroNombre: String = "Pepito"
    // otroNombre = "Juanito" // no válido
}

fun showMyNameConParametros(nombre: String) {
    println("Mi nombre es $nombre")
}

fun showMyNameConSalida(nombre: String): String {
    val resultado = "Mi nombre es $nombre"
    return resultado
}

fun showMyNameConSalidaSimplificada(nombre: String) = "Mi nombre es $nombre"

fun tiposDeKotlin() {
    val charEjemplo: Char = 'a'
    val enteroEjemplo: Int = 23
    val shortEjemplo: Short = 24
    val byteEjemplo: Byte = 25
    val longEjemplo: Long = 232425425
    val floatEjemplo: Float = 23.5f
    val doubleEjemplo: Double = 23.5
    val stringEjemplo: String = "34"
    val booleanEjemplo: Boolean = true
    val cualquierTipo: Any = 23.7  // acepta cualquier tipo (se convierte)

    val stringConvertida: String = enteroEjemplo.toString()
    val enteroConvertido: Int = stringEjemplo.toInt()
}

fun operacionesAritmeticas() {
    // Exactamente igual que en Java
}
