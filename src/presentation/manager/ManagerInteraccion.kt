package main.presentation.manager

import java.util.*

/**
 *
 */
object ManagerInteraccion {

    /* Clase encargada de manejar la comunicacion via terminal con el usuario */
    private val scanner: Scanner = Scanner(System.`in`)

    /**
     *
     */
    fun cleanInput() {
        scanner.nextLine()
    }

    /**
     *
     */
    fun awaitForEnterKeyInteraction() {
        scanner.nextLine()
    }

    /**
     * Proporciona al usuario la opcion de interactuar con el sistema por medio de un String.
     */
    fun getNextLine(): String {
        return scanner.nextLine()
    }

    /**
     * Proporciona al usuario la opcion de interactuar con el sistema por medio de un Integrer.
     *
     * @return [Int] que representa la opcion seleccionada por el usuario. Null en caso de que la opcion seleccionada
     * por el usuario no sea un [Int]
     */
    fun getOption(): Int? = try {
        scanner.nextInt()
    } catch (e: Exception) {
        null
    }

}