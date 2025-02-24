package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;

public interface DetallePedidoService {

	public List<DetallePedido> findAll();
	
	public DetallePedido findById(Long id);
	
	public DetallePedido save(DetallePedido detallePedido);
	
	public void delete(DetallePedido detallePedido);
	
	public List<DetallePedido> findByPedidoId(Long pedidoId);
	
}
