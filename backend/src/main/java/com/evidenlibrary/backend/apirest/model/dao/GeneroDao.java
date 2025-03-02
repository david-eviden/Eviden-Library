package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Genero;

public interface GeneroDao extends JpaRepository<Genero, Long> {
	List<Genero> findByNombreContainingIgnoreCaseOrLibros_TituloContainingIgnoreCaseOrLibros_Autores_NombreContainingIgnoreCase(
            String nombre, String titulo, String autorNombre);
}
