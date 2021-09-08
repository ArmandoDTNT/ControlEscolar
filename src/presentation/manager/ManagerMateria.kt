package main.presentation.manager

import domain.entity.Materia
import domain.exception.agrega_materia.MateriaPreviamenteAgregadaException
import main.data.Profesor

class ManagerMateria(
    private val profesor: Profesor,
) {

    /**
     *  Despliega al usuario la lista de materias agregadas.
     */
    fun consultaListaDeMaterias() {
        val listaDeMaterias = profesor.getMaterias().filter { it.fechaDeEliminacion == null }
        if (listaDeMaterias.isEmpty()) {
            println("Aun no hay materias inscritas")
        } else {
            println("Las materias inscritas hasta el momento son:")
            listaDeMaterias.forEachIndexed { index, materia ->
                println("${index.inc()} ${materia.nombreDeMateria} ${materia.codigoDeMateria}")
            }
        }
    }

    /**
     *  Solicita al usuario los datos nombre de la materia y codigo de materia, al crear la instancia materia se valida
     *  que que no se cuente con alguna agregada anteriormente.
     *
     *  @throws MateriaPreviamenteAgregadaException cuando la materia ya se encuentra dentro de la lista, se indica
     *  al usuario y se brinda la posibilidad de intentar con otra materia.
     */
    @Throws(MateriaPreviamenteAgregadaException::class)
    fun agregaUnaMateria(
        onComplete: (title: String, content: String, action: () -> Unit) -> Unit
    ) {
        ManagerInteraccion.cleanInput()
        println("Por favor indique el nombre de la materia")
        var nombreDeLaMateria: String = ManagerInteraccion.getNextLine()
        println("Por favor indique el codigo de la materia")
        var codigoDeLaMateria: String = ManagerInteraccion.getNextLine()
        try {
            profesor.agregarMateria(nombreDeLaMateria, codigoDeLaMateria)
        } catch (e: MateriaPreviamenteAgregadaException) {
            val title: String =
                "La materia $nombreDeLaMateria con codigo $codigoDeLaMateria ya se encuentra en la lista"
            val content: String = "¿Desea intentar con otra materia?"
            onComplete.invoke(title, content) { agregaUnaMateria(onComplete) }
            return
        }
        val title: String = "La materia $nombreDeLaMateria con codigo $codigoDeLaMateria se ha agregado con exito"
        val content: String = "¿Desea agregar otra materia?"
        onComplete.invoke(title, content) { agregaUnaMateria(onComplete) }
    }

    /**
     * Despliega un menu de materias que aun no han sido eliminadas y brinda la posibilidad de comenzar el flujo para
     * eliminar alguna de las mostradas.
     */
    fun ejecutaFlujoParaEliminarUnaMateria(onComplete: () -> Unit) {
        val listaDeMaterias = profesor.getMaterias().filter { it.fechaDeEliminacion == null }
        if (listaDeMaterias.isEmpty()) {
            println("Aun no hay materias que puedas eliminar")
            // Redirecciona menu
            onComplete.invoke()
        } else {
            println("Selecciona la materia que deseas eliminar")
            listaDeMaterias.forEachIndexed { index, materia ->
                println("${index.inc()} $materia")
            }
            val opcionSeleccionada = ManagerInteraccion.getInt() ?: -1
            if (opcionSeleccionada !in 1..listaDeMaterias.size) {
                println("Por favor selecciona una opcion dentro del menu")
                ManagerInteraccion.cleanInput()
                ejecutaFlujoParaEliminarUnaMateria(onComplete)
            } else {
                val materiaSeleccionada = listaDeMaterias.get(opcionSeleccionada.dec())
                materiaSeleccionada.eliminaMateria()
                println("La materia $materiaSeleccionada se ha eliminado con exito")
                // Redirecciona menu
                onComplete.invoke()
            }
        }
    }

    /**
     * Inicia el flujo para editar una materia brindando la posibilidad de editarla por dos flujos distintos
     * sean estos codigo de materia o seleccionandola de una lista
     */
    fun editaUnaMateria(onComplete: () -> Unit) {
        val menuEditaMateria: String = """
            Selecciona una opcion para identificar la materia
            1) Con codigo de materia
            2) Identificar la materia en lista de materias
        """.trimIndent()
        println(menuEditaMateria)
        val opcionSeleccionada: Int? = ManagerInteraccion.getInt()
        when (opcionSeleccionada) {
            1 -> editaMateriaConCodigo(onComplete)
            2 -> editaMateriaDeLista(onComplete)
            else -> {
                println("La opcion seleccionada no es valida, intenta nuevamente")
                editaUnaMateria(onComplete)
            }
        }
    }

    /**
     * Inicia el flujo para la edicion de una materia identificada por medio de su [codigoDeMateria]
     */
    fun editaMateriaConCodigo(onComplete: () -> Unit) {
        val listaDeMaterias = profesor.getMaterias().filter { it.fechaDeEliminacion == null }
        if (listaDeMaterias.isEmpty()){
            println("Aun no cuentas con materias que puedas editar")
            ManagerInteraccion.cleanInput()
            onComplete.invoke()
            return
        }
        ManagerInteraccion.cleanInput()
        println("Proporciona el codigo de la materia que deseas editar")
        val codigo: String = ManagerInteraccion.getNextLine()
        val materiaAEditar = listaDeMaterias.firstOrNull { it.codigoDeMateria == codigo }
        if (materiaAEditar == null) {
            println("El codigo de materia que proporciona no coincide con alguna materia en la lista")
            onComplete.invoke()
            return
        }
        editaDatosDeMateria(materiaAEditar)
        onComplete.invoke()
    }

    /**
     * Inicia el flujo para la edicion de una materia identificada por medio de una lista de materias
     */
    fun editaMateriaDeLista(onComplete: () -> Unit) {
        val listaDeMaterias = profesor.getMaterias().filter { it.fechaDeEliminacion == null }
        if (listaDeMaterias.isEmpty()) {
            println("Aun no cuentas con materias que puedas editar")
            ManagerInteraccion.cleanInput()
            onComplete.invoke()
            return
        }
        println("Selecciona de la lista la materia que quieres editar")
        listaDeMaterias.forEachIndexed { index, materia ->
            println("${index.inc()}, $materia")
        }
        val opcionSeleccionada = ManagerInteraccion.getInt()
        while (opcionSeleccionada == null || opcionSeleccionada !in 1..listaDeMaterias.size) {
            println("La opcion seleccionada no esta en el menu, por favor intenta de nuevo con una opcion valida")
            editaMateriaDeLista(onComplete)
            return
        }
        val materiaAEditar = listaDeMaterias.get(opcionSeleccionada.dec())
        editaDatosDeMateria(materiaAEditar)
        onComplete.invoke()
    }

    /**
     * Identificada la materia a editar se realiza la toma de datos y se actualizan los atributos.
     */
    fun editaDatosDeMateria(materiaAEditar: Materia) {
        ManagerInteraccion.cleanInput()
        println("Proporcione el nuevo nombre de la materia o presione enter para no modificar")
        val nuevoNombre: String = ManagerInteraccion.getNextLine()
        println("Proporcione el nuevo codigo de la materia o presione enter para no modificar")
        val nuevoCodigo: String = ManagerInteraccion.getNextLine()
        // Validamos si el nuevo codigo ya existe
        val nuevoCodigoYaExiste: Boolean = profesor.getMaterias()
            .firstOrNull { it.codigoDeMateria == nuevoCodigo } != null
        // TODO: Validacion codigo ya existente
        if (nuevoCodigoYaExiste) {
            println("El codigo que proporcionas ya esta asociado a una materia en la lista")
            return
        }
        // Actualizamos el valor de la materia
        if (nuevoNombre.isNotBlank()){
            materiaAEditar.nombreDeMateria = nuevoNombre
        }
        if (nuevoCodigo.isNotBlank()){
            materiaAEditar.codigoDeMateria = nuevoCodigo
        }
        println("Sus preferencias para Nombre y Codigo de materia se han realizado con exito")
    }
}