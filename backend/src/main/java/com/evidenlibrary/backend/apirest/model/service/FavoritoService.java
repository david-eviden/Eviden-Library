package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import com.evidenlibrary.backend.apirest.model.entity.Favorito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

public interface FavoritoService {

	public List<Favorito> findAll();
	
	public Favorito findById(Long id);
	
	public List<Libro> findLibrosByFavoritos(Long id);
	
	public Favorito save(Favorito detalles);
	
	public void deleteLibroByFavorito(Long id);
	
	public void delete(Favorito detalles);

	public List<Favorito> findByUsuarioId(Long usuarioId);
}
