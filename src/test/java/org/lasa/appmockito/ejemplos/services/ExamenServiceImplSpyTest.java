package org.lasa.appmockito.ejemplos.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lasa.appmockito.ejemplos.Datos;
import org.lasa.appmockito.ejemplos.models.Examen;
import org.lasa.appmockito.ejemplos.repositories.ExamenRepository;
import org.lasa.appmockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.lasa.appmockito.ejemplos.repositories.PreguntasRepository;
import org.lasa.appmockito.ejemplos.repositories.PreguntasRepositoryImpl;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {
    @Spy
    private ExamenRepositoryImpl examenRepository;
    @Spy
    private PreguntasRepositoryImpl preguntasRepository;
    @InjectMocks
    private ExamenServiceImpl examenService;

    @Test
    void testSpy() {

        // Given
        List<String> preguntas = Arrays.asList("aritmética");
        doReturn(preguntas).when(preguntasRepository).findPreguntasPorExamenId(anyLong());

        // when
        Examen examen = examenService.findExamenConNombreConPreguntas("Matemáticas");

        // then
        assertNotNull(examen);
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));

        verify(examenRepository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(anyLong());
    }
}