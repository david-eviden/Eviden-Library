package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evidenlibrary.backend.apirest.model.dao.DetalleCarritoDao;
import com.evidenlibrary.backend.apirest.model.entity.Carrito;
import com.evidenlibrary.backend.apirest.model.entity.DetalleCarrito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;

@ExtendWith(MockitoExtension.class)
class DetalleCarritoServiceTest {

    @Mock
    private DetalleCarritoDao detalleCarritoDao;
    
    @InjectMocks
    private DetalleCarritoServiceImpl detalleCarritoService;
    
    private DetalleCarrito detalle1;
    private DetalleCarrito detalle2;
    private List<DetalleCarrito> listaDetalles;
    private List<DetalleCarrito> detallesCarrito1;
    private Carrito carrito1;
    private Usuario usuario1;
    private Libro libro1;
    private Libro libro2;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombre("Usuario1");
        usuario1.setEmail("usuario1@example.com");
        
        // Crear libros
        libro1 = new Libro();
        libro1.setTitulo("Libro 1");
        libro1.setPrecio(29.99);
        libro1.setStock(10);
        
        libro2 = new Libro();
        libro2.setTitulo("Libro 2");
        libro2.setPrecio(19.99);
        libro2.setStock(5);
        
        // Crear carrito
        carrito1 = new Carrito();
        carrito1.setId(1L);
        carrito1.setUsuario(usuario1);
        carrito1.setFechaCreacion(new Date());
        carrito1.setEstado("activo");
        
        // Crear detalles de carrito
        detalle1 = new DetalleCarrito();
        detalle1.setId(1L);
        detalle1.setCarrito(carrito1);
        detalle1.setLibro(libro1);
        detalle1.setCantidad(2);
        
        detalle2 = new DetalleCarrito();
        detalle2.setId(2L);
        detalle2.setCarrito(carrito1);
        detalle2.setLibro(libro2);
        detalle2.setCantidad(1);
        
        listaDetalles = Arrays.asList(detalle1, detalle2);
        
        detallesCarrito1 = new ArrayList<>();
        detallesCarrito1.add(detalle1);
        detallesCarrito1.add(detalle2);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(detalleCarritoDao.findAll()).thenReturn(listaDetalles);
        
        // Ejecutar método a probar
        List<DetalleCarrito> resultado = detalleCarritoService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(detalleCarritoDao.findById(1L)).thenReturn(Optional.of(detalle1));
        
        // Ejecutar método a probar
        DetalleCarrito resultado = detalleCarritoService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(libro1, resultado.getLibro());
        assertEquals(2, resultado.getCantidad());
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(detalleCarritoDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        DetalleCarrito resultado = detalleCarritoService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoDao).findById(99L);
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(detalleCarritoDao.save(detalle1)).thenReturn(detalle1);
        
        // Ejecutar método a probar
        DetalleCarrito resultado = detalleCarritoService.save(detalle1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(libro1, resultado.getLibro());
        assertEquals(2, resultado.getCantidad());
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoDao).save(detalle1);
    }
    
    @Test
    void testDelete() {
        // Ejecutar método a probar
        detalleCarritoService.delete(detalle1);
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoDao).delete(detalle1);
    }
    
    @Test
    void testFindByCarritoId() {
        // Configurar mock
        when(detalleCarritoDao.findByCarritoId(1L)).thenReturn(detallesCarrito1);
        
        // Ejecutar método a probar
        List<DetalleCarrito> resultado = detalleCarritoService.findByCarritoId(1L);
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoDao).findByCarritoId(1L);
    }
    
    @Test
    void testFindLibrosByCarritoId() {
        // Verificar que el método lanza UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            detalleCarritoService.findLibrosByCarritoId(1L);
        });
    }
} 