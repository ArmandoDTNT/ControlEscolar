package main.presentation.manager

import main.data.Profesor

class ManagerAlumno(
    private val profesor: Profesor
) {

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

}