package com.evidenlibrary.backend.apirest.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Favorito;

public interface FavoritoDao extends JpaRepository<Favorito, Long> {

}
