package main.data

import domain.entity.Grupo
import domain.entity.Materia
import domain.exception.manager_grupo.GrupoPreviamenteAgregadoException
import domain.exception.Manager_materia.MateriaPreviamenteAgregadaException
import kotlin.jvm.Throws

/**
 *
 */
class Profesor(
    private val listaDeMaterias: ArrayList<Materia> = arrayListOf(),
    private val listaDeGrupos: ArrayList<Grupo> = arrayListOf(),
) {

    /**
     * Agrega una materia a [listaDeMaterias].
     *
     * @param nombreMateria que representa el nombre de la [Materia] a agregar.
     * @param codigoMateria que representa el codigo de la [Materia] a agregar.
     *
     * @throws MateriaPreviamenteAgregadaException cuando la materia ya fue agregada previamente. Es decir, tanto
     * [nombreMateria] y [codigoMateria] ya estan asociados ambos a una materia dentro de [listaDeMaterias].
     */
    @Throws(MateriaPreviamenteAgregadaException::class)
    fun agregarMateria(nombreMateria: String, codigoMateria: String) {
        val materia = Materia(nombreMateria, codigoMateria)
        val existeMateria: Boolean = listaDeMaterias.contains(materia)
        if (existeMateria)
            throw MateriaPreviamenteAgregadaException()
        listaDeMaterias.add(materia)
    }

    /**
     * Agrega un Grupo a [listaDeGrupos].
     *
     * @param nombreGrupo que representa el nombre de la [Grupo] a agregar.
     * @param cicloEscolar que representa el codigo de la [Grupo] a agregar.
     *
     * @throws GrupoPreviamenteAgregadoException cuando el grupo ya fue agregada previamente. Es decir, tanto
     * [nombreGrupo] y [cicloEscolar] ya estan asociados ambos a una materia dentro de [listaDeGrupos].
     */
    fun agregarGrupo(nombreGrupo: String, cicloEscolar: String) {
        val grupo = Grupo(nombreGrupo, cicloEscolar)
        val existeGrupo: Boolean = listaDeGrupos.contains(grupo)
        if (existeGrupo)
            throw GrupoPreviamenteAgregadoException()
        listaDeGrupos.add(grupo)
    }

    /**
     *  Obtiene lista de Materias
     *
     *  @see Materia
     */
    fun getMaterias(): List<Materia> = listaDeMaterias

    /**
     *  Obtiene la lista de Grupos.
     *
     *  @see Grupo
     */
    fun getGrupos(): List<Grupo> = listaDeGrupos

    /**
     *
     */
    fun getAvailableGroups(): List<Grupo> = listaDeGrupos
        .filter { it.fechaDeEliminacion == null }
        .filter { it.fechaDeInicio == null }


}
