package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.Carrito;

public interface CarritoService {

	public List<Carrito> findAll();
	
	public Carrito findById(Long id);
	
	public Carrito findCarritoByUsuarioId(Long id);
	
	public Carrito save(Carrito carrito);
	
	public void delete(Carrito carrito);
	
	public List<Carrito> findByUsuarioId(Long usuarioId);
}
