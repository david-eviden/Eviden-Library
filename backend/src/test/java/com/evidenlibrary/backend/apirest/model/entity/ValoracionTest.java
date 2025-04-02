package com.evidenlibrary.backend.apirest.model.entity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValoracionTest {
    
    private Valoracion valoracion;
    private Libro libro;
    private Usuario usuario;
    private Date fecha;
    
    @BeforeEach
    void setUp() {
        // Crear un libro para pruebas
        libro = new Libro();
        libro.setTitulo("Test Libro");
        
        // Crear un usuario para pruebas
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test");
        usuario.setApellido("User");
        
        // Fecha actual
        fecha = new Date();
        
        // Crear una valoración para pruebas
        valoracion = new Valoracion();
        valoracion.setLibro(libro);
        valoracion.setUsuario(usuario);
        valoracion.setPuntuacion(5);
        valoracion.setComentario("Comentario de prueba");
        valoracion.setFecha(fecha);
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, valoracion.getId());
        assertEquals(libro, valoracion.getLibro());
        assertEquals(usuario, valoracion.getUsuario());
        assertEquals(5, valoracion.getPuntuacion());
        assertEquals("Comentario de prueba", valoracion.getComentario());
        assertEquals(fecha, valoracion.getFecha());
        
        // Modificar propiedades
        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo("Otro Libro");
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(2L);
        nuevoUsuario.setEmail("otro@example.com");
        nuevoUsuario.setNombre("Otro");
        nuevoUsuario.setApellido("Usuario");
        
        Date nuevaFecha = new Date();
        
        valoracion.setLibro(nuevoLibro);
        valoracion.setUsuario(nuevoUsuario);
        valoracion.setPuntuacion(4);
        valoracion.setComentario("Otro comentario");
        valoracion.setFecha(nuevaFecha);
        
        assertEquals(nuevoLibro, valoracion.getLibro());
        assertEquals(nuevoUsuario, valoracion.getUsuario());
        assertEquals(4, valoracion.getPuntuacion());
        assertEquals("Otro comentario", valoracion.getComentario());
        assertEquals(nuevaFecha, valoracion.getFecha());
    }
    
    @Test
    void testGetLibroDetalles() {
        // Verificar que devuelve el ID del libro
        assertEquals(1L, valoracion.getLibroDetalles());
        
        // Verificar que retorna 0 si el libro es nulo
        valoracion.setLibro(null);
        assertEquals(0L, valoracion.getLibroDetalles());
    }
    
    @Test
    void testBuilder() {
        // Crear valoración
        Valoracion valoracionNueva = new Valoracion();
        valoracionNueva.setLibro(libro);
        valoracionNueva.setUsuario(usuario);
        valoracionNueva.setPuntuacion(4);
        valoracionNueva.setComentario("Comentario con builder");
        valoracionNueva.setFecha(fecha);
        
        assertEquals(2L, valoracionNueva.getId());
        assertEquals(libro, valoracionNueva.getLibro());
        assertEquals(usuario, valoracionNueva.getUsuario());
        assertEquals(4, valoracionNueva.getPuntuacion());
        assertEquals("Comentario con builder", valoracionNueva.getComentario());
        assertEquals(fecha, valoracionNueva.getFecha());
    }
} 