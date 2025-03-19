package com.evidenlibrary.backend.apirest.model.entity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DetalleCarritoTest {
    
    private DetalleCarrito detalleCarrito;
    private Carrito carrito;
    private Libro libro;
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        // Crear usuario para pruebas
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Usuario Prueba");
        usuario.setEmail("usuario@test.com");
        
        // Crear carrito para pruebas
        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setFechaCreacion(new Date());
        carrito.setEstado("activo");
        
        // Crear libro para pruebas
        libro = new Libro();
        libro.setTitulo("Libro de prueba");
        libro.setPrecio(29.99);
        libro.setStock(10);
        
        // Crear detalle de carrito para pruebas
        detalleCarrito = new DetalleCarrito();
        detalleCarrito.setId(1L);
        detalleCarrito.setCarrito(carrito);
        detalleCarrito.setLibro(libro);
        detalleCarrito.setCantidad(2);
        detalleCarrito.setPrecioUnitario(29.99);
        
        // Añadir el detalle al carrito
        carrito.getDetalles().add(detalleCarrito);
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, detalleCarrito.getId());
        assertEquals(carrito, detalleCarrito.getCarrito());
        assertEquals(libro, detalleCarrito.getLibro());
        assertEquals(2, detalleCarrito.getCantidad());
        assertEquals(29.99, detalleCarrito.getPrecioUnitario());
        
        // Modificar propiedades
        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo("Otro libro");
        nuevoLibro.setPrecio(19.99);
        
        detalleCarrito.setLibro(nuevoLibro);
        detalleCarrito.setCantidad(3);
        detalleCarrito.setPrecioUnitario(19.99);
        
        assertEquals(nuevoLibro, detalleCarrito.getLibro());
        assertEquals(3, detalleCarrito.getCantidad());
        assertEquals(19.99, detalleCarrito.getPrecioUnitario());
    }
    
    @Test
    void testRelacionBidireccional() {
        // Verificar que el detalle está en la lista de detalles del carrito
        assertNotNull(detalleCarrito.getCarrito());
        assertEquals(1, carrito.getDetalles().size());
        assertEquals(detalleCarrito, carrito.getDetalles().get(0));
        
        // Crear un nuevo carrito
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setId(2L);
        nuevoCarrito.setUsuario(usuario);
        nuevoCarrito.setFechaCreacion(new Date());
        nuevoCarrito.setEstado("activo");
        
        // Cambiar el carrito del detalle
        carrito.getDetalles().remove(detalleCarrito);
        detalleCarrito.setCarrito(nuevoCarrito);
        nuevoCarrito.getDetalles().add(detalleCarrito);
        
        // Verificar que el detalle ya no está en el carrito antiguo
        assertEquals(0, carrito.getDetalles().size());
        
        // Verificar que el detalle está en el nuevo carrito
        assertEquals(1, nuevoCarrito.getDetalles().size());
        assertEquals(detalleCarrito, nuevoCarrito.getDetalles().get(0));
    }
    
    @Test
    void testBuilder() {
        // Probar construir un detalle con constructor normal
        DetalleCarrito nuevoDetalle = new DetalleCarrito();
        nuevoDetalle.setId(2L);
        nuevoDetalle.setCarrito(carrito);
        nuevoDetalle.setLibro(libro);
        nuevoDetalle.setCantidad(5);
        nuevoDetalle.setPrecioUnitario(27.99);
        
        assertEquals(2L, nuevoDetalle.getId());
        assertEquals(carrito, nuevoDetalle.getCarrito());
        assertEquals(libro, nuevoDetalle.getLibro());
        assertEquals(5, nuevoDetalle.getCantidad());
        assertEquals(27.99, nuevoDetalle.getPrecioUnitario());
    }
} 