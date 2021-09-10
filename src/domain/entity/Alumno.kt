package domain.entity

data class Alumno(
    val nombre: String,
    val numeroDeCuenta: Int
) {
    /* Valor asignado al grupo que consta del numero de calificaciones que tendran los alumnos*/
    var numeroDeCalificaciones: Int = 0
    /* Lista en la que se guardan las evaluaciones asignadas por el profesor que va del 0 al 10 o NP=0 que depende
    del grupo en el que se encuentre el alumno */
    var arregloDeCalificaciones: Array<Double> = Array<Double>(size = numeroDeCalificaciones){-1.0}


    /**
     * Asigna al alumno una calificacion en posicion de la lista seleccionada.
     */
    fun asignarCalificacion(indice: Int, evaluacionAsignada: Double) {
        arregloDeCalificaciones.set(indice,evaluacionAsignada)
    }

    /**
     * Tomando las calificaciones guardadas en la lista de calificaciones del alumno obtiene el promedio de las
     * evaluaciones asignadas al momento.
     */
    fun obtenerPromedio(calificiones: Array<Double>): Double {
        var i: Int = 0
        var sumaPromedio: Double = 0.0
        var promedio: Double = 0.0
        while (i < calificiones.size) {
            sumaPromedio = sumaPromedio + calificiones[i]
            i++
        }
        promedio = sumaPromedio / calificiones.size
        return promedio
    }
}
