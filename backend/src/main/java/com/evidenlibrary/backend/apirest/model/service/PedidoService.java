package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.Pedido;

public interface PedidoService {
	
	public List<Pedido> findAll();
	
	public Pedido findById(Long id);
	
	public List<Pedido> findPedidosByUsuarioId(Long usuarioId);
	
	public Pedido save(Pedido pedido);
	
	public void delete(Pedido pedido);
	
}
