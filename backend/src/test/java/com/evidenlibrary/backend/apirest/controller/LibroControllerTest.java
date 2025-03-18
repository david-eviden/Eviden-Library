package com.evidenlibrary.backend.apirest.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.service.AutorService;
import com.evidenlibrary.backend.apirest.model.service.GeneroService;
import com.evidenlibrary.backend.apirest.model.service.LibroService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;

@WebMvcTest(LibroController.class)
class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private LibroService libroService;
    
    @MockBean
    private AutorService autorService;
    
    @MockBean
    private GeneroService generoService;
    
    @MockBean
    private EntityManager entityManager;
    
    private Libro libro;
    private Autor autor;
    private Genero genero;
    private List<Libro> listaLibros;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Autor Test");
        autor.setBiografia("Biografía de prueba");
        
        genero = new Genero();
        genero.setId(1L);
        genero.setNombre("Género Test");
        genero.setDescripcion("Descripción de prueba");
        
        libro = new Libro();
        libro.setTitulo("Libro Test");
        libro.setPrecio(29.99);
        libro.setStock(10);
        libro.setDescripcion("Descripción de prueba");
        libro.setImagen("imagen-test.jpg");
        libro.setAnio("2023");
        libro.getAutores().add(autor);
        libro.getGeneros().add(genero);
        
        Libro libro2 = new Libro();
        libro2.setTitulo("Libro Test 2");
        libro2.setPrecio(19.99);
        libro2.setStock(5);
        libro2.setDescripcion("Descripción de prueba 2");
        libro2.setImagen("imagen-test-2.jpg");
        libro2.setAnio("2022");
        
        listaLibros = Arrays.asList(libro, libro2);
    }
    
    @Test
    void testGetAllLibros() throws Exception {
        // Configurar mock
        when(libroService.findAll()).thenReturn(listaLibros);
        
        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(get("/api/libros")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Libro Test"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].titulo").value("Libro Test 2"));
    }
    
    @Test
    void testGetLibroById() throws Exception {
        // Configurar mock
        when(libroService.obtenerLibroConValoracionMedia(1L)).thenReturn(libro);
        when(libroService.findById(1L)).thenReturn(libro);
        
        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(get("/api/libro/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Libro Test"))
                .andExpect(jsonPath("$.precio").value(29.99))
                .andExpect(jsonPath("$.stock").value(10));
    }
    
    @Test
    void testGetLibrosPaginados() throws Exception {
        // Configurar paginación
        PageRequest pageRequest = PageRequest.of(0, 6);
        Page<Libro> paginaLibros = new PageImpl<>(listaLibros, pageRequest, listaLibros.size());
        
        // Configurar mock
        when(libroService.findAllPaginado(any())).thenReturn(paginaLibros);
        
        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(get("/api/libros/page/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Libro Test"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].titulo").value("Libro Test 2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
    
    @Test
    void testCreateLibro() throws Exception {
        // Configurar mock
        when(libroService.save(any(Libro.class))).thenReturn(libro);
        when(autorService.findById(anyLong())).thenReturn(autor);
        when(generoService.findById(anyLong())).thenReturn(genero);
        
        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(post("/api/libro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("El libro ha sido creado con éxito"))
                .andExpect(jsonPath("$.libro.id").value(1))
                .andExpect(jsonPath("$.libro.titulo").value("Libro Test"));
    }
    
    @Test
    void testUpdateLibro() throws Exception {
        // Crear libro actualizado
        Libro libroActualizado = new Libro();
        libroActualizado.setTitulo("Libro Actualizado");
        libroActualizado.setPrecio(39.99);
        libroActualizado.setStock(20);
        libroActualizado.setDescripcion("Descripción actualizada");
        libroActualizado.getAutores().add(autor);
        libroActualizado.getGeneros().add(genero);
        
        // Configurar mock
        when(libroService.findById(1L)).thenReturn(libro);
        when(libroService.save(any(Libro.class))).thenReturn(libroActualizado);
        when(autorService.findById(anyLong())).thenReturn(autor);
        when(generoService.findById(anyLong())).thenReturn(genero);
        
        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(put("/api/libro/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroActualizado)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.libro.id").value(1))
                .andExpect(jsonPath("$.libro.titulo").value("Libro Actualizado"))
                .andExpect(jsonPath("$.libro.precio").value(39.99))
                .andExpect(jsonPath("$.libro.stock").value(20));
    }
    
    @Test
    void testGetMejorValorados() throws Exception {
        // Configurar mock
        when(libroService.getMejorValorados()).thenReturn(listaLibros);
        
        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(get("/api/libros/mejor-valorados")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Libro Test"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].titulo").value("Libro Test 2"));
    }
    
    @Test
    void testGetLibrosByAutorId() throws Exception {
        // Configurar paginación
        PageRequest pageRequest = PageRequest.of(0, 6);
        Page<Libro> paginaLibros = new PageImpl<>(listaLibros, pageRequest, listaLibros.size());
        
        // Configurar mock
        when(libroService.findByAutorIdPaginado(anyLong(), any())).thenReturn(paginaLibros);
        
        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(get("/api/libros/autor/1/page/0/size/6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Libro Test"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].titulo").value("Libro Test 2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
} 