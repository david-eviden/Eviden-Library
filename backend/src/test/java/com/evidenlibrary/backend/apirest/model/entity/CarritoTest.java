package com.evidenlibrary.backend.apirest.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarritoTest {
    
    private Carrito carrito;
    private Usuario usuario;
    private DetalleCarrito detalle1;
    private DetalleCarrito detalle2;
    private Libro libro1;
    private Libro libro2;
    
    @BeforeEach
    void setUp() {
        // Crear usuario para pruebas
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Usuario Prueba");
        usuario.setEmail("usuario@test.com");
        
        // Crear libros para pruebas
        libro1 = new Libro();
        libro1.setTitulo("Libro 1");
        libro1.setPrecio(29.99);
        libro1.setStock(10);
        
        libro2 = new Libro();
        libro2.setTitulo("Libro 2");
        libro2.setPrecio(19.99);
        libro2.setStock(5);
        
        // Crear carrito para pruebas
        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setFechaCreacion(new Date());
        carrito.setEstado("activo");
        
        // Crear detalles para pruebas
        detalle1 = new DetalleCarrito();
        detalle1.setId(1L);
        detalle1.setLibro(libro1);
        detalle1.setCantidad(2);
        detalle1.setPrecioUnitario(29.99);
        
        detalle2 = new DetalleCarrito();
        detalle2.setId(2L);
        detalle2.setLibro(libro2);
        detalle2.setCantidad(1);
        detalle2.setPrecioUnitario(19.99);
        
        // Añadir los detalles al carrito
        carrito.addDetalle(detalle1);
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, carrito.getId());
        assertEquals(usuario, carrito.getUsuario());
        assertNotNull(carrito.getFechaCreacion());
        assertEquals("activo", carrito.getEstado());
        
        // Modificar propiedades
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(2L);
        nuevoUsuario.setNombre("Nuevo Usuario");
        
        Date nuevaFecha = new Date();
        
        carrito.setUsuario(nuevoUsuario);
        carrito.setFechaCreacion(nuevaFecha);
        carrito.setEstado("inactivo");
        
        assertEquals(nuevoUsuario, carrito.getUsuario());
        assertEquals(nuevaFecha, carrito.getFechaCreacion());
        assertEquals("inactivo", carrito.getEstado());
    }
    
    @Test
    void testAddDetalle() {
        // Verificar que el detalle1 se añadió correctamente en setUp
        assertEquals(1, carrito.getDetalles().size());
        assertEquals(detalle1, carrito.getDetalles().get(0));
        assertEquals(carrito, detalle1.getCarrito());
        
        // Añadir otro detalle
        carrito.addDetalle(detalle2);
        
        // Verificar que se añadió correctamente
        assertEquals(2, carrito.getDetalles().size());
        assertEquals(detalle2, carrito.getDetalles().get(1));
        assertEquals(carrito, detalle2.getCarrito());
    }
    
    @Test
    void testRemoveDetalle() {
        // Añadir ambos detalles
        carrito.addDetalle(detalle2);
        assertEquals(2, carrito.getDetalles().size());
        
        // Remover un detalle
        carrito.removeDetalle(detalle1);
        
        // Verificar que se eliminó correctamente
        assertEquals(1, carrito.getDetalles().size());
        assertEquals(detalle2, carrito.getDetalles().get(0));
        assertNull(detalle1.getCarrito());
    }
    
    @Test
    void testSetDetalles() {
        // Crear una nueva lista de detalles
        List<DetalleCarrito> nuevosDetalles = new ArrayList<>();
        nuevosDetalles.add(detalle2);
        
        // Establecer los nuevos detalles
        carrito.setDetalles(nuevosDetalles);
        
        // Verificar que se establecieron correctamente
        assertEquals(1, carrito.getDetalles().size());
        assertEquals(detalle2, carrito.getDetalles().get(0));
        assertEquals(carrito, detalle2.getCarrito());
        
        // Asegurar que detalle1 ya no está asociado al carrito
        assertFalse(carrito.getDetalles().contains(detalle1));
    }
    
    @Test
    void testSetDetallesNull() {
        // Establecer detalles como null
        carrito.setDetalles(null);
        
        // Verificar que se manejó correctamente
        assertEquals(0, carrito.getDetalles().size());
    }
    
    @Test
    void testBuilder() {
        // Probar construir un carrito con constructor normal
        Date fecha = new Date();
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setId(2L);
        nuevoCarrito.setUsuario(usuario);
        nuevoCarrito.setFechaCreacion(fecha);
        nuevoCarrito.setEstado("pendiente");
        
        assertEquals(2L, nuevoCarrito.getId());
        assertEquals(usuario, nuevoCarrito.getUsuario());
        assertEquals(fecha, nuevoCarrito.getFechaCreacion());
        assertEquals("pendiente", nuevoCarrito.getEstado());
        assertTrue(nuevoCarrito.getDetalles().isEmpty());
    }
} 