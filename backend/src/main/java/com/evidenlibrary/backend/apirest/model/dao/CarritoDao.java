package com.evidenlibrary.backend.apirest.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Carrito;

public interface CarritoDao extends JpaRepository<Carrito, Long> {

}
