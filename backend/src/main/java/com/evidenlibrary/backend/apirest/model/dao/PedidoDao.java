package com.evidenlibrary.backend.apirest.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evidenlibrary.backend.apirest.model.entity.Pedido;

public interface PedidoDao extends JpaRepository<Pedido, Long> {

}
