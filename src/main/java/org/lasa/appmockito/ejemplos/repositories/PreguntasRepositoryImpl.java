package org.lasa.appmockito.ejemplos.repositories;

import org.lasa.appmockito.ejemplos.Datos;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntasRepositoryImpl implements PreguntasRepository {
    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println("PreguntasRepositoryImpl.findPreguntasPorExamenId");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Datos.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntasRepositoryImpl.guardarVarias");
    }
}
