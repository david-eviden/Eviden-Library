package com.evidenlibrary.backend.apirest.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Valoracion;

public interface ValoracionDao extends JpaRepository<Valoracion, Long> {

}
