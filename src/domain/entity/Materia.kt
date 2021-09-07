package domain.entity

import java.util.*

data class Materia(
    var nombreDeMateria: String,
    var codigoDeMateria: String,
    var fechaDeEliminacion: Date? = null
) {

    fun eliminaMateria() {
        fechaDeEliminacion = Date()
    }


}
