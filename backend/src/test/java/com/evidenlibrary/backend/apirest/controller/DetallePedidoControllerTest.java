package com.evidenlibrary.backend.apirest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Pedido;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.DetallePedidoService;

class DetallePedidoControllerTest {

    @Mock
    private DetallePedidoService detallePedidoService;

    @InjectMocks
    private DetallePedidoController detallePedidoController;

    private DetallePedido detallePedido1;
    private DetallePedido detallePedido2;
    private List<DetallePedido> detallesPedido;
    private Pedido pedido;
    private Libro libro;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Crear usuario de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@example.com");
        usuario.setPassword("password123");
        usuario.setRol("ROLE_USER");
        
        // Crear libro de prueba
        libro = new Libro();
        libro.setTitulo("El Quijote");
        
        // Crear pedido de prueba
        pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");
        pedido.setDireccionEnvio("Calle Principal 123");
        pedido.setTotal(51.98);
        
        // Configurar datos de prueba para detalles de pedido
        detallePedido1 = new DetallePedido();
        detallePedido1.setPedido(pedido);
        detallePedido1.setLibro(libro);
        detallePedido1.setCantidad(2);
        detallePedido1.setPrecioUnitario(25.99);
        
        Libro libro2 = new Libro();
        libro2.setTitulo("Cien años de soledad");
        
        detallePedido2 = new DetallePedido();
        detallePedido2.setPedido(pedido);
        detallePedido2.setLibro(libro2);
        detallePedido2.setCantidad(1);
        detallePedido2.setPrecioUnitario(29.99);
        
        detallesPedido = new ArrayList<>();
        detallesPedido.add(detallePedido1);
        detallesPedido.add(detallePedido2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(detallePedidoService.findAll()).thenReturn(detallesPedido);
        
        // Ejecutar método a probar
        List<DetallePedido> resultado = detallePedidoController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(2, resultado.get(0).getCantidad());
        assertEquals(1, resultado.get(1).getCantidad());
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findAll();
    }

    @SuppressWarnings("null")
    @Test
    void testShow_Success() {
        // Configurar mock
        when(detallePedidoService.findById(1L)).thenReturn(detallePedido1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof DetallePedido);
        assertEquals(1L, ((DetallePedido) respuesta.getBody()).getId());
        assertEquals(2, ((DetallePedido) respuesta.getBody()).getCantidad());
        assertEquals(25.99, ((DetallePedido) respuesta.getBody()).getPrecioUnitario());
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_NotFound() {
        // Configurar mock
        when(detallePedidoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(detallePedidoService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByPedidoId_Success() {
        // Configurar mock
        when(detallePedidoService.findByPedidoId(1L)).thenReturn(detallesPedido);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.findByPedidoId(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<DetallePedido> body = (List<DetallePedido>) respuesta.getBody();
        assertEquals(2, body.size());
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findByPedidoId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByPedidoId_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(detallePedidoService.findByPedidoId(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.findByPedidoId(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findByPedidoId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_Success() {
        // Configurar mocks
        when(detallePedidoService.save(any(DetallePedido.class))).thenReturn(detallePedido1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.create(detallePedido1);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("detallePedido"));
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).save(any(DetallePedido.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(detallePedidoService.save(any(DetallePedido.class))).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.create(detallePedido1);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).save(any(DetallePedido.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_Success() {
        // Configurar mocks
        when(detallePedidoService.findById(1L)).thenReturn(detallePedido1);
        when(detallePedidoService.save(any(DetallePedido.class))).thenReturn(detallePedido1);
        
        // Crear un detalle con datos actualizados
        DetallePedido detalleActualizado = new DetallePedido();
        detalleActualizado.setLibro(libro);
        detalleActualizado.setPedido(pedido);
        detalleActualizado.setCantidad(3); // Incrementar cantidad
        detalleActualizado.setPrecioUnitario(25.99);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.update(detalleActualizado, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("detallePedido"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(detallePedidoService).findById(1L);
        verify(detallePedidoService).save(any(DetallePedido.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(detallePedidoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.update(detallePedido1, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_DatabaseError() {
        // Configurar mocks
        when(detallePedidoService.findById(1L)).thenReturn(detallePedido1);
        when(detallePedidoService.save(any(DetallePedido.class))).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.update(detallePedido1, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(detallePedidoService).findById(1L);
        verify(detallePedidoService).save(any(DetallePedido.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_Success() {
        // Configurar mocks
        when(detallePedidoService.findById(1L)).thenReturn(detallePedido1);
        doNothing().when(detallePedidoService).delete(detallePedido1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(detallePedidoService).findById(1L);
        verify(detallePedidoService).delete(detallePedido1);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(detallePedidoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_DatabaseError() {
        // Configurar mocks
        when(detallePedidoService.findById(1L)).thenReturn(detallePedido1);
        doNothing().when(detallePedidoService).delete(detallePedido1);
        when(detallePedidoService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detallePedidoController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(detallePedidoService).findById(1L);
    }
} 