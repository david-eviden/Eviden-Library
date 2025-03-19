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
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.evidenlibrary.backend.apirest.model.entity.Carrito;
import com.evidenlibrary.backend.apirest.model.entity.DetalleCarrito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.CarritoService;
import com.evidenlibrary.backend.apirest.model.service.DetalleCarritoService;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

class DetalleCarritoControllerTest {

    @Mock
    private DetalleCarritoService detalleCarritoService;
    
    @Mock
    private CarritoService carritoService;
    
    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private DetalleCarritoController detalleCarritoController;

    private DetalleCarrito detalleCarrito1;
    private DetalleCarrito detalleCarrito2;
    private List<DetalleCarrito> detallesCarrito;
    private Usuario usuario;
    private Carrito carrito;
    private Libro libro;

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
        
        // Crear carrito de prueba
        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setFechaCreacion(new Date());
        carrito.setEstado("activo");
        
        // Configurar datos de prueba para detalles de carrito
        detalleCarrito1 = new DetalleCarrito();
        detalleCarrito1.setId(1L);
        detalleCarrito1.setCarrito(carrito);
        detalleCarrito1.setLibro(libro);
        detalleCarrito1.setCantidad(2);
        
        Libro libro2 = new Libro();
        libro2.setTitulo("Cien años de soledad");
        
        detalleCarrito2 = new DetalleCarrito();
        detalleCarrito2.setId(2L);
        detalleCarrito2.setCarrito(carrito);
        detalleCarrito2.setLibro(libro2);
        detalleCarrito2.setCantidad(1);
        
        detallesCarrito = new ArrayList<>();
        detallesCarrito.add(detalleCarrito1);
        detallesCarrito.add(detalleCarrito2);
    }

    @Test
    void testIndex() {
        // Configurar mock
        when(detalleCarritoService.findAll()).thenReturn(detallesCarrito);
        
        // Ejecutar método a probar
        List<DetalleCarrito> resultado = detalleCarritoController.index();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(2, resultado.get(0).getCantidad());
        assertEquals(1, resultado.get(1).getCantidad());
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoService).findAll();
    }

    @SuppressWarnings("null")
    @Test
    void testShow_Success() {
        // Configurar mock
        when(detalleCarritoService.findById(1L)).thenReturn(detalleCarrito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.show(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof DetalleCarrito);
        assertEquals(1L, ((DetalleCarrito) respuesta.getBody()).getId());
        assertEquals(2, ((DetalleCarrito) respuesta.getBody()).getCantidad());
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoService).findById(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testShow_NotFound() {
        // Configurar mock
        when(detalleCarritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.show(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByCarritoId_Success() {
        // Configurar mock
        when(detalleCarritoService.findByCarritoId(1L)).thenReturn(detallesCarrito);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.findByCarritoId(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<DetalleCarrito> body = (List<DetalleCarrito>) respuesta.getBody();
        assertEquals(2, body.size());
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoService).findByCarritoId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testCreate_Success() {
        // Configurar mocks
        when(detalleCarritoService.save(any(DetalleCarrito.class))).thenReturn(detalleCarrito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.create(detalleCarrito1);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("detalleCarrito"));
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoService).save(any(DetalleCarrito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_Success() {
        // Configurar mocks
        when(detalleCarritoService.findById(1L)).thenReturn(detalleCarrito1);
        when(detalleCarritoService.save(any(DetalleCarrito.class))).thenReturn(detalleCarrito1);
        
        // Crear un detalle con datos actualizados
        DetalleCarrito detalleActualizado = new DetalleCarrito();
        detalleActualizado.setLibro(libro);
        detalleActualizado.setCarrito(carrito);
        detalleActualizado.setCantidad(3); // Incrementar cantidad
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.update(detalleActualizado, 1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("detalleCarrito"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(detalleCarritoService).findById(1L);
        verify(detalleCarritoService).save(any(DetalleCarrito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_NotFound() {
        // Configurar mocks
        when(detalleCarritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.update(detalleCarrito1, 99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_Success() {
        // Configurar mocks
        when(detalleCarritoService.findById(1L)).thenReturn(detalleCarrito1);
        doNothing().when(detalleCarritoService).delete(detalleCarrito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.delete(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(detalleCarritoService).findById(1L);
        verify(detalleCarritoService).delete(detalleCarrito1);
    }
    
    @SuppressWarnings("null")
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(detalleCarritoService.findById(99L)).thenReturn(null);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.delete(99L);
        
        // Verificar resultado
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        
        // Verificar que se llamó al método correcto
        verify(detalleCarritoService).findById(99L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testAddToCart_NewCartItem() {
        // Configurar mocks
        when(usuarioService.findById(1L)).thenReturn(usuario);
        
        List<Carrito> carritos = new ArrayList<>();
        carritos.add(carrito);
        when(carritoService.findByUsuarioId(1L)).thenReturn(carritos);
        
        List<DetalleCarrito> detallesExistentes = new ArrayList<>();
        when(detalleCarritoService.findByCarritoId(1L)).thenReturn(detallesExistentes);
        
        when(detalleCarritoService.save(any(DetalleCarrito.class))).thenReturn(detalleCarrito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.addToCart(1L, detalleCarrito1);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("detalleCarrito"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findById(1L);
        verify(carritoService).findByUsuarioId(1L);
        verify(detalleCarritoService).findByCarritoId(1L);
        verify(detalleCarritoService).save(any(DetalleCarrito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testAddToCart_UpdateExistingItem() {
        // Configurar mocks
        when(usuarioService.findById(1L)).thenReturn(usuario);
        
        List<Carrito> carritos = new ArrayList<>();
        carritos.add(carrito);
        when(carritoService.findByUsuarioId(1L)).thenReturn(carritos);
        
        List<DetalleCarrito> detallesExistentes = new ArrayList<>();
        detallesExistentes.add(detalleCarrito1);
        when(detalleCarritoService.findByCarritoId(1L)).thenReturn(detallesExistentes);
        
        // Simular que se actualiza la cantidad
        DetalleCarrito detalleActualizado = new DetalleCarrito();
        detalleActualizado.setId(1L);
        detalleActualizado.setCarrito(carrito);
        detalleActualizado.setLibro(libro);
        detalleActualizado.setCantidad(3); // 2 original + 1 nuevo
        
        when(detalleCarritoService.save(any(DetalleCarrito.class))).thenReturn(detalleActualizado);
        
        // Crear un nuevo detalle con el mismo libro para actualizar
        DetalleCarrito nuevoDetalle = new DetalleCarrito();
        nuevoDetalle.setLibro(libro);
        nuevoDetalle.setCantidad(1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.addToCart(1L, nuevoDetalle);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("detalleCarrito"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findById(1L);
        verify(carritoService).findByUsuarioId(1L);
        verify(detalleCarritoService).findByCarritoId(1L);
        verify(detalleCarritoService).save(any(DetalleCarrito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testAddToCart_CreateNewCart() {
        // Configurar mocks
        when(usuarioService.findById(1L)).thenReturn(usuario);
        
        // Lista vacía de carritos para que cree uno nuevo
        List<Carrito> carritoVacio = new ArrayList<>();
        when(carritoService.findByUsuarioId(1L)).thenReturn(carritoVacio);
        
        // Simular que se crea un nuevo carrito
        when(carritoService.save(any(Carrito.class))).thenReturn(carrito);
        
        List<DetalleCarrito> detallesExistentes = new ArrayList<>();
        when(detalleCarritoService.findByCarritoId(anyLong())).thenReturn(detallesExistentes);
        
        when(detalleCarritoService.save(any(DetalleCarrito.class))).thenReturn(detalleCarrito1);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.addToCart(1L, detalleCarrito1);
        
        // Verificar resultado
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) respuesta.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertTrue(body.containsKey("detalleCarrito"));
        
        // Verificar que se llamaron a los métodos correctos
        verify(usuarioService).findById(1L);
        verify(carritoService).findByUsuarioId(1L);
        verify(carritoService).save(any(Carrito.class));
        verify(detalleCarritoService).findByCarritoId(anyLong());
        verify(detalleCarritoService).save(any(DetalleCarrito.class));
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByUsuarioIdAndCarritoActivo_Success() {
        // Configurar mocks
        List<Carrito> carritos = new ArrayList<>();
        carritos.add(carrito);
        when(carritoService.findByUsuarioId(1L)).thenReturn(carritos);
        
        when(detalleCarritoService.findByCarritoId(1L)).thenReturn(detallesCarrito);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.findByUsuarioIdAndCarritoActivo(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<DetalleCarrito> body = (List<DetalleCarrito>) respuesta.getBody();
        assertEquals(2, body.size());
        
        // Verificar que se llamaron a los métodos correctos
        verify(carritoService).findByUsuarioId(1L);
        verify(detalleCarritoService).findByCarritoId(1L);
    }
    
    @SuppressWarnings("null")
    @Test
    void testFindByUsuarioIdAndCarritoActivo_NoActiveCart() {
        // Configurar mocks
        // Lista vacía de carritos o carrito no activo
        List<Carrito> carritos = new ArrayList<>();
        Carrito carritoNoActivo = new Carrito();
        carritoNoActivo.setId(2L);
        carritoNoActivo.setEstado("finalizado");
        carritos.add(carritoNoActivo);
        
        when(carritoService.findByUsuarioId(1L)).thenReturn(carritos);
        
        // Ejecutar método a probar
        ResponseEntity<?> respuesta = detalleCarritoController.findByUsuarioIdAndCarritoActivo(1L);
        
        // Verificar resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<DetalleCarrito> body = (List<DetalleCarrito>) respuesta.getBody();
        assertEquals(0, body.size()); // No hay elementos porque no hay carrito activo
        
        // Verificar que se llamó al método correcto
        verify(carritoService).findByUsuarioId(1L);
    }
} 