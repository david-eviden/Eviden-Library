package com.evidenlibrary.backend.apirest.model.service;

import com.evidenlibrary.backend.apirest.model.entity.Carrito;

public interface CarritoService {

	//public List<Carrito> findAll();
	
	public Carrito findById(Long id);
	
	public Carrito findByUsuarioId(Long id);
	
	public Carrito save(Carrito carrito);
	
	public void deleteLibroByCarrito(Long id);
	
	//public void delete(Carrito carrito);
}
