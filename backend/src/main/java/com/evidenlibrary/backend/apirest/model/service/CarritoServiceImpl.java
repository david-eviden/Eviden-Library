package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.CarritoDao;
import com.evidenlibrary.backend.apirest.model.entity.Carrito;

@Service
public class CarritoServiceImpl implements CarritoService {
 
	@Autowired
	private CarritoDao carritoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Carrito> findAll() {
		List<Carrito> carritos = carritoDao.findAll();
		carritos.forEach(carrito -> {
			System.out.println("Carrito ID: " + carrito.getId());
			if (carrito.getUsuario() != null) {
				System.out.println("Usuario: " + carrito.getUsuario().getNombre());
			}
			if (carrito.getDetalles() != null) {
				carrito.getDetalles().forEach(detalle -> {
					if (detalle.getLibro() != null) {
						System.out.println("Libro: " + detalle.getLibro().getTitulo());
					}
				});
			}
		});
		return carritos;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Carrito findById(Long id) {
		return carritoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Carrito findCarritoByUsuarioId(Long id) {
		//
		return null;
	}

	@Override
	@Transactional
	public Carrito save(Carrito carrito) {
		return carritoDao.save(carrito);
	}

	@Override
	@Transactional
	public void delete(Carrito carrito) {
		carritoDao.delete(carrito);
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<Carrito> findByUsuarioId(Long usuarioId) {
		return carritoDao.findByUsuarioId(usuarioId);
	}

}
