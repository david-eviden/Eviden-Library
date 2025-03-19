package com.evidenlibrary.backend.apirest.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.evidenlibrary.backend.apirest.model.service.EmailService;
import com.evidenlibrary.backend.apirest.model.service.PedidoService;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;
    
    @Mock
    private UsuarioService usuarioService;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private DetallePedidoService detallePedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private Pedido pedido1;
    private Pedido pedido2;
    private List<Pedido> pedidos;
    private Usuario usuario;
    private DetallePedido detallePedido;
    private List<DetallePedido> detallesPedido;

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
        
        // Crear detalles de pedido
        detallePedido = new DetallePedido();
        
        Libro libro = new Libro();
        libro.setTitulo("El Quijote");
        
        detallePedido.setLibro(libro);
        detallePedido.setCantidad(2);
        detallePedido.setPrecioUnitario(25.99);
        
        detallesPedido = new ArrayList<>();
        detallesPedido.add(detallePedido);
        
        // Configurar datos de prueba para pedidos
        pedido1 = new Pedido();
        pedido1.setUsuario(usuario);
        pedido1.setFechaPedido(new Date());
        pedido1.setTotal(51.98); // 2 * 25.99
        pedido1.setDireccionEnvio("Calle Principal 123");
        pedido1.setEstado("PENDIENTE");
        pedido1.setDetalles(detallesPedido);
        
        pedido2 = new Pedido();
        pedido2.setUsuario(usuario);
        pedido2.setFechaPedido(new Date());
        pedido2.setTotal(77.97); // 3 * 25.99
        pedido2.setDireccionEnvio("Avenida Central 456");
        pedido2.setEstado("ENVIADO");
        
        DetallePedido detallePedido2 = new DetallePedido();
        detallePedido2.setLibro(libro);
        detallePedido2.setCantidad(3);
        detallePedido2.setPrecioUnitario(25.99);
        
        List<DetallePedido> detallesPedido2 = new ArrayList<>();
        detallesPedido2.add(detallePedido2);
        pedido2.setDetalles(detallesPedido2);
        
        pedidos = new ArrayList<>();
        pedidos.add(pedido1);
        pedidos.add(pedido2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(pedidoService.findAll()).thenReturn(pedidos);
        
        // Ejecutar método a probar
        List<Pedido> resultado = pedidoController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
        assertEquals("ENVIADO", resultado.get(1).getEstado());
        
        // Verificar que se llamó al método correcto
        verify(pedidoService).findAll();
    }

    @SuppressWarnings("null")
    @Test
    void testShow_Success() {
        // Configurar mock
        when(pedidoService.findById(1L)).thenReturn(pedido1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Pedido);
        assertEquals(1L, ((Pedido) respuesta.getBody()).getId());
        assertEquals("PENDIENTE", ((Pedido) respuesta.getBody()).getEstado());
        
        // Verificar que se llamó al método correcto
        verify(pedidoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_NotFound() {
        // Configurar mock
        when(pedidoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(pedidoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(pedidoService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(pedidoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByUsuarioId_Success() {
        // Configurar mock
        when(pedidoService.findPedidosByUsuarioId(1L)).thenReturn(pedidos);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.findByUsuarioId(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<Pedido> body = (List<Pedido>) respuesta.getBody();
        assertEquals(2, body.size());
        
        // Verificar que se llamó al método correcto
        verify(pedidoService).findPedidosByUsuarioId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testEnviarEmailConfirmacion_Success() {
        // Configurar mocks
        when(pedidoService.findById(1L)).thenReturn(pedido1);
        when(detallePedidoService.findByPedidoId(1L)).thenReturn(detallesPedido);
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(emailService.enviarEmailConfirmacionPedido(any(Pedido.class), anyString())).thenReturn(true);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.enviarEmailConfirmacion(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("emailEnviado"));
        assertEquals(true, body.get("emailEnviado"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(pedidoService).findById(1L);
        verify(detallePedidoService).findByPedidoId(1L);
        verify(usuarioService).findById(1L);
        verify(emailService).enviarEmailConfirmacionPedido(any(Pedido.class), anyString());
    }
    
    @SuppressWarnings("null")
    @Test
    void testEnviarEmailConfirmacion_EmailFallido() {
        // Configurar mocks
        when(pedidoService.findById(1L)).thenReturn(pedido1);
        when(detallePedidoService.findByPedidoId(1L)).thenReturn(detallesPedido);
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(emailService.enviarEmailConfirmacionPedido(any(Pedido.class), anyString())).thenReturn(false);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.enviarEmailConfirmacion(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("emailEnviado"));
        assertEquals(false, body.get("emailEnviado"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(pedidoService).findById(1L);
        verify(detallePedidoService).findByPedidoId(1L);
        verify(usuarioService).findById(1L);
        verify(emailService).enviarEmailConfirmacionPedido(any(Pedido.class), anyString());
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_Success() {
        // Configurar mocks
        when(pedidoService.save(any(Pedido.class))).thenReturn(pedido1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.create(pedido1);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("pedido"));
        
        // Verificar que se llamó al método correcto
        verify(pedidoService).save(any(Pedido.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(pedidoService.save(any(Pedido.class))).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.create(pedido1);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(pedidoService).save(any(Pedido.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_Success() {
        // Configurar mocks
        when(pedidoService.findById(1L)).thenReturn(pedido1);
        when(pedidoService.save(any(Pedido.class))).thenReturn(pedido1);
        
        // Crear un pedido con datos actualizados
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setEstado("ENTREGADO");
        pedidoActualizado.setFechaPedido(new Date());
        pedidoActualizado.setTotal(51.98);
        pedidoActualizado.setDireccionEnvio("Nueva dirección 789");
        pedidoActualizado.setUsuario(usuario);
        pedidoActualizado.setDetalles(detallesPedido);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.update(pedidoActualizado, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("pedido"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(pedidoService).findById(1L);
        verify(pedidoService).save(any(Pedido.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(pedidoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.update(pedido1, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(pedidoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_Success() {
        // Configurar mocks
        when(pedidoService.findById(1L)).thenReturn(pedido1);
        doNothing().when(pedidoService).delete(pedido1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(pedidoService).findById(1L);
        verify(pedidoService).delete(pedido1);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(pedidoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = pedidoController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(pedidoService).findById(99L);
    }
} 