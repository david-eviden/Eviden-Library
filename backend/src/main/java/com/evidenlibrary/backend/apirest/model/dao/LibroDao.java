package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroDao extends JpaRepository<Libro, Long> {
	public List<Libro> findByTituloContainingIgnoreCaseOrAutores_NombreContainingIgnoreCaseOrGeneros_NombreContainingIgnoreCase(
            String titulo, String autor, String genero);
	
	@Query("SELECT l FROM Libro l LEFT JOIN l.valoraciones v GROUP BY l.id ORDER BY AVG(v.puntuacion) DESC LIMIT 10")
    List<Libro> findTop10MejorValorados();


	Optional<Libro> findByTitulo(String titulo);

	@Query("SELECT l.portada FROM Libro l WHERE l.id = :id")
    byte[] getPortadaBytes(@Param("id") Long id);
}

