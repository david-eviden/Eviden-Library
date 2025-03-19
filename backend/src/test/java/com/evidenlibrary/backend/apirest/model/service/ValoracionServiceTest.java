package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evidenlibrary.backend.apirest.model.dao.ValoracionDao;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.entity.Valoracion;

@ExtendWith(MockitoExtension.class)
class ValoracionServiceTest {

    @Mock
    private ValoracionDao valoracionDao;
    
    @InjectMocks
    private ValoracionServiceImpl valoracionService;
    
    private Valoracion valoracion1;
    private Valoracion valoracion2;
    private List<Valoracion> listaValoraciones;
    private List<Valoracion> valoracionesLibro1;
    private List<Valoracion> valoracionesUsuario1;
    private Usuario usuario1;
    private Usuario usuario2;
    private Libro libro1;
    private Libro libro2;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombre("Usuario1");
        usuario1.setEmail("usuario1@example.com");
        
        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("Usuario2");
        usuario2.setEmail("usuario2@example.com");
        
        libro1 = new Libro();
        libro1.setTitulo("Libro 1");
        
        libro2 = new Libro();
        libro2.setTitulo("Libro 2");
        
        // Crear valoraciones
        valoracion1 = new Valoracion();
        valoracion1.setUsuario(usuario1);
        valoracion1.setLibro(libro1);
        valoracion1.setPuntuacion(5);
        valoracion1.setComentario("Excelente libro");
        valoracion1.setFecha(new Date());
        
        valoracion2 = new Valoracion();
        valoracion2.setUsuario(usuario2);
        valoracion2.setLibro(libro2);
        valoracion2.setPuntuacion(4);
        valoracion2.setComentario("Muy buen libro");
        valoracion2.setFecha(new Date());
        
        listaValoraciones = new ArrayList<>();
        listaValoraciones.add(valoracion1);
        listaValoraciones.add(valoracion2);
        
        valoracionesLibro1 = new ArrayList<>();
        valoracionesLibro1.add(valoracion1);
        
        valoracionesUsuario1 = new ArrayList<>();
        valoracionesUsuario1.add(valoracion1);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(valoracionDao.findAll()).thenReturn(listaValoraciones);
        
        // Ejecutar método a probar
        List<Valoracion> resultado = valoracionService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verificar que se llamó al método correcto
        verify(valoracionDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(valoracionDao.findById(1L)).thenReturn(Optional.of(valoracion1));
        
        // Ejecutar método a probar
        Valoracion resultado = valoracionService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(5, resultado.getPuntuacion());
        assertEquals("Excelente libro", resultado.getComentario());
        assertEquals(usuario1, resultado.getUsuario());
        assertEquals(libro1, resultado.getLibro());
        
        // Verificar que se llamó al método correcto
        verify(valoracionDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(valoracionDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        Valoracion resultado = valoracionService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(valoracionDao).findById(99L);
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(valoracionDao.save(valoracion1)).thenReturn(valoracion1);
        
        // Ejecutar método a probar
        Valoracion resultado = valoracionService.save(valoracion1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(5, resultado.getPuntuacion());
        
        // Verificar que se llamó al método correcto
        verify(valoracionDao).save(valoracion1);
    }
    
    @Test
    void testDelete() {
        // Ejecutar método a probar
        valoracionService.delete(valoracion1);
        
        // Verificar que se llamó al método correcto
        verify(valoracionDao).delete(valoracion1);
    }
    
    @Test
    void testFindByLibroId() {
        // Configurar mock
        when(valoracionDao.findByLibroId(1L)).thenReturn(valoracionesLibro1);
        
        // Ejecutar método a probar
        List<Valoracion> resultado = valoracionService.findByLibroId(1L);
        
        // Verificar resultado
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(libro1, resultado.get(0).getLibro());
        
        // Verificar que se llamó al método correcto
        verify(valoracionDao).findByLibroId(1L);
    }
    
    @Test
    void testFindByUsuarioId() {
        // Configurar mock
        when(valoracionDao.findByUsuarioId(1L)).thenReturn(valoracionesUsuario1);
        
        // Ejecutar método a probar
        List<Valoracion> resultado = valoracionService.findByUsuarioId(1L);
        
        // Verificar resultado
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(usuario1, resultado.get(0).getUsuario());
        
        // Verificar que se llamó al método correcto
        verify(valoracionDao).findByUsuarioId(1L);
    }
} 