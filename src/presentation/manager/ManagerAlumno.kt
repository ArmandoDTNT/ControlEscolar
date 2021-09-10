package main.presentation.manager

import domain.entity.Alumno
import domain.entity.Grupo
import main.data.Profesor
import main.domain.exception.Manager_grupo.AlumnoNoEncontradoEnListaException
import main.domain.exception.Manager_grupo.NotAvailableGroupsException

class ManagerAlumno(
    private val profesor: Profesor
) : GrupoIdentificable, AlumnoIdentificable {

    /**
     * Identifica y retorna un grupo.
     *
     * @throws NotAvailableGroupsException cuando la lista de grupos es vacia.
     * @throws AlumnoNoEncontradoEnListaException cuando la lista de grupos es vacia.
     */
    fun iniciaFlujoParaAsignarEvaluaciones(onFinish: () -> Unit) {
        val gruposDisponibles: List<Grupo> = profesor.getGrupos()
            .filter { it.fechaDeEliminacion == null }
            .filter { it.fechaDeTermino == null }
            .filter { it.numeroDeEvaluaciones != null }
            .filter { it.listaDeAlumnos.isNotEmpty() }
        val title: String = "Aun no cuentas con grupos a los que puedas asignar calificaciones"
        try {
            val grupo: Grupo = obtenerGrupo(title, gruposDisponibles)
            val title2: String = "Indica el numero de cuenta del alumno"
            val alumno: Alumno = obtenerAlumno(title2, grupo)
            asingnarUnaEvaluacion(grupo, alumno)
        } catch (e: NotAvailableGroupsException) {
            println("No hay grupos en los que puedas asignar calificaciones a sus alumnos")
            onFinish.invoke()
        } catch (e: AlumnoNoEncontradoEnListaException) {
            println("El grupo no contiene al alumno con el numero de cuenta que proporcionaste")
            onFinish.invoke()
        }
    }

    /**
     *
     */
    private fun asingnarUnaEvaluacion(grupo: Grupo, alumno: Alumno) {
        println("Las evaluaciones asignadas al alumno hasta el momento son:")
        alumno.arregloDeCalificaciones.forEachIndexed { index, evaluacion ->
            if (evaluacion == -1.0){
                println("${index.inc()} ~")
            } else {
                println("${index.inc()} $evaluacion")
            }

        }
        println("Selecciona la posicion de la evaluacion que quieres asignar o modificar")
        var opcionSeleccionada: Int? = ManagerInteraccion.getInt()
        while (opcionSeleccionada == null || opcionSeleccionada !in 1..grupo.numeroDeEvaluaciones!!) {
            println("La opcion que seleccionas no se encuentra dentro del menu, por favor selecciona una opcion valida")
            opcionSeleccionada = ManagerInteraccion.getInt()
        }
        println("Proporciona la evaluacion que quieres asignar redondeado a piso a un decimal")
        var evaluacionAsignada = ManagerInteraccion.getDouble()
        while (evaluacionAsignada == null) {
            println("La evaluacion que quieres asignar no corresponde con la opcion valida, ingresa un parametro valido")
            evaluacionAsignada = ManagerInteraccion.getDouble()
        }
        val indiceDeEvaluacion = opcionSeleccionada.dec()
        alumno.asignarCalificacion(indiceDeEvaluacion, evaluacionAsignada)
    }


}