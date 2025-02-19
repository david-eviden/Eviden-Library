package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.GeneroDao;
import com.evidenlibrary.backend.apirest.model.entity.Genero;

public class GeneroServiceImpl implements GeneroService {
	
	@Autowired
	private GeneroDao generoDao;

	@Override
	@Transactional(readOnly = true)
	public List<Genero> findAll() {
		return (List<Genero>) generoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Genero findById(Long id) {
		return generoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Genero> findGenerosByLibroId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Genero> findGenerosByAutorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Genero save(Genero genero) {
		return generoDao.save(genero);
	}

	@Override
	@Transactional
	public void delete(Genero genero) {
		generoDao.delete(genero);

	}

}
