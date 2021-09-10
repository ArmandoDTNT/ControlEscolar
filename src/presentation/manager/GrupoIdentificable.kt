package main.presentation.manager

import domain.entity.Grupo
import main.domain.exception.Manager_grupo.NotAvailableGroupsException
import kotlin.jvm.Throws

/**
 *
 */
interface GrupoIdentificable {

    /**
     * Identifica y retorna un grupo.
     *
     * @throws NotAvailableGroupsException cuando la lista de grupos es vacia.
     */
    @Throws(NotAvailableGroupsException::class)
    fun obtenerGrupo(title: String, grupos: List<Grupo>): Grupo {
        if (grupos.isEmpty()) throw NotAvailableGroupsException()
        println(title)
        grupos.forEachIndexed { index, grupo ->
            println("${index.inc()} ${grupo.nombreDeGrupo} ${grupo.cicloEscolar}")
        }
        var opcionSeleccionada: Int? = ManagerInteraccion.getInt()
        while (opcionSeleccionada == null || opcionSeleccionada !in 1..grupos.size) {
            println("Por favor selecciona una opcion valida dentro del menu")
            opcionSeleccionada = ManagerInteraccion.getInt()
        }
        val grupoSeleccionado: Grupo = grupos.get(opcionSeleccionada.dec())
        return grupoSeleccionado
    }

}