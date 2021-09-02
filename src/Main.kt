package main

import main.data.Profesor
import main.ui.ControlEscolar

/**
 *  Clase encargada de la ejecucion del programa
 */
fun main(args: Array<String>) {
    val profesor: Profesor = Profesor()
    val app: ControlEscolar = ControlEscolar(profesor)
    app.execute()
}