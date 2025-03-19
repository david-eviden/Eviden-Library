package com.evidenlibrary.backend.apirest.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibroTest {
    
    private Libro libro;
    private Autor autor;
    private Genero genero;
    private Valoracion valoracion;
    
    @BeforeEach
    void setUp() {
        // Crear un libro para pruebas
        libro = new Libro();
        libro.setTitulo("Test Libro");
        libro.setPrecio(29.99);
        libro.setStock(10);
        libro.setDescripcion("Descripción de prueba");
        libro.setImagen("imagen-test.jpg");
        libro.setAnio("2023");
        
        // Crear un autor de prueba
        autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Autor Test");
        
        // Crear un género de prueba
        genero = new Genero();
        genero.setId(1L);
        genero.setNombre("Género Test");
        
        // Crear una valoración de prueba
        valoracion = new Valoracion();
        valoracion.setPuntuacion(5);
        valoracion.setLibro(libro);
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, libro.getId());
        assertEquals("Test Libro", libro.getTitulo());
        assertEquals(29.99, libro.getPrecio());
        assertEquals(10, libro.getStock());
        assertEquals("Descripción de prueba", libro.getDescripcion());
        assertEquals("imagen-test.jpg", libro.getImagen());
        assertEquals("2023", libro.getAnio());
        
        // Modificar propiedades
        libro.setTitulo("Libro Modificado");
        libro.setPrecio(39.99);
        libro.setStock(20);
        
        assertEquals("Libro Modificado", libro.getTitulo());
        assertEquals(39.99, libro.getPrecio());
        assertEquals(20, libro.getStock());
    }
    
    @Test
    void testRelacionesConAutores() {
        // Verificar que inicialmente no hay autores
        assertTrue(libro.getAutores().isEmpty());
        
        // Añadir autor
        libro.getAutores().add(autor);
        
        // Verificar que se añadió correctamente
        assertEquals(1, libro.getAutores().size());
        assertTrue(libro.getAutores().contains(autor));
    }
    
    @Test
    void testRelacionesConGeneros() {
        // Verificar que inicialmente no hay géneros
        assertTrue(libro.getGeneros().isEmpty());
        
        // Añadir género
        libro.getGeneros().add(genero);
        
        // Verificar que se añadió correctamente
        assertEquals(1, libro.getGeneros().size());
        assertTrue(libro.getGeneros().contains(genero));
    }
    
    @Test
    void testValoracionMedia_SinValoraciones() {
        // Sin valoraciones, debe retornar 0.0
        assertEquals(0.0, libro.getValoracionMedia());
    }
    
    @Test
    void testValoracionMedia_ConValoraciones() {
        // Añadir valoraciones
        Valoracion v1 = new Valoracion();
        v1.setPuntuacion(4);
        v1.setLibro(libro);
        
        Valoracion v2 = new Valoracion();
        v2.setPuntuacion(5);
        v2.setLibro(libro);
        
        libro.getValoraciones().add(v1);
        libro.getValoraciones().add(v2);
        
        // La media debe ser (4 + 5) / 2 = 4.5
        assertEquals(4.5, libro.getValoracionMedia());
    }
    
    @Test
    void testBuilder() {
        // Crear libro
        Libro libroNuevo = new Libro();
        libroNuevo.setTitulo("Libro con Builder");
        libroNuevo.setPrecio(19.99);
        libroNuevo.setStock(5);
        libroNuevo.setDescripcion("Descripción con builder");
        libroNuevo.setImagen("imagen-builder.jpg");
        libroNuevo.setAnio("2022");
        
        assertEquals(2L, libroNuevo.getId());
        assertEquals("Libro con Builder", libroNuevo.getTitulo());
        assertEquals(19.99, libroNuevo.getPrecio());
        assertEquals(5, libroNuevo.getStock());
        assertEquals("Descripción con builder", libroNuevo.getDescripcion());
        assertEquals("imagen-builder.jpg", libroNuevo.getImagen());
        assertEquals("2022", libroNuevo.getAnio());
    }
    
    @Test
    void testValoracionMedia_SetManual() {
        // Probar establecer una valoración media manualmente
        libro.setValoracionMedia(4.8);
        assertEquals(4.8, libro.getValoracionMedia());
    }
    
    @Test
    void testFavoritos() {
        // Inicialmente no hay favoritos
        assertTrue(libro.getFavoritos().isEmpty());
        
        // Añadir un favorito
        Favorito favorito = new Favorito();
        favorito.setLibro(libro);
        
        libro.getFavoritos().add(favorito);
        
        // Verificar que se añadió correctamente
        assertEquals(1, libro.getFavoritos().size());
    }
} 