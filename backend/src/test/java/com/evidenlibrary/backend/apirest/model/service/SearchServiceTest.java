package com.evidenlibrary.backend.apirest.model.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.evidenlibrary.backend.apirest.model.dao.AutorDao;
import com.evidenlibrary.backend.apirest.model.dao.GeneroDao;
import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

class SearchServiceTest {

    @Mock
    private LibroDao libroDao;
    
    @Mock
    private AutorDao autorDao;
    
    @Mock
    private GeneroDao generoDao;
    
    @InjectMocks
    private SearchService searchService;
    
    private Libro libro1, libro2;
    private Autor autor1, autor2;
    private Genero genero1, genero2;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba
        libro1 = new Libro();
        libro1.setTitulo("El Quijote");
        libro1.setDescripcion("Novela clásica española");
        libro1.setAnio("1605");
        
        libro2 = new Libro();
        libro2.setTitulo("Cien años de soledad");
        libro2.setDescripcion("Obra maestra del realismo mágico");
        libro2.setAnio("1967");
        
        autor1 = new Autor();
        autor1.setId(1L);
        autor1.setNombre("Miguel de Cervantes");
        
        autor2 = new Autor();
        autor2.setId(2L);
        autor2.setNombre("Gabriel García Márquez");
        
        genero1 = new Genero();
        genero1.setId(1L);
        genero1.setNombre("Novela");
        
        genero2 = new Genero();
        genero2.setId(2L);
        genero2.setNombre("Realismo Mágico");
    }
    
    @Test
    void searchLibros_ConQueryValida_RetornaListaLibros() {
        // Configurar mocks
        when(libroDao.findByTituloContainingIgnoreCase("quijote")).thenReturn(Arrays.asList(libro1));
        when(libroDao.findByTituloContainingIgnoreCase("cien")).thenReturn(Arrays.asList(libro2));
        when(libroDao.findByAnio("1605")).thenReturn(Arrays.asList(libro1));
        when(libroDao.findByAnio("1967")).thenReturn(Arrays.asList(libro2));
        
        // Ejecutar búsqueda
        List<Libro> resultados = searchService.searchLibros("quijote");
        
        // Verificar
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("El Quijote", resultados.get(0).getTitulo());
        
        // Verificar que se llamaron los métodos correctos
        verify(libroDao).findByTituloContainingIgnoreCase("quijote");
        verify(libroDao).findByAnio("quijote");
    }
    
    @Test
    void searchLibros_ConQueryVacia_RetornaListaVacia() {
        // Ejecutar búsqueda con query vacía
        List<Libro> resultados = searchService.searchLibros("");
        
        // Verificar
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
        
        // Verificar que no se llamaron los métodos del dao
        verify(libroDao, never()).findByTituloContainingIgnoreCase(anyString());
        verify(libroDao, never()).findByAnio(anyString());
    }
    
    @Test
    void searchAutores_ConQueryValida_RetornaListaAutores() {
        // Configurar mocks
        when(autorDao.findByNombreContainingIgnoreCase("cervantes")).thenReturn(Arrays.asList(autor1));
        
        // Ejecutar búsqueda
        List<Autor> resultados = searchService.searchAutores("cervantes");
        
        // Verificar
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Miguel de Cervantes", resultados.get(0).getNombre());
        
        // Verificar que se llamaron los métodos correctos
        verify(autorDao).findByNombreContainingIgnoreCase("cervantes");
    }
    
    @Test
    void searchGeneros_ConQueryValida_RetornaListaGeneros() {
        // Configurar mocks
        when(generoDao.findByNombreContainingIgnoreCase("novela")).thenReturn(Arrays.asList(genero1));
        
        // Ejecutar búsqueda
        List<Genero> resultados = searchService.searchGeneros("novela");
        
        // Verificar
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Novela", resultados.get(0).getNombre());
        
        // Verificar que se llamaron los métodos correctos
        verify(generoDao).findByNombreContainingIgnoreCase("novela");
    }
    
    @Test
    void findLibrosByAutorId_AutorExistente_RetornaLibros() {
        // Configurar mocks
        when(autorDao.findById(1L)).thenReturn(Optional.of(autor1));
        when(libroDao.findByAutoresContaining(autor1)).thenReturn(Arrays.asList(libro1));
        
        // Ejecutar método
        List<Libro> resultados = searchService.findLibrosByAutorId(1L);
        
        // Verificar
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("El Quijote", resultados.get(0).getTitulo());
        
        // Verificar que se llamaron los métodos correctos
        verify(autorDao).findById(1L);
        verify(libroDao).findByAutoresContaining(autor1);
    }
    
    @Test
    void findLibrosByAutorId_AutorNoExistente_LanzaExcepcion() {
        // Configurar mocks
        when(autorDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método y verificar que lanza excepción
        assertThrows(RuntimeException.class, () -> {
            searchService.findLibrosByAutorId(99L);
        });
        
        // Verificar que se llamaron los métodos correctos
        verify(autorDao).findById(99L);
        verify(libroDao, never()).findByAutoresContaining(any(Autor.class));
    }
    
    @Test
    void findLibrosByGeneroId_GeneroExistente_RetornaLibros() {
        // Configurar mocks
        when(generoDao.findById(1L)).thenReturn(Optional.of(genero1));
        when(libroDao.findByGenerosContaining(genero1)).thenReturn(Arrays.asList(libro1, libro2));
        
        // Ejecutar método
        List<Libro> resultados = searchService.findLibrosByGeneroId(1L);
        
        // Verificar
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        
        // Verificar que se llamaron los métodos correctos
        verify(generoDao).findById(1L);
        verify(libroDao).findByGenerosContaining(genero1);
    }
    
    @Test
    void findLibrosByAnio_RetornaLibros() {
        // Configurar mocks
        when(libroDao.findByAnio("1605")).thenReturn(Arrays.asList(libro1));
        
        // Ejecutar método
        List<Libro> resultados = searchService.findLibrosByAnio("1605");
        
        // Verificar
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("El Quijote", resultados.get(0).getTitulo());
        
        // Verificar que se llamaron los métodos correctos
        verify(libroDao).findByAnio("1605");
    }
} 