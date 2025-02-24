package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.DetalleCarrito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface DetalleCarritoService {

	public List<DetalleCarrito> findAll();
	
	public DetalleCarrito findById(Long id);
	
	public List<Libro> findLibrosByCarritoId(Long id);
	
	public DetalleCarrito save(DetalleCarrito detalleCarrito);

	
	public void delete(DetalleCarrito detalleCarrito);

	public List<DetalleCarrito> findByCarritoId(Long carritoId);
}
