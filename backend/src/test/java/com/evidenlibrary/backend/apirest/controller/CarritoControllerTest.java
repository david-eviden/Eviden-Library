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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.evidenlibrary.backend.apirest.model.entity.Carrito;
import com.evidenlibrary.backend.apirest.model.entity.DetalleCarrito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.CarritoService;

class CarritoControllerTest {

    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private CarritoController carritoController;

    private Carrito carrito1;
    private Carrito carrito2;
    private List<Carrito> carritos;
    private Usuario usuario;
    private DetalleCarrito detalleCarrito;
    private List<DetalleCarrito> detallesCarrito;

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
        
        // Crear detalles de carrito
        detalleCarrito = new DetalleCarrito();
        detalleCarrito.setId(1L);
        
        Libro libro = new Libro();
        libro.setTitulo("El Quijote");
        
        detalleCarrito.setLibro(libro);
        detalleCarrito.setCantidad(2);
        
        detallesCarrito = new ArrayList<>();
        detallesCarrito.add(detalleCarrito);
        
        // Configurar datos de prueba para carritos
        carrito1 = new Carrito();
        carrito1.setId(1L);
        carrito1.setUsuario(usuario);
        carrito1.setFechaCreacion(new Date());
        carrito1.setEstado("ACTIVO");
        carrito1.setDetalles(detallesCarrito);
        
        carrito2 = new Carrito();
        carrito2.setId(2L);
        carrito2.setUsuario(usuario);
        carrito2.setFechaCreacion(new Date());
        carrito2.setEstado("FINALIZADO");
        
        DetalleCarrito detalleCarrito2 = new DetalleCarrito();
        detalleCarrito2.setId(2L);
        detalleCarrito2.setLibro(libro);
        detalleCarrito2.setCantidad(3);
        
        List<DetalleCarrito> detallesCarrito2 = new ArrayList<>();
        detallesCarrito2.add(detalleCarrito2);
        carrito2.setDetalles(detallesCarrito2);
        
        carritos = new ArrayList<>();
        carritos.add(carrito1);
        carritos.add(carrito2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(carritoService.findAll()).thenReturn(carritos);
        
        // Ejecutar método a probar
        List<Carrito> resultado = carritoController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals("ACTIVO", resultado.get(0).getEstado());
        assertEquals("FINALIZADO", resultado.get(1).getEstado());
        
        // Verificar que se llamó al método correcto
        verify(carritoService).findAll();
    }

    @SuppressWarnings("null")
    @Test
    void testShow_Success() {
        // Configurar mock
        when(carritoService.findById(1L)).thenReturn(carrito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Carrito);
        assertEquals(1L, ((Carrito) respuesta.getBody()).getId());
        assertEquals("ACTIVO", ((Carrito) respuesta.getBody()).getEstado());
        
        // Verificar que se llamó al método correcto
        verify(carritoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_NotFound() {
        // Configurar mock
        when(carritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(carritoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(carritoService.findById(1L)).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(carritoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByUsuarioId_Success() {
        // Configurar mock
        when(carritoService.findByUsuarioId(1L)).thenReturn(carritos);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.findByUsuarioId(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<Carrito> body = (List<Carrito>) respuesta.getBody();
        assertEquals(2, body.size());
        
        // Verificar que se llamó al método correcto
        verify(carritoService).findByUsuarioId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_Success() {
        // Configurar mocks
        when(carritoService.save(any(Carrito.class))).thenReturn(carrito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.create(carrito1);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("carrito"));
        
        // Verificar que se llamó al método correcto
        verify(carritoService).save(any(Carrito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_DatabaseError() {
        // Configurar mock para lanzar una excepción
        when(carritoService.save(any(Carrito.class))).thenThrow(new DataAccessException("Error de prueba") {
            private static final long serialVersionUID = 1L;
        });
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.create(carrito1);
        
        // Verificar resultado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("error"));
        
        // Verificar que se llamó al método correcto
        verify(carritoService).save(any(Carrito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_Success() {
        // Configurar mocks
        when(carritoService.findById(1L)).thenReturn(carrito1);
        when(carritoService.save(any(Carrito.class))).thenReturn(carrito1);
        
        // Crear un carrito con datos actualizados
        Carrito carritoActualizado = new Carrito();
        carritoActualizado.setEstado("PROCESANDO");
        carritoActualizado.setFechaCreacion(new Date());
        carritoActualizado.setUsuario(usuario);
        carritoActualizado.setDetalles(detallesCarrito);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.update(carritoActualizado, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("carrito"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(carritoService).findById(1L);
        verify(carritoService).save(any(Carrito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(carritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.update(carrito1, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(carritoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_Success() {
        // Configurar mocks
        when(carritoService.findById(1L)).thenReturn(carrito1);
        doNothing().when(carritoService).delete(carrito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(carritoService).findById(1L);
        verify(carritoService).delete(carrito1);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(carritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = carritoController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(carritoService).findById(99L);
    }
} 