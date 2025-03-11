package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroDao extends JpaRepository<Libro, Long> {
	//Busqueda
	public List<Libro> findByTituloContainingIgnoreCaseOrAutores_NombreContainingIgnoreCaseOrGeneros_NombreContainingIgnoreCase(
            String titulo, String autor, String genero);
<<<<<<< HEAD
	//Ranking
	@Query("SELECT l FROM Libro l LEFT JOIN l.valoraciones v GROUP BY l.id ORDER BY AVG(v.puntuacion) DESC LIMIT 10")
=======
	
	@Query("SELECT l FROM Libro l ORDER BY l.valoracionMedia DESC LIMIT 10")
>>>>>>> desarrollo
    List<Libro> findTop10MejorValorados();


	Optional<Libro> findByTitulo(String titulo);

	List<Libro> findByTituloContainingIgnoreCase(String titulo);
	
	//Filtrar por autor
	Page<Libro> findByAutoresId(Long autorId, Pageable pageable);
}

