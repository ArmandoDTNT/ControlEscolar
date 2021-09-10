package main.presentation.manager

import domain.entity.Alumno
import domain.entity.Grupo
import main.domain.exception.Manager_grupo.AlumnoNoEncontradoEnListaException
import main.domain.exception.Manager_grupo.NotAvailableGroupsException
import main.domain.exception.manager_alumno.NotAvailableEstudentsException

/**
 *
 */
interface AlumnoIdentificable {

    /**
     * Tomando como parametro un grupo localiza y retorna un alumno en su lista.
     *
     * @throws AlumnoNoEncontradoEnListaException cuando la lista de grupos es vacia.
     */
    @Throws(AlumnoNoEncontradoEnListaException::class)
    fun obtenerAlumno(title: String, grupo: Grupo): Alumno {
        println(title)
        var identificador: Int? = ManagerInteraccion.getInt()
        while (identificador == null) {
            println("El identificador es unicamente numerico, por favor proporciona un identificador valido")
            identificador = ManagerInteraccion.getInt()
        }
        val alumno: Alumno? = grupo.listaDeAlumnos.firstOrNull { it.numeroDeCuenta == identificador }
        if (alumno == null)
            throw AlumnoNoEncontradoEnListaException()
        return alumno
    }

}