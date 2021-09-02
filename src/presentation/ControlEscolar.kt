package main.ui

import domain.entity.Grupo
import main.data.Profesor
import domain.exception.agrega_materia.MateriaPreviamenteAgregadaException
import main.presentation.manager.ManagerInteraccion
import main.presentation.ui.getInformation
import java.util.*
import kotlin.system.exitProcess

/**
 *
 */
class ControlEscolar(
    private val profesor: Profesor,
) {

    /**
     * Punto de entrada para la ejecucion del programa
     */
    fun execute() {
        saludoProfesor()
        muestraMenu()
        val opcionSeleccionada: Int? = ManagerInteraccion.getOption()
        when (opcionSeleccionada) {
            0 -> finalizaPrograma()
            1 -> agregaUnaMateria()
            2 -> ejecutaFlujoParaEliminarUnaMateria()
            3 -> agregaUnGrupo()
            4 -> ejecutaFlujoParaEliminarUnGrupo()
            5 -> inscribiralumno()
            6 -> {}//eliminaUnAlumno()
            7 -> consultaListaDeMaterias()
            8 -> {}//consultaListaDeGrupos()
            9 -> {}//asignarEvaluaciones()
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
        1. Agregar una materia
        2. Elimina una materia
        3. Agregar un grupo
        4. Elimina un grupo
        5. Inscribir un Alumno
        6. Eliminar un Alumno
        7. Consultar lista de Materias
        8. Consultar lista de Grupos
        9. Asignar evaluaciones
        10. Iniciar Curso
        11. Finalizar Curso
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
     *  Solicita al usuario los datos nombre de la materia y codigo de materia, al crear la instancia materia se valida
     *  que que no se cuente con alguna agregada anteriormente.
     *
     *  @throws MateriaPreviamenteAgregadaException cuando la materia ya se encuentra dentro de la lista, se indica al usuario y se brinda la
     *  posibilidad de intentar con otra materia.
     */
    @Throws(MateriaPreviamenteAgregadaException::class)
    private fun agregaUnaMateria() {
        ManagerInteraccion.cleanInput()
        println("Por favor indique el nombre de la materia")
        val nombreDeLaMateria: String = ingresaTexto()
        println("Por favor indique el codigo de la materia")
        val codigoDeLaMateria: String = ingresaTexto()
        try {
            profesor.agregarMateria(nombreDeLaMateria, codigoDeLaMateria)
        } catch (e: MateriaPreviamenteAgregadaException) {
            val title: String =
                "La materia $nombreDeLaMateria con codigo $codigoDeLaMateria ya se encuentra en la lista"
            val content: String = "¿Desea intentar con otra materia?"
            redireccionaFlujo(title, content, action = this::agregaUnaMateria)
            return
        }
        val title: String = "La materia $nombreDeLaMateria con codigo $codigoDeLaMateria se ha agregado con exito"
        val content: String = "¿Desea agregar otra materia?"
        redireccionaFlujo(title, content, action = ::agregaUnaMateria)
    }

    /**
     * Despliega un menu de materias que aun no han sido eliminadas y brinda la posibilidad de comenzar el flujo para
     * eliminar alguna de las mostradas.
     */
    fun ejecutaFlujoParaEliminarUnaMateria() {
        ManagerInteraccion.cleanInput()
        val listaDeMaterias = profesor.getMaterias().filter { it.fechaDeEliminacion != null }
        println("Selecciona la materia que deseas eliminar")
        listaDeMaterias.forEachIndexed { index, materia ->
            println("${index.inc()} $materia")
        }
        val opcionSeleccionada = ManagerInteraccion.getOption() ?: -1
        if (opcionSeleccionada !in 1..listaDeMaterias.size) {
            println("Por favor selecciona una opcion dentro del menu")
            ejecutaFlujoParaEliminarUnaMateria()
        }
        val materiaSeleccionada = listaDeMaterias.get(opcionSeleccionada.dec())
        materiaSeleccionada.eliminaMateria()
    }

    /**
     *  Solicita al usuario los datos nombre del grupo y codigo del grupo este ultimo en formato "YYYY-(01/01)",
     *  al crear la instancia grupo se valida que no se cuente con alguno agregado anteriormente.
     *
     *  @throws GrupoPreviamenteAgregadoExcetion cuando la materia ya se encuentra dentro de la lista, se indica al usuario y se brinda la
     *  posibilidad de intentar con otra materia.
     */
    private fun agregaUnGrupo() {
        ManagerInteraccion.cleanInput()
        println("Por favor indique el nombre del Grupo")
        val nombreDelGrupo: String = ingresaTexto()
        println("Por favor indique el ciclo escolar")
        val cicloEscolar: String = ingresaTexto()
        try {
            profesor.agregarGrupo(nombreDelGrupo, cicloEscolar)
        } catch (e: MateriaPreviamenteAgregadaException) {
            val title: String = "El grupo $nombreDelGrupo con ciclo escolar $cicloEscolar ya se encuentra en la lista"
            val content: String = "¿Desea intentar con otra materia?"
            redireccionaFlujo(title, content, action = ::agregaUnGrupo)
            return
        }
        val title: String = "\"El grupo $nombreDelGrupo con ciclo escolar $cicloEscolar se ha agregado con exito"
        val content: String = "¿Desea agregar con otro Grupo?"
        redireccionaFlujo(title, content, action = ::agregaUnGrupo)
    }

    /**
     * Despliega un menu de grupos que aun no han sido eliminadas y brinda la posibilidad de comenzar el flujo para
     * eliminar alguno de las mostrados.
     */
    fun ejecutaFlujoParaEliminarUnGrupo() {
        val listaDeGrupos = profesor.getGrupos().filter { it.fechaDeEliminacion == null }
        if (listaDeGrupos.isEmpty()) {
            val title: String = "Aun no has dado de alta algun grupo"
            val content: String = "¿Quieres crear un grupo?"
            redireccionaFlujo(title,content,action = ::agregaUnGrupo)
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
                redireccionaMenu()
            }
        }
    }

    /**
     *  Despliega al usuario la lista de materias agregadas y en caso de no haber aguna permite al usuario agregarla.
     */
    fun consultaListaDeMaterias() {
        val listaDeMaterias = profesor.getMaterias().filter { it.fechaDeEliminacion == null }
        if (listaDeMaterias.isEmpty()) {
            val title: String = "Aun no hay materias inscritas"
            val content: String = "¿Desea agregar una materia?"
            redireccionaFlujo(title, content, action = ::agregaUnaMateria)
            return
        }
        println("Las materias inscritas hasta el momento son:")
        listaDeMaterias.forEachIndexed { index, materia ->
            println("${index.inc()} ${materia.getInformation()}")
        }
        redireccionaMenu()
    }

    /**
     *  Solicita al usuario el nombre y numero de cuenta del alumno, despliega una lista de grupos filtrada a grupos
     *  en los que aun no se da inicio el semestre, al crear la instancia Alumno se valida que no se encuentre inscrito
     *  anteriormente a esa instancia de grupo.
     *
     *  @throws AlumnoPreviamenteInscritoException cuando la materia ya se encuentra dentro de la lista, se indica al
     *  usuario y se brinda la posibilidad de intentar con otra materia.
     */
    fun inscribiralumno() {
        println("Por favor seleccione el grupo al que desea inscribir al alumno")
        val gruposDisponibles = profesor.getGrupos()
        gruposDisponibles.forEachIndexed { index, grupo ->
            var index = 0
            index++
            println("$index $grupo")
        }
    }

    /**
     * Inicia la fecha de inicio del curso
     */
    fun ejecutaFlujoParaIniciarCurso(){
        ManagerInteraccion.cleanInput()
        println("Selecciona el grupo para iniciar el curso")
        val listaDeGrupos = profesor.getGrupos().filterNot { it.haIniciado() }
        if (listaDeGrupos.isEmpty()){
            val title: String = "Aun no ha dado de alta algun grupo"
            val content: String = "¿Desea dar de alta un grupo?"
            redireccionaFlujo(title,content,action = ::agregaUnGrupo)
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
     * Inicializa la fecha de termino del curso
     */
    fun ejecutaFlujoParaFinalizarElCurso(){
        ManagerInteraccion.cleanInput()
        println("Selecciona el grupo para finalizar el curso")
        val listaDeGrupos = profesor.getGrupos().filter { it.haIniciado() }
        if (listaDeGrupos.isEmpty()){
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
    private fun redireccionaFlujo(
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
    private fun redireccionaMenu() {
        ManagerInteraccion.cleanInput()
        println("Presiona Enter para continuar")
        ManagerInteraccion.awaitForEnterKeyInteraction()
        execute()
    }


    /**
     * Proporciona al usuario la opcion de interactuar con el sistema por medio de un String.
     */
    private fun ingresaTexto(): String = ManagerInteraccion.getNextLine()


}