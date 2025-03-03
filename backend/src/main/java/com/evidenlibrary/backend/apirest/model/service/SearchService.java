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
public class SearchService {
    @Autowired
    private LibroDao libroDao;
    @Autowired
    private AutorDao autorDao;
    @Autowired
    private GeneroDao generoDao;
    
    public List<Libro> searchLibros(String query) {
        if (query.toLowerCase().startsWith("titulo:") || query.toLowerCase().startsWith("título:") || query.toLowerCase().startsWith("libro:")) {
            String cleanQuery = query.substring(query.indexOf(":") + 1).trim();
            return libroDao.findByTituloContainingIgnoreCase(cleanQuery);
        } else {
            return libroDao.findByTituloContainingIgnoreCaseOrAutores_NombreContainingIgnoreCaseOrGeneros_NombreContainingIgnoreCase(query, query, query);
        }
    }
    
    public List<Autor> searchAutores(String query) {
        if (query.toLowerCase().startsWith("autor:") || query.toLowerCase().startsWith("por:") || query.toLowerCase().startsWith("escrito por:") || query.toLowerCase().startsWith("de:")) {
            String cleanQuery = query.substring(query.indexOf(":") + 1).trim();
            return autorDao.findByNombreContainingIgnoreCase(cleanQuery);
        } else {
            return autorDao.findByNombreContainingOrLibros_TituloContainingOrLibros_Generos_NombreContaining(query, query, query);
        }
    }
    
    public List<Genero> searchGeneros(String query) {
        if (query.toLowerCase().startsWith("genero:") || query.toLowerCase().startsWith("género:") || query.toLowerCase().startsWith("categoria:") || query.toLowerCase().startsWith("categoría:")) {
            String cleanQuery = query.substring(query.indexOf(":") + 1).trim();
            return generoDao.findByNombreContainingIgnoreCase(cleanQuery);
        } else {
            return generoDao.findByNombreContainingIgnoreCaseOrLibros_TituloContainingIgnoreCaseOrLibros_Autores_NombreContainingIgnoreCase(query, query, query);
        }
    }
}