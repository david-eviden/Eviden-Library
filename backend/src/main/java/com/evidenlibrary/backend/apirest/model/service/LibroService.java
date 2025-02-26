package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroService {

	public List<Libro> findAll();
	
	public Page<Libro> findAllPaginado(Pageable pageable); // Paginación
	
	public Libro findById(Long id);
	
	public List<Libro> findLibrosByAutorId(Long id);
	
	public List<Libro> findLibrosByFavoritoId(Long id);
	
	public List<Libro> findLibrosByCarritoId(Long id);
	
	public Libro save(Libro libro);
	
	public void delete(Libro libro);
	
	public void deleteAll();
}
