package com.evidenlibrary.backend.apirest.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Autor;

public interface AutorDao extends JpaRepository<Autor, Long> {

}
