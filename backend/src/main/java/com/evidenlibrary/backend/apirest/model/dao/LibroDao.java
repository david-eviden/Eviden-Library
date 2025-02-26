package com.evidenlibrary.backend.apirest.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface LibroDao extends JpaRepository<Libro, Long> {

	Optional<Libro> findByTitulo(String titulo);

}
