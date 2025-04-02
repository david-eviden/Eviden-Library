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

import com.evidenlibrary.backend.apirest.model.dao.CarritoDao;
import com.evidenlibrary.backend.apirest.model.dao.FavoritoDao;
import com.evidenlibrary.backend.apirest.model.dao.PedidoDao;
import com.evidenlibrary.backend.apirest.model.dao.UsuarioDao;
import com.evidenlibrary.backend.apirest.model.dao.ValoracionDao;
import com.evidenlibrary.backend.apirest.model.entity.Carrito;
import com.evidenlibrary.backend.apirest.model.entity.Favorito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Pedido;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.entity.Valoracion;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioDao usuarioDao;
    
    @Mock
    private CarritoDao carritoDao;
    
    @Mock
    private FavoritoDao favoritoDao;
    
    @Mock
    private ValoracionDao valoracionDao;
    
    @Mock
    private PedidoDao pedidoDao;
    
    @InjectMocks
    private UsuarioServiceImpl usuarioService;
    
    private Usuario usuario1;
    private Usuario usuario2;
    private List<Usuario> listaUsuarios;
    private Carrito carrito;
    private Favorito favorito;
    private Valoracion valoracion;
    private Pedido pedido;
    private Libro libro;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombre("Juan");
        usuario1.setApellido("Pérez");
        usuario1.setEmail("juan.perez@example.com");
        usuario1.setPassword("password123");
        usuario1.setRol("ROLE_USER");
        
        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("Maria");
        usuario2.setApellido("Rodriguez");
        usuario2.setEmail("maria.rodriguez@example.com");
        usuario2.setPassword("password456");
        usuario2.setRol("ROLE_USER");
        
        listaUsuarios = new ArrayList<>();
        listaUsuarios.add(usuario1);
        listaUsuarios.add(usuario2);
        
        // Crear libro
        libro = new Libro();
        libro.setTitulo("Libro de Prueba");
        libro.setPrecio(29.99);
        libro.setStock(10);
        
        // Crear carrito
        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario1);
        usuario1.getCarritos().add(carrito);
        
        // Crear favorito
        favorito = new Favorito();
        favorito.setUsuario(usuario1);
        favorito.setLibro(libro);
        favorito.setFechaAgregado(new Date());
        usuario1.getFavoritos().add(favorito);
        
        // Crear valoración
        valoracion = new Valoracion();
        valoracion.setUsuario(usuario1);
        valoracion.setLibro(libro);
        valoracion.setPuntuacion(5);
        valoracion.setComentario("Excelente libro");
        usuario1.getValoraciones().add(valoracion);
        
        // Crear pedido
        pedido = new Pedido();
        pedido.setUsuario(usuario1);
        usuario1.getPedidos().add(pedido);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(usuarioDao.findAll()).thenReturn(listaUsuarios);
        
        // Ejecutar método a probar
        List<Usuario> resultado = usuarioService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Maria", resultado.get(1).getNombre());
        
        // Verificar que se llamó al método correcto
        verify(usuarioDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario1));
        
        // Ejecutar método a probar
        Usuario resultado = usuarioService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Pérez", resultado.getApellido());
        
        // Verificar que se llamó al método correcto
        verify(usuarioDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(usuarioDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        Usuario resultado = usuarioService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(usuarioDao).findById(99L);
    }
    
    @Test
    void testFindByEmail() {
        // Configurar mock
        when(usuarioDao.findByEmail("juan.perez@example.com")).thenReturn(usuario1);
        
        // Ejecutar método a probar
        Usuario resultado = usuarioService.findByEmail("juan.perez@example.com");
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("juan.perez@example.com", resultado.getEmail());
        
        // Verificar que se llamó al método correcto
        verify(usuarioDao).findByEmail("juan.perez@example.com");
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(usuarioDao.save(usuario1)).thenReturn(usuario1);
        
        // Ejecutar método a probar
        Usuario resultado = usuarioService.save(usuario1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan", resultado.getNombre());
        
        // Verificar que se llamó al método correcto
        verify(usuarioDao).save(usuario1);
    }
    
    @Test
    void testDelete() {
        // Ejecutar método a probar
        usuarioService.delete(usuario1);
        
        // Verificar que se llamaron a los métodos correctos para eliminar relaciones
        verify(carritoDao).delete(carrito);
        verify(favoritoDao).delete(favorito);
        verify(valoracionDao).delete(valoracion);
        verify(pedidoDao).delete(pedido);
        
        // Verificar que se eliminó el usuario
        verify(usuarioDao).delete(usuario1);
    }
} 