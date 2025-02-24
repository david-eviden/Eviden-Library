package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.DetalleCarritoDao;
import com.evidenlibrary.backend.apirest.model.entity.DetalleCarrito;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@Service
public class DetalleCarritoServiceImpl implements DetalleCarritoService {
	
	@Autowired
	private DetalleCarritoDao detalleCarritoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<DetalleCarrito> findAll() {
		return detalleCarritoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public DetalleCarrito findById(Long id) {
		return detalleCarritoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public DetalleCarrito save(DetalleCarrito detalleCarrito) {
		return detalleCarritoDao.save(detalleCarrito);
	}

	@Override
	@Transactional
	public void delete(DetalleCarrito detalleCarrito) {
		detalleCarritoDao.delete(detalleCarrito);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DetalleCarrito> findByCarritoId(Long carritoId) {
		return detalleCarritoDao.findByCarritoId(carritoId);
	}

	@Override
	public List<Libro> findLibrosByCarritoId(Long id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'findLibrosByCarritoId'");
	}

}
