package domain.entity

import domain.exception.manager_grupo.AlumnoPreviamenteInscritoException
import main.domain.exception.Manager_grupo.AlumnoNoEncontradoEnListaException
import main.framework.contains
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 * @param fechaDeInicio
 */
data class Grupo(
    var nombreDeGrupo: String,
    var cicloEscolar: String,
    var fechaDeInicio: Date? = null,
    var fechaDeTermino: Date? = null,
    var fechaDeEliminacion: Date? = null,
    val uuid: String = UUID.randomUUID().toString()
) {

    /* Asigna al grupo las evaluaciones que se realizaran */
    var numeroDeEvaluaciones: Int? = null

    /* Contiene las instancias de alumno correspondientes a cada grupo */
    var listaDeAlumnos: ArrayList<Alumno> = arrayListOf()

    /**
     * Crea una instancia de Alumno y valida que esta no se encuentre ya en la lista de grupo seleccionada por el
     * usuario.
     *
     *@throws AlumnoPreviamenteInscritoException cuando el alumno ya se encuentra dentro de la lista del grupo seleccionado,
     * se indica al usuario y se brinda la posibilidad de intentar con otros datos.
     */
    @Throws(AlumnoPreviamenteInscritoException::class)
    fun inscribirAlumno(nombre: String, numeroDeCuenta: Int) {
        val alumno: Alumno = Alumno(nombre, numeroDeCuenta)
        if (listaDeAlumnos.contains { it.numeroDeCuenta == numeroDeCuenta}){
            throw AlumnoPreviamenteInscritoException(numeroDeCuenta)
        }
        listaDeAlumnos.add(alumno)
    }

    /**
     * Valida si en la lista del grupo seleccionado se encuentra una instancia de Alumno y la elimina.
     *
     * @throws AlumnoNoEncontradoEnListaException
     */
    @Throws(AlumnoNoEncontradoEnListaException::class)
    fun eliminarAlumno(numeroDeCuenta: Int) {
        val alumnoAEliminar: Alumno? = listaDeAlumnos.firstOrNull { it.numeroDeCuenta == numeroDeCuenta }
        val existeAlumno: Boolean = alumnoAEliminar != null
        if (existeAlumno) {
            listaDeAlumnos.remove(alumnoAEliminar)
        } else {
         throw AlumnoNoEncontradoEnListaException()
        }
    }

    /**
     * Modifica la cantidad de evaluaciones del grupo recibiendo como parametro [cantidadDeEvaluaciones]
     */
    fun asignaCantidadEvaluaciones(cantidadDeEvaluaciones: Int) {
        numeroDeEvaluaciones = cantidadDeEvaluaciones
    }

    /**
     * Valida si el curso se ha iniciado.
     */
    fun haIniciado(): Boolean = fechaDeInicio != null

    /**
     * Modifica el atributo fecha de inicio
     */
    fun iniciaCurso(){
        fechaDeInicio = Date()
    }

    /**
     * Modifica el atributo fecha de inicio
     */
    fun finalizaCurso(){
        fechaDeTermino = Date()
    }
}