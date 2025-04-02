package com.evidenlibrary.backend.apirest.model.entity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PedidoTest {
    
    private Pedido pedido;
    private Usuario usuario;
    private DetallePedido detalle;
    private Libro libro;
    
    @BeforeEach
    void setUp() {
        // Crear un usuario para pruebas
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Usuario Prueba");
        usuario.setEmail("usuario@test.com");
        
        // Crear un libro para el detalle
        libro = new Libro();
        libro.setTitulo("Libro de prueba");
        libro.setPrecio(29.99);
        
        // Crear un pedido para pruebas
        pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(new Date());
        pedido.setTotal(29.99);
        pedido.setDireccionEnvio("Calle Prueba 123");
        pedido.setEstado("PENDIENTE");
        
        // Crear detalle de pedido
        detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setLibro(libro);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(29.99);
        
        // Añadir el detalle al pedido
        pedido.getDetalles().add(detalle);
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, pedido.getId());
        assertEquals(usuario, pedido.getUsuario());
        assertNotNull(pedido.getFechaPedido());
        assertEquals(29.99, pedido.getTotal());
        assertEquals("Calle Prueba 123", pedido.getDireccionEnvio());
        assertEquals("PENDIENTE", pedido.getEstado());
        
        // Modificar propiedades
        Date nuevaFecha = new Date();
        pedido.setFechaPedido(nuevaFecha);
        pedido.setTotal(39.99);
        pedido.setDireccionEnvio("Nueva Dirección");
        pedido.setEstado("ENVIADO");
        
        assertEquals(nuevaFecha, pedido.getFechaPedido());
        assertEquals(39.99, pedido.getTotal());
        assertEquals("Nueva Dirección", pedido.getDireccionEnvio());
        assertEquals("ENVIADO", pedido.getEstado());
    }
    
    @Test
    void testRelacionConDetalles() {
        // Verificar que el pedido tiene un detalle
        assertEquals(1, pedido.getDetalles().size());
        
        // Crear un nuevo detalle
        DetallePedido nuevoDetalle = new DetallePedido();
        nuevoDetalle.setPedido(pedido);
        nuevoDetalle.setLibro(libro);
        nuevoDetalle.setCantidad(2);
        nuevoDetalle.setPrecioUnitario(29.99);
        
        // Añadir el nuevo detalle
        pedido.getDetalles().add(nuevoDetalle);
        
        // Verificar que ahora tiene dos detalles
        assertEquals(2, pedido.getDetalles().size());
        assertTrue(pedido.getDetalles().contains(detalle));
        assertTrue(pedido.getDetalles().contains(nuevoDetalle));
    }
    
    @Test
    void testSetDetalles() {
        // Crear una lista nueva de detalles
        DetallePedido nuevoDetalle = new DetallePedido();
        nuevoDetalle.setPedido(pedido);
        nuevoDetalle.setLibro(libro);
        nuevoDetalle.setCantidad(3);
        nuevoDetalle.setPrecioUnitario(29.99);
        
        // Lista con solo el nuevo detalle
        java.util.List<DetallePedido> nuevosDetalles = new java.util.ArrayList<>();
        nuevosDetalles.add(nuevoDetalle);
        
        // Establecer la nueva lista
        pedido.setDetalles(nuevosDetalles);
        
        // Verificar que se reemplazó correctamente
        assertEquals(1, pedido.getDetalles().size());
        assertEquals(nuevoDetalle, pedido.getDetalles().get(0));
        assertEquals(3, pedido.getDetalles().get(0).getCantidad());
    }
    
    @Test
    void testBuilder() {
        // Probar construir un pedido
        Date fecha = new Date();
        Pedido pedidoBuilder = new Pedido();
        pedidoBuilder.setUsuario(usuario);
        pedidoBuilder.setFechaPedido(fecha);
        pedidoBuilder.setTotal(59.98);
        pedidoBuilder.setDireccionEnvio("Dirección Builder");
        pedidoBuilder.setEstado("ENTREGADO");
        
        assertEquals(2L, pedidoBuilder.getId());
        assertEquals(usuario, pedidoBuilder.getUsuario());
        assertEquals(fecha, pedidoBuilder.getFechaPedido());
        assertEquals(59.98, pedidoBuilder.getTotal());
        assertEquals("Dirección Builder", pedidoBuilder.getDireccionEnvio());
        assertEquals("ENTREGADO", pedidoBuilder.getEstado());
        assertEquals(0, pedidoBuilder.getDetalles().size());
    }
} 