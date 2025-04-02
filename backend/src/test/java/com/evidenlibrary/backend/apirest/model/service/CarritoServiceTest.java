package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.evidenlibrary.backend.apirest.model.entity.Carrito;
import com.evidenlibrary.backend.apirest.model.entity.DetalleCarrito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoDao carritoDao;
    
    @InjectMocks
    private CarritoServiceImpl carritoService;
    
    private Carrito carrito1;
    private Carrito carrito2;
    private List<Carrito> listaCarritos;
    private List<Carrito> carritosUsuario1;
    private Usuario usuario1;
    private Usuario usuario2;
    private Libro libro1;
    private DetalleCarrito detalle1;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombre("Usuario1");
        usuario1.setEmail("usuario1@example.com");
        
        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("Usuario2");
        usuario2.setEmail("usuario2@example.com");
        
        libro1 = new Libro();
        libro1.setTitulo("Libro 1");
        libro1.setPrecio(29.99);
        libro1.setStock(10);
        
        // Crear carritos
        carrito1 = new Carrito();
        carrito1.setId(1L);
        carrito1.setUsuario(usuario1);
        carrito1.setFechaCreacion(new Date());
        carrito1.setEstado("activo");
        
        carrito2 = new Carrito();
        carrito2.setId(2L);
        carrito2.setUsuario(usuario2);
        carrito2.setFechaCreacion(new Date());
        carrito2.setEstado("activo");
        
        // Crear detalle de carrito
        detalle1 = new DetalleCarrito();
        detalle1.setId(1L);
        detalle1.setCarrito(carrito1);
        detalle1.setLibro(libro1);
        detalle1.setCantidad(2);
        
        carrito1.getDetalles().add(detalle1);
        
        listaCarritos = Arrays.asList(carrito1, carrito2);
        
        carritosUsuario1 = new ArrayList<>();
        carritosUsuario1.add(carrito1);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(carritoDao.findAll()).thenReturn(listaCarritos);
        
        // Ejecutar método a probar
        List<Carrito> resultado = carritoService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verificar que se llamó al método correcto
        verify(carritoDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(carritoDao.findById(1L)).thenReturn(Optional.of(carrito1));
        
        // Ejecutar método a probar
        Carrito resultado = carritoService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(usuario1, resultado.getUsuario());
        assertEquals(1, resultado.getDetalles().size());
        
        // Verificar que se llamó al método correcto
        verify(carritoDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(carritoDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        Carrito resultado = carritoService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(carritoDao).findById(99L);
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(carritoDao.save(carrito1)).thenReturn(carrito1);
        
        // Ejecutar método a probar
        Carrito resultado = carritoService.save(carrito1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(usuario1, resultado.getUsuario());
        
        // Verificar que se llamó al método correcto
        verify(carritoDao).save(carrito1);
    }
    
    @Test
    void testDelete() {
        // Ejecutar método a probar
        carritoService.delete(carrito1);
        
        // Verificar que se llamó al método correcto
        verify(carritoDao).delete(carrito1);
    }
    
    @Test
    void testFindByUsuarioId() {
        // Configurar mock
        when(carritoDao.findByUsuarioId(1L)).thenReturn(carritosUsuario1);
        
        // Ejecutar método a probar
        List<Carrito> resultado = carritoService.findByUsuarioId(1L);
        
        // Verificar resultado
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(usuario1, resultado.get(0).getUsuario());
        
        // Verificar que se llamó al método correcto
        verify(carritoDao).findByUsuarioId(1L);
    }
} 