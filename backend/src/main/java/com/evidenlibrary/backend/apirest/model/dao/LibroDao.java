package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroDao extends JpaRepository<Libro, Long> {

	List<Libro> findByTituloContainingIgnoreCaseOuAutor_NombreContainingIgnoreCaseOrGenero_NombreContainingIgnoreCase(
		String titulo, String autorNombre, String generoNombre
	);

}
