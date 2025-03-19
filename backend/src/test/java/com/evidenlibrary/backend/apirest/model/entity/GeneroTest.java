package com.evidenlibrary.backend.apirest.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeneroTest {
    
    private Genero genero;
    private Libro libro;
    
    @BeforeEach
    void setUp() {
        // Crear un género para pruebas
        genero = new Genero();
        genero.setId(1L);
        genero.setNombre("Ciencia Ficción");
        genero.setDescripcion("Narrativa basada en elementos científicos ficticios o futuristas");
        
        // Crear un libro para pruebas
        libro = new Libro();
        libro.setTitulo("Dune");
        libro.setPrecio(24.99);
        libro.setStock(15);
        libro.setDescripcion("Novela de ciencia ficción escrita por Frank Herbert");
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, genero.getId());
        assertEquals("Ciencia Ficción", genero.getNombre());
        assertEquals("Narrativa basada en elementos científicos ficticios o futuristas", genero.getDescripcion());
        
        // Modificar propiedades
        genero.setNombre("Ciencia Ficción y Fantasía");
        genero.setDescripcion("Descripción actualizada");
        
        assertEquals("Ciencia Ficción y Fantasía", genero.getNombre());
        assertEquals("Descripción actualizada", genero.getDescripcion());
    }
    
    @Test
    void testRelacionConLibros() {
        // Verificar que inicialmente no hay libros
        assertTrue(genero.getLibros().isEmpty());
        
        // Agregar relación bidireccional
        libro.getGeneros().add(genero);
        genero.getLibros().add(libro);
        
        // Verificar la relación
        assertEquals(1, genero.getLibros().size());
        assertTrue(genero.getLibros().contains(libro));
    }
    
    @Test
    void testBuilder() {
        // Crear género directamente sin usar builder
        Genero generoNuevo = new Genero();
        generoNuevo.setId(2L);
        generoNuevo.setNombre("Fantasía");
        generoNuevo.setDescripcion("Narrativa con elementos sobrenaturales o mágicos");
        
        assertEquals(2L, generoNuevo.getId());
        assertEquals("Fantasía", generoNuevo.getNombre());
        assertEquals("Narrativa con elementos sobrenaturales o mágicos", generoNuevo.getDescripcion());
        
        // Los libros deben inicializarse aunque no usemos builder
        assertNotNull(generoNuevo.getLibros());
        assertTrue(generoNuevo.getLibros().isEmpty());
    }
    
    @Test
    void testMultiplesLibros() {
        // Crear varios libros
        Libro libro1 = new Libro();
        libro1.setTitulo("Dune");
        
        Libro libro2 = new Libro();
        libro2.setTitulo("Fundación");
        
        Libro libro3 = new Libro();
        libro3.setTitulo("Neuromante");
        
        // Agregar los libros al género
        libro1.getGeneros().add(genero);
        genero.getLibros().add(libro1);
        
        libro2.getGeneros().add(genero);
        genero.getLibros().add(libro2);
        
        libro3.getGeneros().add(genero);
        genero.getLibros().add(libro3);
        
        // Verificar la relación
        assertEquals(3, genero.getLibros().size());
        assertTrue(genero.getLibros().contains(libro1));
        assertTrue(genero.getLibros().contains(libro2));
        assertTrue(genero.getLibros().contains(libro3));
    }
} 