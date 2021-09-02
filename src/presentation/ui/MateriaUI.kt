package main.presentation.ui

import domain.entity.Materia

/** */
// fun getInformation(materia: Materia): String =
//    "${materia.nombreDeMateria} - ${materia.codigoDeMateria}"

/** */
fun Materia.getInformation(): String = "$codigoDeMateria - $nombreDeMateria"
