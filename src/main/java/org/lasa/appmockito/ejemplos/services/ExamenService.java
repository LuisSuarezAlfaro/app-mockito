package org.lasa.appmockito.ejemplos.services;

import org.lasa.appmockito.ejemplos.models.Examen;

import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findExamenPorNombre(String nombre);

    Examen findExamenConNombreConPreguntas(String nombre);

    Examen guardar(Examen examen);
}
