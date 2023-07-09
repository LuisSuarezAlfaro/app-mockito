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

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    @Mock
    private ExamenRepositoryImpl examenRepository;
    @Mock
    private PreguntasRepositoryImpl preguntasRepository;
    @InjectMocks
    private ExamenServiceImpl service;
    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
        /**
         * Primera opcion de instacion repository y services
         */
        //MockitoAnnotations.openMocks(this);

    }

    @Test
    void findExamenPorNombre() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matemáticas", examen.orElseThrow().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();
        when(examenRepository.findAll()).thenReturn(datos);
        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");

        assertFalse(examen.isPresent());
    }

    @Test
    void testPreguntasExamen() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenConNombreConPreguntas("Historia");

        assertNotNull(examen);
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));

    }

    @Test
    void testPreguntasExamenVerify() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenConNombreConPreguntas("Historia");

        assertNotNull(examen);
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));

        verify(examenRepository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(anyLong());

    }

    @Test
    void testNoExisteExamenVerify() {

        // Given
        when(examenRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        Examen examen = service.findExamenConNombreConPreguntas("Historia2");

        // Then
        assertNull(examen);
        verify(examenRepository).findAll();

    }

    @Test
    void testGuardarExamen() {

        // Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        when(examenRepository.guardar(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        // When
        Examen examen = service.guardar(newExamen);

        // Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());

        verify(examenRepository).guardar(any(Examen.class));
        verify(preguntasRepository).guardarVarias(anyList());
    }

    @Test
    void testManejoException() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntasRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenConNombreConPreguntas("Matemáticas");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(examenRepository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenConNombreConPreguntas("Matemáticas");

        verify(examenRepository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(argThat(arg -> arg.equals(5L)));
        verify(preguntasRepository).findPreguntasPorExamenId(eq(5L));
    }

    @Test
    void testArgumentMatchers2() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenConNombreConPreguntas("Matemáticas");

        verify(examenRepository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));
    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long>{

        private Long argument;

        public boolean matches(Long argument){
            this.argument = argument;
            return argument != null && argument > 0;
        }

        public String toString(){
            return "Es para un mensaje personalizado de error que imprime " +
                    "mockito en caso que falle el test " + argument +
                    " debe ser un numero entero positivo";
        }
    }

    @Test
    void testArgumentCaptor() {

        // given
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        // when
        service.findExamenConNombreConPreguntas("Matemáticas");

        // then
        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(preguntasRepository).findPreguntasPorExamenId(captor.capture());

        assertEquals(5L, captor.getValue());

    }

    @Test
    void testDoThrow() {

        // given
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);

        //Cuando el metodo no retorna nada
        doThrow(IllegalArgumentException.class).when(preguntasRepository).guardarVarias(anyList());

        // when and then
        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L? Datos.PREGUNTAS: Collections.emptyList();
        }).when(preguntasRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenConNombreConPreguntas("Matemáticas");

        assertEquals(5L, examen.getId());
        assertTrue(examen.getPreguntas().contains("geometría"));
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());

    }

    @Test
    void testDoAnswerGuardarExamen() {

        // Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        doAnswer(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }

        }).when(examenRepository).guardar(any(Examen.class));

        // When
        Examen examen = service.guardar(newExamen);

        // Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());

        verify(examenRepository).guardar(any(Examen.class));
        verify(preguntasRepository).guardarVarias(anyList());
    }

    @Test
    void testDoCallRealMethod() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        doCallRealMethod().when(preguntasRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenConNombreConPreguntas("Matemáticas");

        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
    }

    @Test
    void testSpy() {
        // Given
        //ExamenRepository examenRepository = mock(ExamenRepository.class);
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntasRepository preguntasRepository = spy(PreguntasRepositoryImpl.class);

        List<String> preguntas = Arrays.asList("aritmética");
        //when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntasRepository).findPreguntasPorExamenId(anyLong());

        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntasRepository);

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

    @Test
    void testOrdenDeInvocaciones() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenConNombreConPreguntas("Matemáticas");
        service.findExamenConNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(preguntasRepository);
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(6L);

    }

    @Test
    void testOrdenDeInvocaciones2() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenConNombreConPreguntas("Matemáticas");
        service.findExamenConNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(examenRepository, preguntasRepository);

        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(5L);

        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(6L);

    }

    @Test
    void testNumeroInvocaciones() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenConNombreConPreguntas("Matemáticas");

        verify(preguntasRepository).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, times(1)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atMost(1)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumeroInvocaciones2() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenConNombreConPreguntas("Matemáticas");

        //verify(preguntasRepository).findPreguntasPorExamenId(5L); Falla
        verify(preguntasRepository, times(2)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atLeast(2)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atMost(20)).findPreguntasPorExamenId(5L);
        //verify(preguntasRepository, atMostOnce()).findPreguntasPorExamenId(5L); Falla
    }

    @Test
    void testNumeroInvocaciones3() {
        when(examenRepository.findAll()).thenReturn(Collections.emptyList());

        service.findExamenConNombreConPreguntas("Matamáticas");

        verify(preguntasRepository, never()).findPreguntasPorExamenId(5L);
        verifyNoInteractions(preguntasRepository);
        verify(examenRepository, times(1)).findAll();
        verify(examenRepository, atLeast(1)).findAll();
        verify(examenRepository, atLeastOnce()).findAll();
        verify(examenRepository, atMost(10)).findAll();
        verify(examenRepository, atMostOnce()).findAll();


    }
}