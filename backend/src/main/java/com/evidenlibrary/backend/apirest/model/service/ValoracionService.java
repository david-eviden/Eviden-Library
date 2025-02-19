package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.Valoracion;

public interface ValoracionService {

	public List<Valoracion> findAll();
	
	public Valoracion findById(Long id);
	
	public List<Valoracion> findValoracionByLibroId(Long id);
	
	public List<Valoracion> findValoracionByUsuarioId(Long id);
	
	public Valoracion save(Valoracion valoracion);
	
	public void delete(Valoracion valoracion);
	
}
