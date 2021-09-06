package main.presentation

import domain.entity.Grupo
import domain.exception.agrega_materia.MateriaPreviamenteAgregadaException
import main.data.Profesor
import main.presentation.manager.ManagerAlumno
import main.presentation.manager.ManagerGrupo
import main.presentation.manager.ManagerInteraccion
import main.presentation.manager.ManagerMateria
import java.util.*
import kotlin.system.exitProcess

/**
 *
 */
class ControlEscolar(
    private val profesor: Profesor,
) {

    /* */
    private val managerMateria: ManagerMateria = ManagerMateria(profesor)
    private val managerGrupo: ManagerGrupo = ManagerGrupo(profesor)
    private val managerAlumno: ManagerAlumno = ManagerAlumno(profesor)

    /**
     * Punto de entrada para la ejecucion del programa
     */
    fun execute() {
        saludoProfesor()
        muestraMenu()
        val opcionSeleccionada: Int? = ManagerInteraccion.getOption()
        when (opcionSeleccionada) {
            0 -> finalizaPrograma()
            1 -> managerMateria.agregaUnaMateria()
            2 -> {
                managerMateria.ejecutaFlujoParaEliminarUnaMateria()
                ManagerInteraccion.awaitForEnterKeyInteraction()
            }
            3 -> {
                managerMateria.consultaListaDeMaterias()
                ManagerInteraccion.awaitForEnterKeyInteraction()
            }
            4 -> managerGrupo.agregaUnGrupo()
            5 -> {
                managerGrupo.ejecutaFlujoParaEliminarUnGrupo()
                ManagerInteraccion.awaitForEnterKeyInteraction()
            }
            6 -> {
            }//eliminaUnAlumno()
            7 -> {
            }
            8 -> {
            }//consultaListaDeGrupos()
            9 -> {
            }//asignarEvaluaciones()
            10 -> ejecutaFlujoParaIniciarCurso()
            11 -> ejecutaFlujoParaFinalizarElCurso()
            null -> {
            }
            else -> {
            }
        }

    }

    /**
     * Da la bienvenida al usuario del programa
     */
    private fun saludoProfesor() {
        println("Bienvenido profesor \"Roberto\"")
        println("¿Que desea hacer?")
    }

    /**
     * Implime el menu con las acciones que puede realizar el programa
     */
    private fun muestraMenu() {
        val menu: String = """
        . Agregar una materia
        . Elimina una materia
        . Consulta lista de materias
        . Elimina un grupo
        . Inscribir un Alumno
        . Eliminar un Alumno
        . Consultar lista de Materias
        . Consultar lista de Grupos
        . Asignar evaluaciones
        . Iniciar Curso
        . Finalizar Curso
        0. Finalizar Programa
    """.trimIndent()
        println(menu)
    }

    /**
     * Imprime un mensaje de despedida y finaliza la ejecucion del programa
     */
    private fun finalizaPrograma() {
        println("Hasta la proxima profesor \"Roberto\"")
        exitProcess(0)
    }

    /**
     * Inicia el flujo para asignar la fecha de inicio del curso
     */
    fun ejecutaFlujoParaIniciarCurso() {
        ManagerInteraccion.cleanInput()
        println("Selecciona el grupo para iniciar el curso")
        val listaDeGrupos = profesor.getGrupos().filterNot { it.haIniciado() }
        if (listaDeGrupos.isEmpty()) {
            val title: String = "Aun no ha dado de alta algun grupo"
            return
        }
        listaDeGrupos.forEachIndexed { index, grupo ->
            println("${index.inc()} $grupo")
        }
        val opcionSeleccionada: Int = ManagerInteraccion.getOption() ?: -1
        when {
            opcionSeleccionada !in 1..listaDeGrupos.size -> {
                println("Por favor selecciona una opcion valida del menu")
                ejecutaFlujoParaIniciarCurso()
            }
            else -> {
                val grupoSeleccionado: Grupo = listaDeGrupos.get(opcionSeleccionada.dec())
                grupoSeleccionado.iniciaCurso()
                println("El grupo $grupoSeleccionado, ha iniciado el curso exitosamente")
                redireccionaMenu()
            }
        }
    }

    /**
     * Inicializa flujo para fijar la fecha de termino del curso
     */
    fun ejecutaFlujoParaFinalizarElCurso() {
        ManagerInteraccion.cleanInput()
        println("Selecciona el grupo para finalizar el curso")
        val listaDeGrupos = profesor.getGrupos().filter { it.haIniciado() }
        if (listaDeGrupos.isEmpty()) {
            println("Aun no hay grupos que hayan iniciado el semestre")
            redireccionaMenu()
        }
        listaDeGrupos.forEachIndexed { index, grupo ->
            println("${index.inc()} $grupo")
        }
        val opcionSeleccionada: Int = ManagerInteraccion.getOption() ?: -1
        when {
            opcionSeleccionada !in 1..listaDeGrupos.size -> {
                println("Por favor selecciona una opcion valida del menu")
                ejecutaFlujoParaIniciarCurso()
            }
            else -> {
                val grupoSeleccionado: Grupo = listaDeGrupos.get(opcionSeleccionada.dec())
                grupoSeleccionado.iniciaCurso()
                println("El grupo $grupoSeleccionado, ha finalizado el curso exitosamente")
                redireccionaMenu()
            }
        }
    }

    /**
     * Se presenta la informacion de [title] y [content] al usuario y se brinda la [action] para poder redireccionar
     * al flujo deseado.
     */
    fun redireccionaFlujo(
        title: String,
        content: String,
        action: () -> Unit
    ) {
        //
        val menuReintentar: String = """
            $title
            $content
            
            1) Si
            2) No
        """.trimIndent()
        println(menuReintentar)
        //
        var opcionSeleccionada: Int? = ManagerInteraccion.getOption()
        while (opcionSeleccionada != 1 && opcionSeleccionada != 2) {
            println("La opcion elegida no esta en el menu, por favor selecciona una opcion valida")
            ManagerInteraccion.cleanInput()
            opcionSeleccionada = ManagerInteraccion.getOption()
        }
        //
        when (opcionSeleccionada) {
            1 -> action()
            2 -> execute()
        }
    }

    /**
     * Se presenta la informacion de [title] y [content] al usuario y se brinda la [action] para poder redireccionar
     * al flujo deseado.
     */
    fun redireccionaMenu() {
        ManagerInteraccion.cleanInput()
        println("Presiona Enter para continuar")
        ManagerInteraccion.awaitForEnterKeyInteraction()
        execute()
    }
}