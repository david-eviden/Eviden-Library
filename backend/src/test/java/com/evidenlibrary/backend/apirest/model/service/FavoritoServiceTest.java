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
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evidenlibrary.backend.apirest.model.dao.FavoritoDao;
import com.evidenlibrary.backend.apirest.model.entity.Favorito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;

@ExtendWith(MockitoExtension.class)
class FavoritoServiceTest {

    @Mock
    private FavoritoDao favoritoDao;
    
    @InjectMocks
    private FavoritoServiceImpl favoritoService;
    
    private Favorito favorito1;
    private Favorito favorito2;
    private List<Favorito> listaFavoritos;
    private List<Favorito> favoritosUsuario1;
    private Usuario usuario1;
    private Usuario usuario2;
    private Libro libro1;
    private Libro libro2;
    
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
        
        libro2 = new Libro();
        libro2.setTitulo("Libro 2");
        libro2.setPrecio(19.99);
        libro2.setStock(5);
        
        // Crear favoritos
        favorito1 = new Favorito();
        favorito1.setUsuario(usuario1);
        favorito1.setLibro(libro1);
        favorito1.setFechaAgregado(new Date());
        
        favorito2 = new Favorito();
        favorito2.setUsuario(usuario2);
        favorito2.setLibro(libro2);
        favorito2.setFechaAgregado(new Date());
        
        listaFavoritos = new ArrayList<>();
        listaFavoritos.add(favorito1);
        listaFavoritos.add(favorito2);
        
        favoritosUsuario1 = new ArrayList<>();
        favoritosUsuario1.add(favorito1);
    }
    
    @Test
    void testFindAll() {
        // Configurar mock
        when(favoritoDao.findAll()).thenReturn(listaFavoritos);
        
        // Ejecutar método a probar
        List<Favorito> resultado = favoritoService.findAll();
        
        // Verificar resultado
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verificar que se llamó al método correcto
        verify(favoritoDao).findAll();
    }
    
    @Test
    void testFindById() {
        // Configurar mock
        when(favoritoDao.findById(1L)).thenReturn(Optional.of(favorito1));
        
        // Ejecutar método a probar
        Favorito resultado = favoritoService.findById(1L);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(usuario1, resultado.getUsuario());
        assertEquals(libro1, resultado.getLibro());
        
        // Verificar que se llamó al método correcto
        verify(favoritoDao).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Configurar mock
        when(favoritoDao.findById(99L)).thenReturn(Optional.empty());
        
        // Ejecutar método a probar
        Favorito resultado = favoritoService.findById(99L);
        
        // Verificar resultado
        assertNull(resultado);
        
        // Verificar que se llamó al método correcto
        verify(favoritoDao).findById(99L);
    }
    
    @Test
    void testSave() {
        // Configurar mock
        when(favoritoDao.save(favorito1)).thenReturn(favorito1);
        
        // Ejecutar método a probar
        Favorito resultado = favoritoService.save(favorito1);
        
        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(usuario1, resultado.getUsuario());
        assertEquals(libro1, resultado.getLibro());
        
        // Verificar que se llamó al método correcto
        verify(favoritoDao).save(favorito1);
    }
    
    @Test
    void testDelete() {
        // Configurar mock
        when(favoritoDao.findById(1L)).thenReturn(Optional.of(favorito1));
        
        // Ejecutar método a probar
        favoritoService.delete(favorito1);
        
        // Verificar que se llamaron a los métodos correctos en el orden adecuado
        verify(favoritoDao).findById(1L);
        verify(favoritoDao).delete(favorito1);
        verify(favoritoDao).flush();
    }
    
    @Test
    void testDelete_NotFound() {
        // Configurar mock
        when(favoritoDao.findById(99L)).thenReturn(Optional.empty());
        
        Favorito favoritoInexistente = new Favorito();
        
        // Ejecutar método a probar
        favoritoService.delete(favoritoInexistente);
        
        // Verificar que se llamó findById pero no delete ni flush
        verify(favoritoDao).findById(99L);
        verify(favoritoDao, never()).delete(any(Favorito.class));
        verify(favoritoDao, never()).flush();
    }
    
    @Test
    void testFindByUsuarioId() {
        // Configurar mock
        when(favoritoDao.findByUsuarioId(1L)).thenReturn(favoritosUsuario1);
        
        // Ejecutar método a probar
        List<Favorito> resultado = favoritoService.findByUsuarioId(1L);
        
        // Verificar resultado
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(usuario1, resultado.get(0).getUsuario());
        
        // Verificar que se llamó al método correcto
        verify(favoritoDao).findByUsuarioId(1L);
    }
} 