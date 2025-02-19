package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.DetallePedidoDao;
import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;

public class DetallePedidoServiceImpl implements DetallePedidoService {
	
	@Autowired
	private DetallePedidoDao detallePedidoDao;

	@Override
	@Transactional(readOnly = true)
	public List<DetallePedido> findAll() {
		return (List<DetallePedido>) detallePedidoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public DetallePedido findById(Long id) {
		return detallePedidoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public DetallePedido save(DetallePedido detalles) {
		return detallePedidoDao.save(detalles);
	}

	@Override
	@Transactional
	public void delete(DetallePedido detalles) {
		detallePedidoDao.delete(detalles);

	}

}
