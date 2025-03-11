package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.FavoritoDao;
import com.evidenlibrary.backend.apirest.model.entity.Favorito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@Service
public class FavoritoServiceImpl implements FavoritoService {

	@Autowired
	private FavoritoDao favoritoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Favorito> findAll() {
		return (List<Favorito>) favoritoDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Favorito findById(Long id) {
		return favoritoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Libro> findLibrosByFavoritos(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Favorito save(Favorito detalles) {
		return favoritoDao.save(detalles);
	}

	@Override
	@Transactional
	public void deleteLibroByFavorito(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void delete(Favorito detalles) {
		try {
			// Ensure the entity is managed
			Favorito managedFavorito = favoritoDao.findById(detalles.getId()).orElse(null);
			if (managedFavorito != null) {
				// Clear references
				managedFavorito.setUsuario(null);
				managedFavorito.setLibro(null);
				// Delete the entity
				favoritoDao.delete(managedFavorito);
				favoritoDao.flush(); // Force immediate deletion
			}
		} catch (Exception e) {
			// Log the error
			System.err.println("Error deleting favorito: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Favorito> findByUsuarioId(Long usuarioId) {
		return favoritoDao.findByUsuarioId(usuarioId);
	}

}
