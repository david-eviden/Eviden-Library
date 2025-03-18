package com.evidenlibrary.backend.apirest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.service.SearchService;

class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private List<Libro> libros;
    private List<Autor> autores;
    private List<Genero> generos;
    private Libro libro1;
    private Libro libro2;
    private Autor autor;
    private Genero genero;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba para libros
        libro1 = new Libro();
        libro1.setTitulo("El Quijote");
        libro1.setAnio("1605");
        
        libro2 = new Libro();
        libro2.setTitulo("Cien años de soledad");
        libro2.setAnio("1967");
        
        libros = new ArrayList<>();
        libros.add(libro1);
        libros.add(libro2);
        
        // Configurar datos de prueba para autores
        autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Miguel de Cervantes");
        
        autores = new ArrayList<>();
        autores.add(autor);
        
        // Configurar datos de prueba para géneros
        genero = new Genero();
        genero.setId(1L);
        genero.setNombre("Novela");
        
        generos = new ArrayList<>();
        generos.add(genero);
    }

    @SuppressWarnings("null")
	@Test
    void testSearch_AllCategoriesEnabled() {
        // Configurar mocks
        when(searchService.searchLibros("Quijote")).thenReturn(libros);
        when(searchService.searchAutores("Quijote")).thenReturn(autores);
        when(searchService.searchGeneros("Quijote")).thenReturn(generos);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = searchController.search("Quijote", true, true, true);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> results = (Map<String, Object>) respuesta.getBody();
        
        assertNotNull(results.get("libros"));
        assertNotNull(results.get("autores"));
        assertNotNull(results.get("generos"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(searchService).searchLibros("Quijote");
        verify(searchService).searchAutores("Quijote");
        verify(searchService).searchGeneros("Quijote");
    }
    
    @SuppressWarnings("null")
	@Test
    void testSearch_OnlyLibrosEnabled() {
        // Configurar mocks
        when(searchService.searchLibros("Quijote")).thenReturn(libros);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = searchController.search("Quijote", true, false, false);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> results = (Map<String, Object>) respuesta.getBody();
        
        assertNotNull(results.get("libros"));
        assertTrue(!results.containsKey("autores"));
        assertTrue(!results.containsKey("generos"));
        
        // Verificar que se llamó al método correcto
        verify(searchService).searchLibros("Quijote");
    }
    
    @SuppressWarnings("null")
	@Test
    void testSearch_CustomCombination() {
        // Configurar mocks
        when(searchService.searchLibros("Garcia")).thenReturn(libros);
        when(searchService.searchGeneros("Garcia")).thenReturn(generos);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = searchController.search("Garcia", true, false, true);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> results = (Map<String, Object>) respuesta.getBody();
        
        assertNotNull(results.get("libros"));
        assertTrue(!results.containsKey("autores"));
        assertNotNull(results.get("generos"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(searchService).searchLibros("Garcia");
        verify(searchService).searchGeneros("Garcia");
    }
    
    @SuppressWarnings("null")
	@Test
    void testGetLibrosByAutor() {
        // Configurar mocks
        when(searchService.findLibrosByAutorId(1L)).thenReturn(libros);
        
        // Ejecutar método a probar
        ResponseEntity<List<Libro>> respuesta = searchController.getLibrosByAutor(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(2, respuesta.getBody().size());
        assertEquals("El Quijote", respuesta.getBody().get(0).getTitulo());
        
        // Verificar que se llamó al método correcto
        verify(searchService).findLibrosByAutorId(1L);
    }
    
    @SuppressWarnings("null")
	@Test
    void testGetLibrosByGenero() {
        // Configurar mocks
        when(searchService.findLibrosByGeneroId(1L)).thenReturn(libros);
        
        // Ejecutar método a probar
        ResponseEntity<List<Libro>> respuesta = searchController.getLibrosByGenero(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(2, respuesta.getBody().size());
        assertEquals("El Quijote", respuesta.getBody().get(0).getTitulo());
        
        // Verificar que se llamó al método correcto
        verify(searchService).findLibrosByGeneroId(1L);
    }
    
    @SuppressWarnings("null")
	@Test
    void testGetLibrosByAnio() {
        // Configurar mocks
        when(searchService.findLibrosByAnio("1605")).thenReturn(List.of(libro1));
        
        // Ejecutar método a probar
        ResponseEntity<List<Libro>> respuesta = searchController.getLibrosByAnio("1605");
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().size());
        assertEquals("El Quijote", respuesta.getBody().get(0).getTitulo());
        assertEquals("1605", respuesta.getBody().get(0).getAnio());
        
        // Verificar que se llamó al método correcto
        verify(searchService).findLibrosByAnio("1605");
    }
} 