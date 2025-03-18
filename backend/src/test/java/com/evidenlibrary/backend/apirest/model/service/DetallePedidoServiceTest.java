package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.evidenlibrary.backend.apirest.model.dao.DetallePedidoDao;
import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Pedido;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;

@ExtendWith(MockitoExtension.class)
class DetallePedidoServiceTest {

    @Mock
    private DetallePedidoDao detallePedidoDao;
    
    @InjectMocks
    private DetallePedidoServiceImpl detallePedidoService;
    
    private DetallePedido detalle1;
    private DetallePedido detalle2;
    private List<DetallePedido> listaDetalles;
    private List<DetallePedido> detallesLibro1;
    private List<DetallePedido> detallesPedido1;
    private Pedido pedido1;
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
        
        // Crear pedido
        pedido1 = new Pedido();
        pedido1.setUsuario(usuario1);
        pedido1.setFechaPedido(new Date());
        pedido1.setDireccionEnvio("Calle Principal 123");
        pedido1.setEstado("procesando");
        pedido1.setTotal(49.98);
        
        // Crear detalles de pedido
        detalle1 = new DetallePedido();
        detalle1.setPedido(pedido1);
        detalle1.setLibro(libro1);
        detalle1.setCantidad(1);
        detalle1.setPrecioUnitario(29.99);
        
        detalle2 = new DetallePedido();
        detalle2.setPedido(pedido1);
        detalle2.setLibro(libro2);
        detalle2.setCantidad(1);
        detalle2.setPrecioUnitario(19.99);
        
        listaDetalles = Arrays.asList(detalle1, detalle2);
        
        detallesLibro1 = new ArrayList<>();
        detallesLibro1.add(detalle1);
        
        detallesPedido1 = new ArrayList<>();
        detallesPedido1.add(detalle1);
        detallesPedido1.add(detalle2);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(detallePedidoDao.findAll()).thenReturn(listaDetalles);
        
        // Ejecutar método a probar
        List<DetallePedido> resultado = detallePedidoService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(detallePedidoDao.findById(1L)).thenReturn(Optional.of(detalle1));
        
        // Ejecutar método a probar
        DetallePedido resultado = detallePedidoService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(libro1, resultado.getLibro());
        assertEquals(1, resultado.getCantidad());
        assertEquals(29.99, resultado.getPrecioUnitario());
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(detallePedidoDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        DetallePedido resultado = detallePedidoService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoDao).findById(99L);
    }
    
    @Test
    void testFindByLibroId() {
        // Configurar mock
        when(detallePedidoDao.findByLibroId(1L)).thenReturn(detallesLibro1);
        
        // Ejecutar método a probar
        List<DetallePedido> resultado = detallePedidoService.findByLibroId(1L);
        
        // Verificar resultado
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(libro1, resultado.get(0).getLibro());
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoDao).findByLibroId(1L);
    }
    
    @Test
    void testFindByPedidoId() {
        // Configurar mock
        when(detallePedidoDao.findByPedidoId(1L)).thenReturn(detallesPedido1);
        
        // Ejecutar método a probar
        List<DetallePedido> resultado = detallePedidoService.findByPedidoId(1L);
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoDao).findByPedidoId(1L);
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(detallePedidoDao.save(detalle1)).thenReturn(detalle1);
        
        // Ejecutar método a probar
        DetallePedido resultado = detallePedidoService.save(detalle1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(libro1, resultado.getLibro());
        assertEquals(1, resultado.getCantidad());
        assertEquals(29.99, resultado.getPrecioUnitario());
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoDao).save(detalle1);
    }
    
    @Test
    void testDelete() {
        // Ejecutar método a probar
        detallePedidoService.delete(detalle1);
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoDao).delete(detalle1);
    }
    
    @Test
    void testDeleteAll() {
        // Ejecutar método a probar
        detallePedidoService.deleteAll();
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoDao).deleteAll();
    }
} 