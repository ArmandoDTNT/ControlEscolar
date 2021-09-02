package main.presentation.manager

import main.data.Profesor
import main.presentation.ui.getInformation

class ManagerMateria(
    private val profesor: Profesor,
) {

    /**
     *  Despliega al usuario la lista de materias agregadas.
     */
    fun consultaListaDeMaterias() {
        val listaDeMaterias = profesor.getMaterias().filter { it.fechaDeEliminacion == null }
        if (listaDeMaterias.isEmpty()) {
            println("Aun no hay materias inscritas")
        } else {
            println("Las materias inscritas hasta el momento son:")
            listaDeMaterias.forEachIndexed { index, materia ->
                println("${index.inc()} ${materia.getInformation()}")
            }
        }
    }

}