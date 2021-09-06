package main.presentation.manager

import domain.exception.agrega_materia.MateriaPreviamenteAgregadaException
import main.data.Profesor
import main.presentation.ControlEscolar
import main.presentation.ui.getInformation

class ManagerMateria(
    private val profesor: Profesor,
) {

    /* */

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

    /**
     *  Solicita al usuario los datos nombre de la materia y codigo de materia, al crear la instancia materia se valida
     *  que que no se cuente con alguna agregada anteriormente.
     *
     *  @throws MateriaPreviamenteAgregadaException cuando la materia ya se encuentra dentro de la lista, se indica
     *  al usuario y se brinda la posibilidad de intentar con otra materia.
     */
    @Throws(MateriaPreviamenteAgregadaException::class)
    fun agregaUnaMateria() {
        ManagerInteraccion.cleanInput()
        println("Por favor indique el nombre de la materia")
        val nombreDeLaMateria: String = ManagerInteraccion.getNextLine()
        println("Por favor indique el codigo de la materia")
        val codigoDeLaMateria: String = ManagerInteraccion.getNextLine()
        try {
            profesor.agregarMateria(nombreDeLaMateria, codigoDeLaMateria)
        } catch (e: MateriaPreviamenteAgregadaException) {
            val title: String =
                "La materia $nombreDeLaMateria con codigo $codigoDeLaMateria ya se encuentra en la lista"
            val content: String = "¿Desea intentar con otra materia?"
            return
        }
        val title: String = "La materia $nombreDeLaMateria con codigo $codigoDeLaMateria se ha agregado con exito"
        val content: String = "¿Desea agregar otra materia?"
    }

    /**
     * Despliega un menu de materias que aun no han sido eliminadas y brinda la posibilidad de comenzar el flujo para
     * eliminar alguna de las mostradas.
     */
    fun ejecutaFlujoParaEliminarUnaMateria() {
        ManagerInteraccion.cleanInput()
        val listaDeMaterias = profesor.getMaterias().filter { it.fechaDeEliminacion == null }
        if (listaDeMaterias.isEmpty()) {
            println("Aun no hay materias que puedas eliminar")
        } else {
            println("Selecciona la materia que deseas eliminar")
            listaDeMaterias.forEachIndexed { index, materia ->
                println("${index.inc()} $materia")
            }
            val opcionSeleccionada = ManagerInteraccion.getOption() ?: -1
            if (opcionSeleccionada !in 1..listaDeMaterias.size) {
                println("Por favor selecciona una opcion dentro del menu")
                ejecutaFlujoParaEliminarUnaMateria()
            } else {
                val materiaSeleccionada = listaDeMaterias.get(opcionSeleccionada.dec())
                materiaSeleccionada.eliminaMateria()
                println("La materia $materiaSeleccionada se ha eliminado con exito")
            }
        }
    }

}