package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.AutorDao;
import com.evidenlibrary.backend.apirest.model.dao.GeneroDao;
import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@Service
public class SearchService{
	@Autowired
    private LibroDao libroDao;
    @Autowired
    private AutorDao autorDao;
    @Autowired
    private GeneroDao generoDao;
    
    //Metodo dividir las consultas
    private List<String> parseQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        
        return Arrays.asList(query.toLowerCase().split("\\s+"))
                .stream()
                .filter(term -> !term.isEmpty())
                .collect(Collectors.toList());
    }
    
    public List<Libro> searchLibros(String query) {
    	// Dividir la consulta 
        List<String> terms = parseQuery(query);

        if (terms.isEmpty()) {
        	return List.of(); // Lista vacía si no hay términos
        }
       
        Set<Libro> allResults = new HashSet<>();
        
        for (String term : terms) { 
        	
        	//Busqueda por titulo
        	allResults.addAll(libroDao.findByTituloContainingIgnoreCase(term));
        	
        	//Busqueda por año
        	allResults.addAll(libroDao.findByAnio(term));
        	
            //allResults.addAll(libroDao.findByTerm(term));
        }
        
        return new ArrayList<>(allResults);
    }
    
    public List<Autor> searchAutores(String query) {
    	
        List<String> terms = parseQuery(query);
        
        if (terms.isEmpty()) {
            return List.of();
        }
        
        Set<Autor> allResults = new HashSet<>();
        
        // Buscar autores por cada término
        for (String term : terms) {
        	allResults.addAll(autorDao.findByNombreContainingIgnoreCase(term));
            //allResults.addAll(autorDao.findByTerm(term));
        }
        
        return new ArrayList<>(allResults);
    }
    
    public List<Genero> searchGeneros(String query) {
    	
        List<String> terms = parseQuery(query);
        
        if (terms.isEmpty()) {
            return List.of(); 
        }
        
        Set<Genero> allResults = new HashSet<>();
        
        // Buscar géneros por cada término
        for (String term : terms) {
        	allResults.addAll(generoDao.findByNombreContainingIgnoreCase(term));           
        	//allResults.addAll(generoDao.findByTerm(term));
        }
        
        return new ArrayList<>(allResults);
    }
    
    
    //Todos los libros asociados  a un autor
    @Transactional(readOnly = true)
    public List<Libro> findLibrosByAutorId(Long autorId) {
        // autor existe
        Autor autor = autorDao.findById(autorId)
            .orElseThrow(() -> new RuntimeException("Autor no encontrado con ID: " + autorId));
        
        // libros asociados a este autor
        return libroDao.findByAutoresContaining(autor);
    }
    
    //Todos los libros asociados a un género 
    @Transactional(readOnly = true)
    public List<Libro> findLibrosByGeneroId(Long generoId) {
        // género exista
        Genero genero = generoDao.findById(generoId)
            .orElseThrow(() -> new RuntimeException("Género no encontrado con ID: " + generoId));
        
        // libros asociados a este género
        return libroDao.findByGenerosContaining(genero);
    }
    
    //Busqueda por año de publicacion
    @Transactional(readOnly = true)
    public List<Libro> findLibrosByAnio(String anio) {
        return libroDao.findByAnio(anio);
    }
}