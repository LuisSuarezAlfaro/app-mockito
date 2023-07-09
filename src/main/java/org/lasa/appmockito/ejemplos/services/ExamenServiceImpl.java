package org.lasa.appmockito.ejemplos.services;

import org.lasa.appmockito.ejemplos.models.Examen;
import org.lasa.appmockito.ejemplos.repositories.ExamenRepository;
import org.lasa.appmockito.ejemplos.repositories.PreguntasRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{

    private ExamenRepository examenRepository;

    private PreguntasRepository preguntasRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntasRepository preguntasRepository){
        this.examenRepository = examenRepository;
        this.preguntasRepository = preguntasRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return examenRepository.findAll()
                .stream()
                .filter(e -> e.getNombre().contains(nombre))
                .findFirst();
    }

    @Override
    public Examen findExamenConNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.orElseThrow();
            List<String> preguntas = preguntasRepository.findPreguntasPorExamenId(examen.getId());
            preguntasRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            preguntasRepository.guardarVarias(examen.getPreguntas());
        }
        return examenRepository.guardar(examen);
    }


}
