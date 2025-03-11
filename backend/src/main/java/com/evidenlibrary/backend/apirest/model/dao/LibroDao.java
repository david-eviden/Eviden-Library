package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroDao extends JpaRepository<Libro, Long> {
	
	Optional<Libro> findByTitulo(String titulo);

	//Ranking
	@Query("SELECT l FROM Libro l LEFT JOIN l.valoraciones v GROUP BY l.id ORDER BY AVG(v.puntuacion) DESC LIMIT 10")
    List<Libro> findTop10MejorValorados();
	
	//Filtrar por autor
	Page<Libro> findByAutoresId(Long autorId, Pageable pageable);
	
	//Busqueda	  
    @Query("SELECT DISTINCT l FROM Libro l " +
    	       "WHERE " +
    	       "(:term IS NULL OR :term = '') OR " +
    	       "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
    	       "LOWER(l.isbn) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
    	       "LOWER(l.anio_publicacion) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
    	       "LOWER(l.descripcion) LIKE LOWER(CONCAT('%', :term, '%'))")
    	List<Libro> findByTerm(@Param("term") String term);

	


}

