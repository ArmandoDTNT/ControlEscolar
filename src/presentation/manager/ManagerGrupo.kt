package main.presentation.manager

import domain.entity.Grupo
import domain.exception.agrega_materia.MateriaPreviamenteAgregadaException
import domain.exception.inscribir_alumno.AlumnoPreviamenteInscritoException
import main.data.Profesor
import main.domain.exception.groups_manager.NotAvailableGroupsException
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
    fun EditaUnGrupo(onComplete: () -> Unit) {
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
            EditaUnGrupo(onComplete)
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
     *  Solicita al usuario el nombre y numero de cuenta del alumno, despliega una lista de grupos filtrada a grupos
     *  en los que aun no se da inicio el semestre, al crear la instancia Alumno se valida que no se encuentre inscrito
     *  anteriormente a esa instancia de grupo.
     *
     *  @throws AlumnoPreviamenteInscritoException cuando el alumno ya se encuentra dentro de la lista de alumnos, se
     *  indica al usuario y se redirige al menu principal
     */
    fun flujoParaInscribirAlumno(onComplete: () -> Unit) {
        var grupoSeleccionado: Grupo? = null
        try {
            grupoSeleccionado = grupoParaInscribirAlumno()
            inscribirAlumno(grupoSeleccionado)
        } catch (e: NotAvailableGroupsException) {
            println("No hay grupos disponibles en los que puedas incribir alumnos")
            onComplete.invoke()
        } catch (e: AlumnoPreviamenteInscritoException) {
            val title: String = "El alumno  con numero de cuenta ${e.numeroDeCuenta}, ya se encuentra" +
                    "en la lista del grupo ${grupoSeleccionado?.nombreDeGrupo}"
            val content: String = "Intenta con otro alumno."
            flujoParaInscribirAlumno(onComplete)
        }
    }

    @Throws(NotAvailableGroupsException::class)
    fun grupoParaInscribirAlumno(): Grupo {
        val gruposDisponibles = profesor.getAvailableGroups()
        if (gruposDisponibles.isEmpty())
            throw NotAvailableGroupsException()
        println("Por favor seleccione el grupo al que desea inscribir al alumno")
        gruposDisponibles.forEachIndexed { index, grupo ->
            println("${index.inc()} ${grupo.nombreDeGrupo} ${grupo.cicloEscolar}")
        }
        var opcionSeleccionada: Int? = ManagerInteraccion.getInt()
        while (opcionSeleccionada == null || opcionSeleccionada !in 1..gruposDisponibles.size) {
            println("Por favor selecciona una opcion valida dentro del menu")
            opcionSeleccionada = ManagerInteraccion.getInt()
        }
        val grupoSeleccionado: Grupo = gruposDisponibles.get(opcionSeleccionada.dec())
        return grupoSeleccionado
    }

    @Throws(AlumnoPreviamenteInscritoException::class)
    fun inscribirAlumno(grupoSeleccionado: Grupo) {
        println("Indica el nombre del alumno")
        val nombreDelAlumno: String = ManagerInteraccion.getNextLine()
        println("Indica el nombre del alumno")
        var numeroDeCuenta: Int? = ManagerInteraccion.getInt()
        while (numeroDeCuenta == null) {
            println("El numero de cuenta del alumno unicamente es numerico, por favor proporciona un valor valido")
            ManagerInteraccion.cleanInput()
            numeroDeCuenta = ManagerInteraccion.getInt()
        }
        grupoSeleccionado.inscribirAlumno(nombreDelAlumno, numeroDeCuenta)
    }


}
