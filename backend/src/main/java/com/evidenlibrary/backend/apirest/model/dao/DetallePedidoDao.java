package com.evidenlibrary.backend.apirest.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;

public interface DetallePedidoDao extends JpaRepository<DetallePedido, Long> {

}
