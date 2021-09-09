package main.presentation.manager

import domain.entity.Grupo
import domain.exception.Manager_materia.MateriaPreviamenteAgregadaException
import domain.exception.manager_grupo.AlumnoPreviamenteInscritoException
import main.data.Profesor
import main.domain.exception.Manager_grupo.AlumnoNoEncontradoEnListaException
import main.domain.exception.Manager_grupo.NotAvailableGroupsException
import main.framework.contains
import kotlin.jvm.Throws

class ManagerGrupo(
    private val profesor: Profesor
) {

    /**
     *  Solicita al usuario los datos nombre del grupo y codigo del grupo,,
     *  al crear la instancia grupo se valida que no se cuente con alguno agregado anteriormente.
     *
     *  @throws GrupoPreviamenteAgregadoExcetion cuando la materia ya se encuentra dentro de la lista, se indica al usuario y se brinda la
     *  posibilidad de intentar con otra materia.
     */
    fun agregaUnGrupo(
        onComplete: (title: String, content: String, action: () -> Unit) -> Unit
    ) {
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
            onComplete(title, content) { agregaUnGrupo(onComplete) }
            return
        }
        val title: String = "\"El grupo $nombreDelGrupo con ciclo escolar $cicloEscolar se ha agregado con exito"
        val content: String = "¿Desea agregar con otro Grupo?"
        onComplete(title, content) { agregaUnGrupo(onComplete) }
    }

    /**
     * Despliega un menu de grupos que aun no han sido eliminadas y brinda la posibilidad de comenzar el flujo para
     * eliminar alguno de las mostrados.
     */
    fun ejecutaFlujoParaEliminarUnGrupo() {
        val listaDeGrupos = profesor.getGrupos().filter { it.fechaDeEliminacion == null }
        if (listaDeGrupos.isEmpty()) {
            println("Aun no has dado de alta algun grupo")
            return
        }
        listaDeGrupos.forEachIndexed { index, grupo ->
            println("${index.inc()} $grupo")
        }
        val opcionSeleccionada: Int = ManagerInteraccion.getInt() ?: -1
        when {
            opcionSeleccionada in 1..listaDeGrupos.size -> {
                println("Por favor selecciona una opcion valida dentro del menu")
                ejecutaFlujoParaEliminarUnGrupo()
            }
            else -> {
                val grupoSeleccionado = listaDeGrupos.get(opcionSeleccionada.dec())
                println("El grupo $grupoSeleccionado, se ha eliminado con exito")
            }
        }
    }

    /**
     * Se encarga de realizar validaciones mediante las cuales de ser exitosas permitan editar los atributos
     * [nombreDeGrupo] y [cicloEscolar] de un grupo.
     */
    fun editaUnGrupo(onComplete: () -> Unit) {
        val listaDeGrupos: List<Grupo> = profesor.getGrupos().filter { it.fechaDeInicio == null }
        if (listaDeGrupos.isEmpty()) {
            println("Aun no cuentas con grupos que puedas editar")
            onComplete.invoke()
            return
        }
        println("Selecciona el grupo que deseas editar")
        listaDeGrupos.forEachIndexed { index, grupo ->
            println(" ${index.inc()} ${grupo.nombreDeGrupo} ${grupo.cicloEscolar} ${grupo.numeroDeEvaluaciones}")
        }
        val opcionSeleccionada: Int? = ManagerInteraccion.getInt()
        if (opcionSeleccionada == null || opcionSeleccionada !in 1..listaDeGrupos.size) {
            println("Por favor selecciona una opcion valida dentro del menu")
            editaUnGrupo(onComplete)
        } else {
            val grupoSeleccionado: Grupo = listaDeGrupos.get(opcionSeleccionada.dec())
            ManagerInteraccion.cleanInput()
            println("Indique por favor el nuevo nombre del grupo o presione enter para no modificar")
            val nuevoNombreGrupo: String = ManagerInteraccion.getNextLine()
            println("Indique por favor el nuevo ciclo escolar")
            val nuevoCicloEscolar: String = ManagerInteraccion.getNextLine()
            val grupoYaExistente: Grupo? = profesor.getGrupos()
                .firstOrNull { it.nombreDeGrupo == nuevoNombreGrupo && it.cicloEscolar == nuevoCicloEscolar }
            val grupoYaExiste: Boolean = grupoYaExistente != null
            if (grupoYaExiste) {
                println("Los datos que proporcionas ya estan asignados a un grupo")
                onComplete.invoke()
            } else {
                if (nuevoNombreGrupo.isNotBlank()) grupoSeleccionado.nombreDeGrupo = nuevoNombreGrupo
                if (nuevoCicloEscolar.isNotBlank()) grupoSeleccionado.cicloEscolar = nuevoCicloEscolar
                println("Las preferencias para nombre de grupo y ciclo escolar se han actualizado con exito")
                onComplete.invoke()
            }
        }

    }

    /**
     *  Inicializa el flujo para añadir un alumno a la lista y controla los posibles errores durante el proceso.
     *
     *  @throws NotAvailableGroupsException cuando la lista de grupos es vacia.
     *
     *  @throws AlumnoPreviamenteInscritoException cuando el alumno ya se encuentra dentro de la lista de alumnos, se
     *  indica al usuario y se redirige al menu principal
     */
    fun ejecutaFlujoParaInscribirAlumno(onComplete: () -> Unit) {
        var grupoSeleccionado: Grupo? = null
        try {
            val title: String = "Por favor seleccione el grupo al que desea inscribir al alumno"
            val grupos: List<Grupo> = profesor.getAvailableGroups()
            grupoSeleccionado = obtenerGrupo(title, grupos)
            inscribeAlumno(grupoSeleccionado)
        } catch (e: NotAvailableGroupsException) {
            println("No hay grupos disponibles en los que puedas incribir alumnos")
            onComplete.invoke()
        } catch (e: AlumnoPreviamenteInscritoException) {
            val title: String = "El alumno  con numero de cuenta ${e.numeroDeCuenta}, ya se encuentra" +
                    "en la lista del grupo ${grupoSeleccionado?.nombreDeGrupo}"
            val content: String = "Intenta con otro alumno."
            ejecutaFlujoParaInscribirAlumno(onComplete)
        }
    }

    /**
     * Identifica y retorna un grupo.
     *
     * @throws NotAvailableGroupsException cuando la lista de grupos es vacia.
     */
    @Throws(NotAvailableGroupsException::class)
    fun obtenerGrupo(title: String, grupos: List<Grupo>): Grupo {
        if (grupos.isEmpty())
            throw NotAvailableGroupsException()
        println(title)
        grupos.forEachIndexed { index, grupo ->
            println("${index.inc()} ${grupo.nombreDeGrupo} ${grupo.cicloEscolar}")
        }
        var opcionSeleccionada: Int? = ManagerInteraccion.getInt()
        while (opcionSeleccionada == null || opcionSeleccionada !in 1..grupos.size) {
            println("Por favor selecciona una opcion valida dentro del menu")
            opcionSeleccionada = ManagerInteraccion.getInt()
        }
        val grupoSeleccionado: Grupo = grupos.get(opcionSeleccionada.dec())
        return grupoSeleccionado
    }

    /**
     * Toma y valida los atributos necesarios para crear un alumno.
     *
     * @throws AlumnoPreviamenteInscritoException cuando un alumno con mismos atributos ya se encuentra agregado a la
     * lista de [grupoSeleccionado]
     *
     * @throws AlumnoPreviamenteInscritoException cuando un alumno con mismos atributos ya se encuentra agregado a la
     * lista de [grupoSeleccionado]
     */
    @Throws(AlumnoPreviamenteInscritoException::class)
    fun inscribeAlumno(grupoSeleccionado: Grupo) {
        println("Indica el nombre del alumno")
        val nombreDelAlumno: String = ManagerInteraccion.getNextLine()
        println("Indica el numero de cuenta del alumno")
        var numeroDeCuenta: Int? = ManagerInteraccion.getInt()
        while (numeroDeCuenta == null) {
            println("El numero de cuenta del alumno unicamente es numerico, por favor proporciona un valor valido")
            ManagerInteraccion.cleanInput()
            numeroDeCuenta = ManagerInteraccion.getInt()
        }
        grupoSeleccionado.inscribirAlumno(nombreDelAlumno, numeroDeCuenta)
    }

    /**
     * Toma y valida los atributos necesarios para crear un alumno.
     *
     * @throws AlumnoPreviamenteInscritoException cuando un alumno con mismos atributos ya se encuentra agregado a la
     * lista de [grupoSeleccionado]
     *
     * @throws AlumnoNoEncontradoEnListaException cuando en la lista de alumnos del grupo no se identifica algun alumno
     * con el numero de cuenta proporcionado
     */
    fun ejecutaFlujoParaEliminarUnAlumno(onComplete: () -> Unit) {
        var grupoSeleccionado: Grupo? = null
        try {
            val title: String = ("Por favor seleccione el grupo en el que quiere eliminar al alumno")
            val grupos: List<Grupo> = profesor.getAvailableGroups()
            grupoSeleccionado = obtenerGrupo(title,grupos)
            eliminaAlumno(grupoSeleccionado)
        } catch (e: NotAvailableGroupsException){
            println("No hay grupos disponibles de los que puedas eliminar un alumno")
            onComplete.invoke()
        } catch (e:AlumnoNoEncontradoEnListaException) {
            println("El numero de cuenta que proporcionas no esta asociado a ningun alumno")
            onComplete.invoke()
        }

    }

    /**
     * Toma y valida el numero de cuenta del alumno que se busca eliminar de la lista.
     *
     * @throws AlumnoNoEncontradoEnListaException cuando en la lista de alumnos del grupo no se identifica algun alumno
     * con el numero de cuenta proporcionado
     */
    @Throws(AlumnoNoEncontradoEnListaException::class)
    fun eliminaAlumno(grupoSeleccionado: Grupo) {
        println("Indica el nombre del alumno")
        var numeroDeCuenta: Int? = ManagerInteraccion.getInt()
        while (numeroDeCuenta == null) {
            println("El numero de cuenta del alumno unicamente es numerico, por favor proporciona un valor valido")
            ManagerInteraccion.cleanInput()
            numeroDeCuenta = ManagerInteraccion.getInt()
        }
        grupoSeleccionado.eliminarAlumno(numeroDeCuenta)
    }




}
