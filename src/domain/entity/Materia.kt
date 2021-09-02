package domain.entity

import java.util.*

data class Materia(
    val nombreDeMateria: String,
    val codigoDeMateria: String,
    var fechaDeEliminacion: Date? = null
) {

    fun eliminaMateria() {
        fechaDeEliminacion = Date()
    }

    fun eliminada() {
        val eliminada: Boolean = fechaDeEliminacion != null
    }

}
