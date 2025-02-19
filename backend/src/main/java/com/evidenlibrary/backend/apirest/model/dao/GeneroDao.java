package com.evidenlibrary.backend.apirest.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Genero;

public interface GeneroDao extends JpaRepository<Genero, Long> {

}
