package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.UsuarioDao;
import com.evidenlibrary.backend.apirest.model.dao.ValoracionDao;
import com.evidenlibrary.backend.apirest.model.entity.Carrito;
import com.evidenlibrary.backend.apirest.model.entity.Favorito;
import com.evidenlibrary.backend.apirest.model.entity.Pedido;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.entity.Valoracion;
import com.evidenlibrary.backend.apirest.model.dao.CarritoDao;
import com.evidenlibrary.backend.apirest.model.dao.FavoritoDao;
import com.evidenlibrary.backend.apirest.model.dao.PedidoDao;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioDao usuarioDao;
	@Autowired
	private CarritoDao carritoDao;
	@Autowired
	private FavoritoDao favoritoDao;
	@Autowired
	private ValoracionDao valoracionDao;
	@Autowired
	private PedidoDao pedidoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Usuario> findAll() {
		return (List<Usuario>) usuarioDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Usuario findById(Long id) {
		return usuarioDao.findById(id).orElse(null);
	}
	
    @Override
    @Transactional(readOnly = true)
    public Usuario findByEmail(String email) {
        return usuarioDao.findByEmail(email);
    }

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> findUsuarioByPedidoId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Usuario save(Usuario usuario) {
		return usuarioDao.save(usuario);
	}

	@Override
	@Transactional
	public void delete(Usuario usuario) {
		//Eliminar los libros del carrito del usuario
		for(Carrito carrito : usuario.getCarritos()) {
			carritoDao.delete(carrito);
		}
		// Eliminar los libros favoritos del usuario
	    for (Favorito favorito : usuario.getFavoritos()) {
	        favoritoDao.delete(favorito); 
	    }

	    // Eliminar las valoraciones del usuario
	    for (Valoracion valoracion : usuario.getValoraciones()) {
	        valoracionDao.delete(valoracion); // 
	    }
	    
	    // Eliminar los pedidos del usuario
	    for (Pedido pedido : usuario.getPedidos()) {
	        pedidoDao.delete(pedido); // 
	    }
	    usuarioDao.delete(usuario);
	}

}
