package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evidenlibrary.backend.apirest.model.entity.Genero;

public interface GeneroDao extends JpaRepository<Genero, Long> {
	
	//Busqueda
	
	List<Genero> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT DISTINCT g FROM Genero g " +
            "WHERE " +
            "(:term IS NULL OR :term = '') OR " +
            "LOWER(g.nombre) LIKE LOWER(CONCAT('%', :term, '%'))")
     List<Genero> findByTerm(@Param("term") String term);
}
