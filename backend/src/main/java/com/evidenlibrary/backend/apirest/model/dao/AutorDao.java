package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Autor;

public interface AutorDao extends JpaRepository<Autor, Long> {
    List<Autor> findByNombreContainingOrLibros_TituloContainingOrLibros_Gneros_NombreCpntaining(
        String nombre, String titulo, String genero
    );
}
