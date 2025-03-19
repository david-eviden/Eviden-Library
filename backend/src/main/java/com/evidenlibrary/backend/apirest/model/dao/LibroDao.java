package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroDao extends JpaRepository<Libro, Long> {
	
	Optional<Libro> findByTitulo(String titulo);

	//Ranking
	@Query("SELECT l FROM Libro l LEFT JOIN l.valoraciones v GROUP BY l.id ORDER BY AVG(v.puntuacion) DESC LIMIT 10")
    List<Libro> findTop10MejorValorados();
	
	//Filtrar por autor y genero
	Page<Libro> findByAutoresId(Long autorId, Pageable pageable);
	Page<Libro> findByGenerosId(Long generoId, Pageable pageable);
	
	//Busqueda	
	List<Libro> findByTituloContainingIgnoreCase(String titulo);
	List<Libro> findByAnio(String anio);
	List<Libro> findByAutoresContaining(Autor autorId);    
    List<Libro> findByGenerosContaining(Genero generoId);
	
    @Query("SELECT DISTINCT l FROM Libro l " +
    	       "WHERE " +
    	       "(:term IS NULL OR :term = '') OR " +
    	       "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
    	       //"LOWER(l.anio_publicacion) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
    	       "LOWER(l.descripcion) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Libro> findByTerm(@Param("term") String term);

	@Query("SELECT l FROM Libro l JOIN l.generos g JOIN l.autores a WHERE g.id =:generoId AND a.id = :autorId")
	Page<Libro> findByGenerosIdAndAutoresId(@Param("generoId") Long generoId, @Param("autorId") Long autorId, Pageable pageable);


}

