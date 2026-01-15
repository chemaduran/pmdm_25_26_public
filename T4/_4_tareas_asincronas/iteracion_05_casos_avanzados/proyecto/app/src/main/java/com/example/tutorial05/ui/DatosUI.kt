package com.example.tutorial05.ui

import com.example.tutorial05.data.model.DatosCombinados
import com.example.tutorial05.data.model.Usuario

/**
 * Tipo de datos que puede mostrar la UI.
 */
sealed class DatosUI {
    data class Usuarios(val lista: List<Usuario>) : DatosUI()
    data class DatosMixtos(val datos: DatosCombinados) : DatosUI()
}
