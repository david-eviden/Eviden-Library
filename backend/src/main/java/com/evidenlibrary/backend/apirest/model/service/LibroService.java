package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroService {

	public List<Libro> findAll();
	
	public Page<Libro> findAllPaginado(Pageable pageable); // Paginación
	
	public Page<Libro> findByAutorIdPaginado(Long autorId, Pageable pageable); //Paginacion con autor
	
	public List<Libro> getMejorValorados();//Mejor valorados
	
	public Libro findById(Long id);
	
	//public Libro findByTitulo(String titulo);
	
	public List<Libro> findLibrosByAutorId(Long id);
	
	public List<Libro> findLibrosByFavoritoId(Long id);
	
	public List<Libro> findLibrosByCarritoId(Long id);
	
	public Libro save(Libro libro);
	
	public void delete(Libro libro);
	
	public void deleteAll();

	Libro findByTitulo(String titulo);

	
}
