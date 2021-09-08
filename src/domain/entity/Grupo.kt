package domain.entity

import domain.exception.inscribir_alumno.AlumnoPreviamenteInscritoException
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
    val numeroDeEvaluaciones: Int = 0

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
        // TODO: Lanzar excepcion si el alumno ya existe
        // throw AlumnoPreviamenteInscritoException(nombre, numeroDeCuenta)
        listaDeAlumnos.add(alumno)
    }

    /**
     * Valida se en la lista del grupo seleccionado se encuentra una instancia de Alumno y la elimina.
     *
     *@throws Exception cuando el alumno ya se encuentra dentro de la lista del grupo seleccionado,
     * se indica al usuario y se brinda la posibilidad de intentar con otros datos.
     */
    @Throws(AlumnoPreviamenteInscritoException::class)
    fun eliminarAlumno(numeroDeCuenta: Int) {
        var borrar: Alumno? = null
        for (alumno in listaDeAlumnos) {
            if (numeroDeCuenta == alumno.numeroDeCuenta) {
                borrar = alumno
                listaDeAlumnos.remove(borrar)
                return
            }
        }
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