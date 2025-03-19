package com.evidenlibrary.backend.apirest.model.entity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FavoritoTest {
    
    private Favorito favorito;
    private Usuario usuario;
    private Libro libro;
    private Date fechaAgregado;
    
    @BeforeEach
    void setUp() {
        // Crear usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail("test@example.com");
        
        // Crear libro
        libro = new Libro();
        libro.setTitulo("Test Libro");
        libro.setPrecio(29.99);
        libro.setStock(10);
        
        // Fecha actual
        fechaAgregado = new Date();
        
        // Crear favorito
        favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setLibro(libro);
        favorito.setFechaAgregado(fechaAgregado);
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, favorito.getId());
        assertEquals(usuario, favorito.getUsuario());
        assertEquals(libro, favorito.getLibro());
        assertEquals(fechaAgregado, favorito.getFechaAgregado());
        
        // Modificar propiedades
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(2L);
        nuevoUsuario.setNombre("Otro");
        nuevoUsuario.setApellido("Usuario");
        
        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo("Otro Libro");
        
        Date nuevaFecha = new Date();
        
        favorito.setUsuario(nuevoUsuario);
        favorito.setLibro(nuevoLibro);
        favorito.setFechaAgregado(nuevaFecha);
        
        assertEquals(nuevoUsuario, favorito.getUsuario());
        assertEquals(nuevoLibro, favorito.getLibro());
        assertEquals(nuevaFecha, favorito.getFechaAgregado());
    }
    
    @Test
    void testRelacionesInversas() {
        // Verificar que el libro tiene este favorito
        libro.getFavoritos().add(favorito);
        
        assertEquals(1, libro.getFavoritos().size());
        assertTrue(libro.getFavoritos().contains(favorito));
        
        // Verificar que el usuario tiene este favorito
        usuario.getFavoritos().add(favorito);
        
        assertEquals(1, usuario.getFavoritos().size());
        assertTrue(usuario.getFavoritos().contains(favorito));
    }
    
    @Test
    void testBuilder() {
        // Fecha para el test
        Date fechaBuilder = new Date();
        
        // Crear favorito
        Favorito favoritoNuevo = new Favorito();
        favoritoNuevo.setUsuario(usuario);
        favoritoNuevo.setLibro(libro);
        favoritoNuevo.setFechaAgregado(fechaBuilder);
        
        assertEquals(2L, favoritoNuevo.getId());
        assertEquals(usuario, favoritoNuevo.getUsuario());
        assertEquals(libro, favoritoNuevo.getLibro());
        assertEquals(fechaBuilder, favoritoNuevo.getFechaAgregado());
    }
} 