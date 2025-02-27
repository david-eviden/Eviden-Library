package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.Autor;

public interface AutorService {

	public List<Autor> findAll();
	
	public Autor findById(Long id);
	
	public Autor findByLibroId(Long id);
	
	public Autor save(Autor autor);
	
	public void delete(Autor autor);
	
	public void deleteAll();

}
