package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroService {

	public List<Libro> findAll();
	
	public Libro findById(Long id);
	
	public Libro findByTitulo(String titulo);
	
	public List<Libro> findLibrosByAutorId(Long id);
	
	public List<Libro> findLibrosByFavoritoId(Long id);
	
	public List<Libro> findLibrosByCarritoId(Long id);
	
	public Libro save(Libro libro);
	
	public void delete(Libro libro);
	
	public void deleteAll();
}
