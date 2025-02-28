package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroDao extends JpaRepository<Libro, Long> {
	public List<Libro> findByTituloContainingIgnoreCaseOrAutores_NombreContainingIgnoreCaseOrGeneros_NombreContainingIgnoreCase(
            String titulo, String autor, String genero);


	Optional<Libro> findByTitulo(String titulo);
}

