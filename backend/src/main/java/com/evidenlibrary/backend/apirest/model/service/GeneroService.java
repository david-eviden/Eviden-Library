package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.Genero;

public interface GeneroService {
	
	public List<Genero> findAll();
	
	public Genero findById(Long id);
	
	public List<Genero> findGenerosByLibroId(Long id);
	
	public List<Genero> findGenerosByAutorId(Long id);
	
	public Genero save(Genero genero);
	
	public void delete(Genero genero);
	
    public void deleteAll();
}
