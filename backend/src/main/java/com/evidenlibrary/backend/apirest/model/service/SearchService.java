package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    public List<Libro> searchLibros(String query) {
        // Dividir la consulta en términos individuales para búsqueda más flexible
        List<String> terms = Arrays.asList(query.toLowerCase().split("\\s+"))
                                   .stream()
                                   .filter(term -> !term.isEmpty())
                                   .collect(Collectors.toList());
        
        if (terms.isEmpty()) {
            return List.of();
        }
        
        // Buscar libros que coincidan con cualquiera de los términos en título, autor o género
        return libroDao.findByMultipleTerms(terms);
    }
    
    public List<Autor> searchAutores(String query) {
        // Dividir la consulta en términos individuales
        List<String> terms = Arrays.asList(query.toLowerCase().split("\\s+"))
                                   .stream()
                                   .filter(term -> !term.isEmpty())
                                   .collect(Collectors.toList());
        
        if (terms.isEmpty()) {
            return List.of();
        }
        
        // Buscar autores que coincidan con cualquiera de los términos
        return autorDao.findByMultipleTerms(terms);
    }
    
    public List<Genero> searchGeneros(String query) {
        // Dividir la consulta en términos individuales
        List<String> terms = Arrays.asList(query.toLowerCase().split("\\s+"))
                                   .stream()
                                   .filter(term -> !term.isEmpty())
                                   .collect(Collectors.toList());
        
        if (terms.isEmpty()) {
            return List.of();
        }
        
        // Buscar géneros que coincidan con cualquiera de los términos
        return generoDao.findByMultipleTerms(terms);
    }
}