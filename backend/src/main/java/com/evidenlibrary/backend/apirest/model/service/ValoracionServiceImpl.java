package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.ValoracionDao;
import com.evidenlibrary.backend.apirest.model.entity.Valoracion;

public class ValoracionServiceImpl implements ValoracionService {
	
	@Autowired
	private ValoracionDao valoracionDao;

	@Override
	@Transactional(readOnly = true)
	public List<Valoracion> findAll() {
		return (List<Valoracion>) valoracionDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Valoracion findById(Long id) {
		return valoracionDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Valoracion> findValoracionByLibroId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Valoracion> findValoracionByUsuarioId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Valoracion save(Valoracion valoracion) {
		return valoracionDao.save(valoracion);
	}

	@Override
	@Transactional
	public void delete(Valoracion valoracion) {
		valoracionDao.delete(valoracion);
	}

}
