package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evidenlibrary.backend.apirest.model.entity.Autor;

public interface AutorDao extends JpaRepository<Autor, Long> {
	
    //Busqueda
	List<Autor> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT DISTINCT a FROM Autor a " +
            "WHERE " +
            "(:term IS NULL OR :term = '') OR " +
            "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(a.biografia) LIKE LOWER(CONCAT('%', :term, '%'))")
     List<Autor> findByTerm(@Param("term") String term);
}
