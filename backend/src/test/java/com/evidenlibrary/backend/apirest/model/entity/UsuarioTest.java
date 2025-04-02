package com.evidenlibrary.backend.apirest.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsuarioTest {
    
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        // Crear un usuario para pruebas
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password");
        usuario.setRol("ROLE_USER");
        usuario.setDireccion("Calle Test, 123");
        usuario.setFoto("foto-test.jpg");
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, usuario.getId());
        assertEquals("Test", usuario.getNombre());
        assertEquals("User", usuario.getApellido());
        assertEquals("test@example.com", usuario.getEmail());
        assertEquals("password", usuario.getPassword());
        assertEquals("ROLE_USER", usuario.getRol());
        assertEquals("Calle Test, 123", usuario.getDireccion());
        assertEquals("foto-test.jpg", usuario.getFoto());
        
        // Modificar propiedades
        usuario.setNombre("Otro");
        usuario.setApellido("Usuario");
        usuario.setEmail("otro@example.com");
        usuario.setPassword("newpassword");
        usuario.setRol("ROLE_ADMIN");
        usuario.setDireccion("Otra Calle, 456");
        usuario.setFoto("otra-foto.jpg");
        
        assertEquals("Otro", usuario.getNombre());
        assertEquals("Usuario", usuario.getApellido());
        assertEquals("otro@example.com", usuario.getEmail());
        assertEquals("newpassword", usuario.getPassword());
        assertEquals("ROLE_ADMIN", usuario.getRol());
        assertEquals("Otra Calle, 456", usuario.getDireccion());
        assertEquals("otra-foto.jpg", usuario.getFoto());
    }
    
    @Test
    void testColeccionesIniciales() {
        // Verificar que las colecciones se inicializan vacías
        assertTrue(usuario.getPedidos().isEmpty());
        assertTrue(usuario.getCarritos().isEmpty());
        assertTrue(usuario.getValoraciones().isEmpty());
        assertTrue(usuario.getFavoritos().isEmpty());
    }
    
    @Test
    void testAgregarPedido() {
        // Crear y añadir un pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        
        usuario.getPedidos().add(pedido);
        
        // Verificar que se añadió correctamente
        assertEquals(1, usuario.getPedidos().size());
        assertEquals(pedido, usuario.getPedidos().get(0));
    }
    
    @Test
    void testAgregarCarrito() {
        // Crear y añadir un carrito
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        
        usuario.getCarritos().add(carrito);
        
        // Verificar que se añadió correctamente
        assertEquals(1, usuario.getCarritos().size());
        assertEquals(carrito, usuario.getCarritos().get(0));
    }
    
    @Test
    void testAgregarValoracion() {
        // Crear y añadir una valoración
        Valoracion valoracion = new Valoracion();
        valoracion.setUsuario(usuario);
        valoracion.setPuntuacion(5);
        
        usuario.getValoraciones().add(valoracion);
        
        // Verificar que se añadió correctamente
        assertEquals(1, usuario.getValoraciones().size());
        assertEquals(valoracion, usuario.getValoraciones().get(0));
    }
    
    @Test
    void testAgregarFavorito() {
        // Crear y añadir un favorito
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        
        usuario.getFavoritos().add(favorito);
        
        // Verificar que se añadió correctamente
        assertEquals(1, usuario.getFavoritos().size());
        assertEquals(favorito, usuario.getFavoritos().get(0));
    }
    
    @Test
    void testBuilder() {
        // Crear un usuario
        Usuario usuarioNuevo = new Usuario();
        usuarioNuevo.setId(2L);
        usuarioNuevo.setNombre("Builder");
        usuarioNuevo.setApellido("Test");
        usuarioNuevo.setEmail("builder@example.com");
        usuarioNuevo.setPassword("builderpass");
        usuarioNuevo.setRol("ROLE_USER");
        usuarioNuevo.setDireccion("Dirección Builder");
        usuarioNuevo.setFoto("builder-foto.jpg");
        
        assertEquals(2L, usuarioNuevo.getId());
        assertEquals("Builder", usuarioNuevo.getNombre());
        assertEquals("Test", usuarioNuevo.getApellido());
        assertEquals("builder@example.com", usuarioNuevo.getEmail());
        assertEquals("builderpass", usuarioNuevo.getPassword());
        assertEquals("ROLE_USER", usuarioNuevo.getRol());
        assertEquals("Dirección Builder", usuarioNuevo.getDireccion());
        assertEquals("builder-foto.jpg", usuarioNuevo.getFoto());
    }
} 