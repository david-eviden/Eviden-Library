package com.evidenlibrary.backend.apirest.model.entity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DetallePedidoTest {
    
    private DetallePedido detallePedido;
    private Pedido pedido;
    private Libro libro;
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        // Crear usuario para pruebas
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Usuario Prueba");
        usuario.setEmail("usuario@test.com");
        
        // Crear pedido para pruebas
        pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(new Date());
        pedido.setTotal(29.99);
        pedido.setDireccionEnvio("Calle Prueba 123");
        pedido.setEstado("PENDIENTE");
        
        // Crear libro para pruebas
        libro = new Libro();
        libro.setTitulo("Libro de prueba");
        libro.setPrecio(29.99);
        libro.setStock(10);
        
        // Crear detalle de pedido para pruebas
        detallePedido = new DetallePedido();
        detallePedido.setPedido(pedido);
        detallePedido.setLibro(libro);
        detallePedido.setCantidad(1);
        detallePedido.setPrecioUnitario(29.99);
        
        // Añadir el detalle al pedido
        pedido.getDetalles().add(detallePedido);
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, detallePedido.getId());
        assertEquals(pedido, detallePedido.getPedido());
        assertEquals(libro, detallePedido.getLibro());
        assertEquals(1, detallePedido.getCantidad());
        assertEquals(29.99, detallePedido.getPrecioUnitario());
        
        // Modificar propiedades
        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo("Otro libro");
        nuevoLibro.setPrecio(19.99);
        
        detallePedido.setLibro(nuevoLibro);
        detallePedido.setCantidad(2);
        detallePedido.setPrecioUnitario(19.99);
        
        assertEquals(nuevoLibro, detallePedido.getLibro());
        assertEquals(2, detallePedido.getCantidad());
        assertEquals(19.99, detallePedido.getPrecioUnitario());
    }
    
    @Test
    void testRelacionBidireccional() {
        // Verificar que el detalle está en la lista de detalles del pedido
        assertNotNull(detallePedido.getPedido());
        assertEquals(1, pedido.getDetalles().size());
        assertEquals(detallePedido, pedido.getDetalles().get(0));
        
        // Crear un nuevo pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setFechaPedido(new Date());
        nuevoPedido.setTotal(19.99);
        nuevoPedido.setDireccionEnvio("Nueva Dirección");
        nuevoPedido.setEstado("PROCESANDO");
        
        // Cambiar el pedido del detalle
        pedido.getDetalles().remove(detallePedido);
        detallePedido.setPedido(nuevoPedido);
        nuevoPedido.getDetalles().add(detallePedido);
        
        // Verificar que el detalle ya no está en el pedido antiguo
        assertEquals(0, pedido.getDetalles().size());
        
        // Verificar que el detalle está en el nuevo pedido
        assertEquals(1, nuevoPedido.getDetalles().size());
        assertEquals(detallePedido, nuevoPedido.getDetalles().get(0));
    }
    
    @Test
    void testBuilder() {
        // Probar construir un detalle con constructor normal
        DetallePedido nuevoDetalle = new DetallePedido();
        nuevoDetalle.setPedido(pedido);
        nuevoDetalle.setLibro(libro);
        nuevoDetalle.setCantidad(3);
        nuevoDetalle.setPrecioUnitario(27.99);
        
        assertEquals(2L, nuevoDetalle.getId());
        assertEquals(pedido, nuevoDetalle.getPedido());
        assertEquals(libro, nuevoDetalle.getLibro());
        assertEquals(3, nuevoDetalle.getCantidad());
        assertEquals(27.99, nuevoDetalle.getPrecioUnitario());
    }
} 