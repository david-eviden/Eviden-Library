package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.DetallePedidoDao;
import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;

@Service
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
	@Transactional(readOnly = true)
	public List<DetallePedido> findByLibroId(Long libroId) {
	    return detallePedidoDao.findByLibroId(libroId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DetallePedido> findByPedidoId(Long pedidoId) {
		return detallePedidoDao.findByPedidoId(pedidoId);
	}

	@Override
	@Transactional
	public DetallePedido save(DetallePedido detallePedido) {
		return detallePedidoDao.save(detallePedido);
	}

	@Override
	@Transactional
	public void delete(DetallePedido detallePedido) {
		detallePedidoDao.delete(detallePedido);
	}

    @Override
    @Transactional
    public void deleteAll() {
        detallePedidoDao.deleteAll();
    }
}
