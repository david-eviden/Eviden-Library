package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evidenlibrary.backend.apirest.model.entity.Autor;

public interface AutorDao extends JpaRepository<Autor, Long> {
	
    //Busqueda
	List<Autor> findByNombreContainingIgnoreCase(String nombre);
    
    List<Autor> findByNombreContainingOrLibros_TituloContainingOrLibros_Generos_NombreContaining(
            String nombre, String tituloLibro, String nombreGenero);
    
    // Nueva consulta para buscar con múltiples términos
    @Query("SELECT DISTINCT a FROM Autor a " +
           "LEFT JOIN a.libros l " +
           "LEFT JOIN l.generos g " +
           "WHERE " +
           "(:#{#terms.size()} = 0) OR " +
           "(EXISTS (SELECT 1 FROM :#{#terms} t WHERE LOWER(a.nombre) LIKE CONCAT('%', LOWER(t), '%'))) OR " +
           "(EXISTS (SELECT 1 FROM :#{#terms} t WHERE LOWER(a.apellido) LIKE CONCAT('%', LOWER(t), '%'))) OR " +
           "(EXISTS (SELECT 1 FROM :#{#terms} t WHERE LOWER(l.titulo) LIKE CONCAT('%', LOWER(t), '%'))) OR " +
           "(EXISTS (SELECT 1 FROM :#{#terms} t WHERE LOWER(g.nombre) LIKE CONCAT('%', LOWER(t), '%')))")
    List<Autor> findByMultipleTerms(@Param("terms") List<String> terms);
}
