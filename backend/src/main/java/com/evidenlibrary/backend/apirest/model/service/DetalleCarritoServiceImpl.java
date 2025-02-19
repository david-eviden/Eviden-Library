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
		return (List<DetalleCarrito>) detalleCarritoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public DetalleCarrito findById(Long id) {
		return detalleCarritoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Libro> findLibrosByCarritoId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public DetalleCarrito save(DetalleCarrito detalles) {
		return detalleCarritoDao.save(detalles);
	}

	@Override
	@Transactional
	public void delete(DetalleCarrito detalles) {
		detalleCarritoDao.delete(detalles);

	}

}
