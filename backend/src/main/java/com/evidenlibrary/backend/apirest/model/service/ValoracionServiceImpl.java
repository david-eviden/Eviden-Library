package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.ValoracionDao;
import com.evidenlibrary.backend.apirest.model.entity.Valoracion;

@Service
public class ValoracionServiceImpl implements ValoracionService {
	
	@Autowired
	private ValoracionDao valoracionDao;

	@Override
	@Transactional(readOnly = true)
	public List<Valoracion> findAll() {
		return valoracionDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Valoracion findById(Long id) {
		return valoracionDao.findById(id).orElse(null);
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

	@Override
	@Transactional(readOnly = true)
	public List<Valoracion> findByLibroId(Long libroId) {
		return valoracionDao.findByLibroId(libroId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Valoracion> findByUsuarioId(Long usuarioId) {
		return valoracionDao.findByUsuarioId(usuarioId);
	}

}
