package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
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

import com.evidenlibrary.backend.apirest.model.dao.PedidoDao;
import com.evidenlibrary.backend.apirest.model.entity.Pedido;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoDao pedidoDao;
    
    @InjectMocks
    private PedidoServiceImpl pedidoService;
    
    private Pedido pedido1;
    private Pedido pedido2;
    private List<Pedido> listaPedidos;
    private List<Pedido> pedidosUsuario1;
    private Usuario usuario1;
    private Usuario usuario2;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombre("Usuario1");
        usuario1.setEmail("usuario1@example.com");
        
        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("Usuario2");
        usuario2.setEmail("usuario2@example.com");
        
        // Crear pedidos
        pedido1 = new Pedido();
        pedido1.setUsuario(usuario1);
        pedido1.setFechaPedido(new Date());
        pedido1.setTotal(99.99);
        pedido1.setEstado("ENTREGADO");
        pedido1.setDireccionEnvio("Calle Test 123");
        
        pedido2 = new Pedido();
        pedido2.setUsuario(usuario2);
        pedido2.setFechaPedido(new Date());
        pedido2.setTotal(149.99);
        pedido2.setEstado("PENDIENTE");
        pedido2.setDireccionEnvio("Avenida Principal 456");
        
        listaPedidos = new ArrayList<>();
        listaPedidos.add(pedido1);
        listaPedidos.add(pedido2);
        
        pedidosUsuario1 = new ArrayList<>();
        pedidosUsuario1.add(pedido1);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(pedidoDao.findAll()).thenReturn(listaPedidos);
        
        // Ejecutar método a probar
        List<Pedido> resultado = pedidoService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verificar que se llamó al método correcto
        verify(pedidoDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(pedidoDao.findById(1L)).thenReturn(Optional.of(pedido1));
        
        // Ejecutar método a probar
        Pedido resultado = pedidoService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(usuario1, resultado.getUsuario());
        assertEquals(99.99, resultado.getTotal());
        assertEquals("ENTREGADO", resultado.getEstado());
        assertEquals("Calle Test 123", resultado.getDireccionEnvio());
        
        // Verificar que se llamó al método correcto
        verify(pedidoDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(pedidoDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        Pedido resultado = pedidoService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(pedidoDao).findById(99L);
    }
    
    @Test
    void testFindPedidosByUsuarioId() {
        // Configurar mock
        when(pedidoDao.findByUsuarioId(1L)).thenReturn(pedidosUsuario1);
        
        // Ejecutar método a probar
        List<Pedido> resultado = pedidoService.findPedidosByUsuarioId(1L);
        
        // Verificar resultado
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(usuario1, resultado.get(0).getUsuario());
        
        // Verificar que se llamó al método correcto
        verify(pedidoDao).findByUsuarioId(1L);
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(pedidoDao.save(pedido1)).thenReturn(pedido1);
        
        // Ejecutar método a probar
        Pedido resultado = pedidoService.save(pedido1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(99.99, resultado.getTotal());
        assertEquals("ENTREGADO", resultado.getEstado());
        
        // Verificar que se llamó al método correcto
        verify(pedidoDao).save(pedido1);
    }
    
    @Test
    void testDelete() {
        // Ejecutar método a probar
        pedidoService.delete(pedido1);
        
        // Verificar que se llamó al método correcto
        verify(pedidoDao).delete(pedido1);
    }
} 