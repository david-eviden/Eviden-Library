package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.AutorDao;
import com.evidenlibrary.backend.apirest.model.entity.Autor;

public class AutorServiceImpl implements AutorService {

	@Autowired
	private AutorDao autorDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Autor> findAll() {
		return (List<Autor>) autorDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Autor findById(Long id) {
		return autorDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Autor findByLibroId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Autor save(Autor autor) {
		return autorDao.save(autor);
	}

	@Override
	@Transactional
	public void delete(Autor autor) {
		autorDao.delete(autor);

	}

}
