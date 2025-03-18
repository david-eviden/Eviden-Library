package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Valoracion;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroDao libroDao;
    
    @Mock
    private DetallePedidoService detallePedidoService;
    
    @InjectMocks
    private LibroServiceImpl libroService;
    
    private Libro libro1;
    private Libro libro2;
    private List<Libro> listaLibros;
    private Autor autor;
    private Genero genero;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Autor Test");
        autor.setBiografia("Biografía de prueba");
        
        genero = new Genero();
        genero.setId(1L);
        genero.setNombre("Género Test");
        genero.setDescripcion("Descripción de prueba");
        
        libro1 = new Libro();
        libro1.setTitulo("Libro 1");
        libro1.setPrecio(29.99);
        libro1.setStock(10);
        libro1.setDescripcion("Descripción del libro 1");
        libro1.getAutores().add(autor);
        libro1.getGeneros().add(genero);
        
        libro2 = new Libro();
        libro2.setTitulo("Libro 2");
        libro2.setPrecio(19.99);
        libro2.setStock(5);
        libro2.setDescripcion("Descripción del libro 2");
        libro2.getAutores().add(autor);
        
        // Agregar algunas valoraciones a libro1
        Valoracion v1 = new Valoracion();
        v1.setPuntuacion(4);
        v1.setLibro(libro1);
        
        Valoracion v2 = new Valoracion();
        v2.setPuntuacion(5);
        v2.setLibro(libro1);
        
        libro1.getValoraciones().add(v1);
        libro1.getValoraciones().add(v2);
        
        listaLibros = Arrays.asList(libro1, libro2);
    }
    
    @Test
    void testFindAll() {
        // Configurar el mock
        when(libroDao.findAll()).thenReturn(listaLibros);
        
        // Ejecutar el método a probar
        List<Libro> resultado = libroService.findAll();
        
        // Verificar resultados
        assertEquals(2, resultado.size());
        assertEquals("Libro 1", resultado.get(0).getTitulo());
        assertEquals("Libro 2", resultado.get(1).getTitulo());
        
        // Verificar que se llamó al método correcto del DAO
        verify(libroDao).findAll();
    }
    
    @Test
    void testFindAllPaginado() {
        // Configurar paginación
        Pageable pageable = PageRequest.of(0, 2);
        Page<Libro> paginaLibros = new PageImpl<>(listaLibros, pageable, listaLibros.size());
        
        // Configurar el mock
        when(libroDao.findAll(pageable)).thenReturn(paginaLibros);
        
        // Ejecutar el método a probar
        Page<Libro> resultado = libroService.findAllPaginado(pageable);
        
        // Verificar resultados
        assertEquals(2, resultado.getContent().size());
        assertEquals(1, resultado.getTotalPages());
        assertEquals(2, resultado.getTotalElements());
        
        // Verificar que se llamó al método correcto del DAO
        verify(libroDao).findAll(pageable);
    }
    
    @Test
    void testFindByAutorIdPaginado() {
        // Configurar paginación
        Pageable pageable = PageRequest.of(0, 2);
        Page<Libro> paginaLibros = new PageImpl<>(listaLibros, pageable, listaLibros.size());
        
        // Configurar el mock
        when(libroDao.findByAutoresId(1L, pageable)).thenReturn(paginaLibros);
        
        // Ejecutar el método a probar
        Page<Libro> resultado = libroService.findByAutorIdPaginado(1L, pageable);
        
        // Verificar resultados
        assertEquals(2, resultado.getContent().size());
        assertEquals(1, resultado.getTotalPages());
        assertEquals(2, resultado.getTotalElements());
        
        // Verificar que se llamó al método correcto del DAO
        verify(libroDao).findByAutoresId(1L, pageable);
    }
    
    @Test
    void testFindById() {
        // Configurar el mock
        when(libroDao.findById(1L)).thenReturn(Optional.of(libro1));
        
        // Ejecutar el método a probar
        Libro resultado = libroService.findById(1L);
        
        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Libro 1", resultado.getTitulo());
        
        // Verificar que se llamó al método correcto del DAO
        verify(libroDao).findById(1L);
    }
    
    @Test
    void testFindByTitulo() {
        // Configurar el mock
        when(libroDao.findByTitulo("Libro 1")).thenReturn(Optional.of(libro1));
        
        // Ejecutar el método a probar
        Libro resultado = libroService.findByTitulo("Libro 1");
        
        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Libro 1", resultado.getTitulo());
        
        // Verificar que se llamó al método correcto del DAO
        verify(libroDao).findByTitulo("Libro 1");
    }
    
    @Test
    void testSave() {
        // Configurar el mock
        when(libroDao.save(libro1)).thenReturn(libro1);
        
        // Ejecutar el método a probar
        Libro resultado = libroService.save(libro1);
        
        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Libro 1", resultado.getTitulo());
        
        // Verificar que se llamó al método correcto del DAO
        verify(libroDao).save(libro1);
    }
    
    @Test
    void testDelete() {
        // Configurar mocks para los detalles de pedido
        List<DetallePedido> detallesPedido = new ArrayList<>();
        when(detallePedidoService.findByPedidoId(libro1.getId())).thenReturn(detallesPedido);
        
        // Ejecutar el método a probar
        libroService.delete(libro1);
        
        // Verificar que se llamaron a los métodos correctos
        verify(detallePedidoService).findByPedidoId(libro1.getId());
        verify(libroDao).save(libro1); // Primero guarda para actualizar relaciones
        verify(libroDao).delete(libro1); // Luego elimina
    }
    
    @Test
    void testGetMejorValorados() {
        // Configurar los mocks
        when(libroDao.findAll()).thenReturn(listaLibros);
        when(libroDao.findTop10MejorValorados()).thenReturn(listaLibros);
        
        // Ejecutar el método a probar
        List<Libro> resultado = libroService.getMejorValorados();
        
        // Verificar resultados
        assertEquals(2, resultado.size());
        assertEquals("Libro 1", resultado.get(0).getTitulo());
        assertEquals("Libro 2", resultado.get(1).getTitulo());
        
        // Verificar que se calcularon las valoraciones medias
        verify(libroDao).findAll();
        verify(libroDao).findTop10MejorValorados();
    }
    
    @Test
    void testObtenerLibroConValoracionMedia() {
        // Configurar los mocks
        when(libroDao.findById(1L)).thenReturn(Optional.of(libro1));
        when(libroDao.save(libro1)).thenReturn(libro1);
        
        // Ejecutar el método a probar
        Libro resultado = libroService.obtenerLibroConValoracionMedia(1L);
        
        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(4.5, resultado.getValoracionMedia()); // (4+5)/2 = 4.5
        
        // Verificar que se llamaron a los métodos correctos
        verify(libroDao).findById(1L);
        verify(libroDao).save(libro1);
    }
} 