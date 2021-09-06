package main.presentation.manager

import domain.exception.agrega_materia.MateriaPreviamenteAgregadaException
import main.data.Profesor
import main.presentation.ControlEscolar

class ManagerGrupo (
    private val profesor: Profesor
    ){

    private val controlEscolar: ControlEscolar = ControlEscolar(profesor)

    /**
     *  Solicita al usuario los datos nombre del grupo y codigo del grupo este ultimo en formato "YYYY-(01/01)",
     *  al crear la instancia grupo se valida que no se cuente con alguno agregado anteriormente.
     *
     *  @throws GrupoPreviamenteAgregadoExcetion cuando la materia ya se encuentra dentro de la lista, se indica al usuario y se brinda la
     *  posibilidad de intentar con otra materia.
     */
    fun agregaUnGrupo() {
        ManagerInteraccion.cleanInput()
        println("Por favor indique el nombre del Grupo")
        val nombreDelGrupo: String = ManagerInteraccion.getNextLine()
        println("Por favor indique el ciclo escolar")
        val cicloEscolar: String = ManagerInteraccion.getNextLine()
        try {
            profesor.agregarGrupo(nombreDelGrupo, cicloEscolar)
        } catch (e: MateriaPreviamenteAgregadaException) {
            val title: String = "El grupo $nombreDelGrupo con ciclo escolar $cicloEscolar ya se encuentra en la lista"
            val content: String = "¿Desea intentar con otra materia?"
            controlEscolar.redireccionaFlujo(title, content, action = ::agregaUnGrupo)
        }
        val title: String = "\"El grupo $nombreDelGrupo con ciclo escolar $cicloEscolar se ha agregado con exito"
        val content: String = "¿Desea agregar con otro Grupo?"
        controlEscolar.redireccionaFlujo(title, content, action = ::agregaUnGrupo)
    }

    /**
     * Despliega un menu de grupos que aun no han sido eliminadas y brinda la posibilidad de comenzar el flujo para
     * eliminar alguno de las mostrados.
     */

    fun ejecutaFlujoParaEliminarUnGrupo() {
        val listaDeGrupos = profesor.getGrupos().filter { it.fechaDeEliminacion == null }
        if (listaDeGrupos.isEmpty()) {
            val title: String = "Aun no has dado de alta algun grupo"
            return
        }
        listaDeGrupos.forEachIndexed { index, grupo ->
            println("${index.inc()} $grupo")
        }
        val opcionSeleccionada: Int = ManagerInteraccion.getOption() ?: -1
        when {
            opcionSeleccionada in 1..listaDeGrupos.size ->{
                println("Por favor selecciona una opcion valida dentro del menu")
                ejecutaFlujoParaEliminarUnGrupo()
            }
            else -> {
                val grupoSeleccionado = listaDeGrupos.get(opcionSeleccionada.dec())
                println("El grupo $grupoSeleccionado, se ha eliminado con exito")
                controlEscolar.redireccionaMenu()
            }
        }
    }


}