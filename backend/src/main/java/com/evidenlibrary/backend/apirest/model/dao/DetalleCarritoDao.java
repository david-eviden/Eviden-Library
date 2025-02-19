package com.evidenlibrary.backend.apirest.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.DetalleCarrito;

public interface DetalleCarritoDao extends JpaRepository<DetalleCarrito, Long> {

}
