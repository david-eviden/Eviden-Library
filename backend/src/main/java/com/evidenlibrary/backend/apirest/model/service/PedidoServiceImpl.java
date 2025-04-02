package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.PedidoDao;
import com.evidenlibrary.backend.apirest.model.entity.Pedido;

@Service
public class PedidoServiceImpl implements PedidoService {

	@Autowired
	private PedidoDao pedidoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Pedido> findAll() {
		return pedidoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Pedido findById(Long id) {
		return pedidoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pedido> findPedidosByUsuarioId(Long id) {
		return pedidoDao.findByUsuarioId(id);
	}

	@Override
	@Transactional
	public Pedido save(Pedido pedido) {
		return pedidoDao.save(pedido);
	}

	@Override
	@Transactional
	public void delete(Pedido pedido) {
		pedidoDao.delete(pedido);
	}

}
