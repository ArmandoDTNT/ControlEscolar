package domain.exception.manager_grupo

/**
 * Se dispara en caso de que al validar la instancia Alumno creada con los datos proporcionados por el usuario
 * coincida con alguna otra de la lista de grupo en la que se va a guardar.
 */
class AlumnoPreviamenteInscritoException(
    val numeroDeCuenta: Int
) : Exception()