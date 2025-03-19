package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.evidenlibrary.backend.apirest.model.dao.GeneroDao;
import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@ExtendWith(MockitoExtension.class)
class GeneroServiceTest {

    @Mock
    private GeneroDao generoDao;
    
    @Mock
    private LibroDao libroDao;
    
    @InjectMocks
    private GeneroServiceImpl generoService;
    
    private Genero genero1;
    private Genero genero2;
    private List<Genero> listaGeneros;
    private Libro libro1;
    private Libro libro2;
    
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        // Configurar datos de prueba
        genero1 = new Genero();
        genero1.setId(1L);
        genero1.setNombre("Ciencia Ficción");
        genero1.setDescripcion("Narrativa basada en elementos científicos ficticios o futuristas");
        
        genero2 = new Genero();
        genero2.setId(2L);
        genero2.setNombre("Fantasía");
        genero2.setDescripcion("Narrativa con elementos sobrenaturales o mágicos");
        
        listaGeneros = new ArrayList<>();
        listaGeneros.add(genero1);
        listaGeneros.add(genero2);
        
        // Crear libros
        libro1 = new Libro();
        libro1.setTitulo("Dune");
        libro1.setPrecio(29.99);
        libro1.setStock(10);
        
        libro2 = new Libro();
        libro2.setTitulo("El Señor de los Anillos");
        libro2.setPrecio(39.99);
        libro2.setStock(5);
        
        // Establecer relaciones
        libro1.getGeneros().add(genero1);
        genero1.getLibros().add(libro1);
        
        libro2.getGeneros().add(genero2);
        genero2.getLibros().add(libro2);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(generoDao.findAll()).thenReturn(listaGeneros);
        
        // Ejecutar método a probar
        List<Genero> resultado = generoService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals("Ciencia Ficción", resultado.get(0).getNombre());
        assertEquals("Fantasía", resultado.get(1).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(generoDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(generoDao.findById(1L)).thenReturn(Optional.of(genero1));
        
        // Ejecutar método a probar
        Genero resultado = generoService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Ciencia Ficción", resultado.getNombre());
        
        // Verificar que se llamó al método correcto
        verify(generoDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(generoDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        Genero resultado = generoService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(generoDao).findById(99L);
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(generoDao.save(genero1)).thenReturn(genero1);
        
        // Ejecutar método a probar
        Genero resultado = generoService.save(genero1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Ciencia Ficción", resultado.getNombre());
        
        // Verificar que se llamó al método correcto
        verify(generoDao).save(genero1);
    }
    
    @Test
    void testDelete() {
        // Ejecutar método a probar
        generoService.delete(genero1);
        
        // Verificar que se llamaron a los métodos correctos
        verify(libroDao).save(libro1);
        verify(generoDao).delete(genero1);
        
        // Verificar que se eliminó la relación
        assertFalse(libro1.getGeneros().contains(genero1));
    }
    
    @Test
    void testDeleteAll() {
        // Configurar mock
        when(generoDao.findAll()).thenReturn(listaGeneros);
        
        // Ejecutar método a probar
        generoService.deleteAll();
        
        // Verificar que se llamaron a los métodos correctos
        verify(generoDao).findAll();
        verify(libroDao).save(libro1);
        verify(libroDao).save(libro2);
        verify(generoDao).deleteAll();
        
        // Verificar que se eliminaron las relaciones
        assertFalse(libro1.getGeneros().contains(genero1));
        assertFalse(libro2.getGeneros().contains(genero2));
    }
} 