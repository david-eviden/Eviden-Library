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
	
	public List<Libro> searchLibros (String query){
		return libroDao.findByTituloContainingIgnoreCaseOrAutores_NombreContainingIgnoreCaseOrGenero_NombreContainingIgnoreCase(query,query,query);
	}
	
	public List<Autor> searchAutores (String query){
		return autorDao.findByNombreContainingOrLibros_TituloContainingOrLibros_Generos_NombreContaining(query, query, query);
	}
	
	public List<Genero> searchGeneros(String query){
		return generoDao.findByNombreContainingIgnoreCaseOrLibros_TituloContainingIgnoreCaseOrLibros_Autores_NombreContainingIgnoreCase(query, query, query);
	}
}