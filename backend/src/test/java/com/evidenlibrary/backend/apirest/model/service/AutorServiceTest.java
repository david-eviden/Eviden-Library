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

import com.evidenlibrary.backend.apirest.model.dao.AutorDao;
import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@ExtendWith(MockitoExtension.class)
class AutorServiceTest {

    @Mock
    private AutorDao autorDao;
    
    @Mock
    private LibroDao libroDao;
    
    @InjectMocks
    private AutorServiceImpl autorService;
    
    private Autor autor1;
    private Autor autor2;
    private List<Autor> listaAutores;
    private Libro libro1;
    private Libro libro2;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        autor1 = new Autor();
        autor1.setId(1L);
        autor1.setNombre("Gabriel García Márquez");
        autor1.setBiografia("Biografía de Gabriel García Márquez");
        
        autor2 = new Autor();
        autor2.setId(2L);
        autor2.setNombre("Jorge Luis Borges");
        autor2.setBiografia("Biografía de Jorge Luis Borges");
        
        listaAutores = new ArrayList<>();
        listaAutores.add(autor1);
        listaAutores.add(autor2);
        
        // Crear libros
        libro1 = new Libro();
        libro1.setTitulo("Cien años de soledad");
        libro1.setPrecio(29.99);
        libro1.setStock(10);
        
        libro2 = new Libro();
        libro2.setTitulo("El Aleph");
        libro2.setPrecio(19.99);
        libro2.setStock(5);
        
        // Establecer relaciones
        libro1.getAutores().add(autor1);
        autor1.getLibros().add(libro1);
        
        libro2.getAutores().add(autor2);
        autor2.getLibros().add(libro2);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(autorDao.findAll()).thenReturn(listaAutores);
        
        // Ejecutar método a probar
        List<Autor> resultado = autorService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals("Gabriel García Márquez", resultado.get(0).getNombre());
        assertEquals("Jorge Luis Borges", resultado.get(1).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(autorDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(autorDao.findById(1L)).thenReturn(Optional.of(autor1));
        
        // Ejecutar método a probar
        Autor resultado = autorService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Gabriel García Márquez", resultado.getNombre());
        
        // Verificar que se llamó al método correcto
        verify(autorDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(autorDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        Autor resultado = autorService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(autorDao).findById(99L);
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(autorDao.save(autor1)).thenReturn(autor1);
        
        // Ejecutar método a probar
        Autor resultado = autorService.save(autor1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Gabriel García Márquez", resultado.getNombre());
        
        // Verificar que se llamó al método correcto
        verify(autorDao).save(autor1);
    }
    
    @Test
    void testDelete() {
        // Ejecutar método a probar
        autorService.delete(autor1);
        
        // Verificar que se llamaron a los métodos correctos
        verify(libroDao).save(libro1);
        verify(autorDao).delete(autor1);
        
        // Verificar que se eliminó la relación
        assertFalse(libro1.getAutores().contains(autor1));
    }
    
    @Test
    void testDeleteAll() {
        // Configurar mock
        when(autorDao.findAll()).thenReturn(listaAutores);
        
        // Ejecutar método a probar
        autorService.deleteAll();
        
        // Verificar que se llamaron a los métodos correctos
        verify(autorDao).findAll();
        verify(libroDao).save(libro1);
        verify(libroDao).save(libro2);
        verify(autorDao).deleteAll();
        
        // Verificar que se eliminaron las relaciones
        assertFalse(libro1.getAutores().contains(autor1));
        assertFalse(libro2.getAutores().contains(autor2));
    }
} 