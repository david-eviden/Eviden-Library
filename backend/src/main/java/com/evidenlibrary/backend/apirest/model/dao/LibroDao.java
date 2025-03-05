package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroDao extends JpaRepository<Libro, Long> {
	public List<Libro> findByTituloContainingIgnoreCaseOrAutores_NombreContainingIgnoreCaseOrGeneros_NombreContainingIgnoreCase(
            String titulo, String autor, String genero);
	
	@Query("SELECT l FROM Libro l ORDER BY l.valoracionMedia DESC")
    List<Libro> findTop10MejorValorados();


	Optional<Libro> findByTitulo(String titulo);

	List<Libro> findByTituloContainingIgnoreCase(String titulo);
}

