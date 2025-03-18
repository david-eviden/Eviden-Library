package com.evidenlibrary.backend.apirest.model.entity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AutorTest {
    
    private Autor autor;
    private Libro libro;
    
    @BeforeEach
    void setUp() {
        // Crear un autor para pruebas
        autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Gabriel García Márquez");
        autor.setBiografia("Escritor colombiano, reconocido por Cien años de soledad");
        
        // Crear un libro para pruebas
        libro = new Libro();
        libro.setTitulo("Cien años de soledad");
        libro.setPrecio(29.99);
        libro.setStock(10);
        libro.setDescripcion("Una novela fundamental de la literatura latinoamericana");
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, autor.getId());
        assertEquals("Gabriel García Márquez", autor.getNombre());
        assertEquals("Escritor colombiano, reconocido por Cien años de soledad", autor.getBiografia());
        
        // Modificar propiedades
        autor.setNombre("Gabriel García Márquez (actualizado)");
        autor.setBiografia("Nueva biografía actualizada");
        
        assertEquals("Gabriel García Márquez (actualizado)", autor.getNombre());
        assertEquals("Nueva biografía actualizada", autor.getBiografia());
    }
    
    @Test
    void testRelacionConLibros() {
        // Verificar que inicialmente no hay libros
        assertTrue(autor.getLibros().isEmpty());
        
        // Agregar relación bidireccional
        libro.getAutores().add(autor);
        autor.getLibros().add(libro);
        
        // Verificar la relación
        assertEquals(1, autor.getLibros().size());
        assertTrue(autor.getLibros().contains(libro));
    }
    
    @Test
    void testGetLibrosSimples() {
        // Agregar un libro al autor
        libro.getAutores().add(autor);
        autor.getLibros().add(libro);
        
        // Obtener los libros simples
        Set<Autor.LibroSimple> librosSimples = autor.getLibrosSimples();
        
        // Verificar los resultados
        assertEquals(1, librosSimples.size());
        
        Autor.LibroSimple libroSimple = librosSimples.iterator().next();
        assertEquals(1L, libroSimple.getId());
        assertEquals("Cien años de soledad", libroSimple.getTitulo());
        assertEquals(29.99, libroSimple.getPrecio());
        assertEquals(10, libroSimple.getStock());
    }
    
    @Test
    void testLibroSimpleConstructores() {
        // Primer constructor
        Autor.LibroSimple libroSimple1 = new Autor.LibroSimple(1L, "Test Libro", 19.99, 5);
        assertEquals(1L, libroSimple1.getId());
        assertEquals("Test Libro", libroSimple1.getTitulo());
        assertEquals(19.99, libroSimple1.getPrecio());
        assertEquals(5, libroSimple1.getStock());
        
        // Segundo constructor (orden diferente de los parámetros)
        Autor.LibroSimple libroSimple2 = new Autor.LibroSimple(2L, 29.99, 10, "Otro Libro");
        assertEquals(2L, libroSimple2.getId());
        assertEquals("Otro Libro", libroSimple2.getTitulo());
        assertEquals(29.99, libroSimple2.getPrecio());
        assertEquals(10, libroSimple2.getStock());
    }
    
    @Test
    void testLibroSimpleSetters() {
        Autor.LibroSimple libroSimple = new Autor.LibroSimple(1L, "Test Libro", 19.99, 5);
        
        // Probar setters
        libroSimple.setId(3L);
        libroSimple.setTitulo("Libro Modificado");
        libroSimple.setPrecio(39.99);
        libroSimple.setStock(15);
        
        // Verificar cambios
        assertEquals(3L, libroSimple.getId());
        assertEquals("Libro Modificado", libroSimple.getTitulo());
        assertEquals(39.99, libroSimple.getPrecio());
        assertEquals(15, libroSimple.getStock());
    }
    
    @Test
    void testBuilder() {
        // Crear autor
        Autor autorNuevo = new Autor();
        autorNuevo.setId(2L);
        autorNuevo.setNombre("Jorge Luis Borges");
        autorNuevo.setBiografia("Escritor argentino, reconocido por Ficciones");
        
        assertEquals(2L, autorNuevo.getId());
        assertEquals("Jorge Luis Borges", autorNuevo.getNombre());
        assertEquals("Escritor argentino, reconocido por Ficciones", autorNuevo.getBiografia());
        assertTrue(autorNuevo.getLibros().isEmpty());
    }
} 