package domain.exception.agrega_materia

/**
 * Se dispara en caso de que al validar la instancia Materia creada con los datos proporcionados por el usuario
 * coincida con alguna otra de la lista de Materias en la que se va a guardar.
 */
class MateriaPreviamenteAgregadaException : Exception()