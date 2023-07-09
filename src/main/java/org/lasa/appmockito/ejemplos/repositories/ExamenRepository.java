package org.lasa.appmockito.ejemplos.repositories;

import org.lasa.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {

    Examen guardar(Examen examen);
    List<Examen> findAll();
}
