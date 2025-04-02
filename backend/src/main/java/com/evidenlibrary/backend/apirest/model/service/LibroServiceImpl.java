package com.evidenlibrary.backend.apirest.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@Service
public class LibroServiceImpl implements LibroService {

	@Autowired
	private LibroDao libroDao;
	
	@Autowired
	private DetallePedidoService detallePedidoService;
	
	@Override
	@Transactional(readOnly = true)
	public List<Libro> findAll() {
		return (List<Libro>) libroDao.findAll();
	}
	
    @Override
    @Transactional(readOnly = true)
    public Page<Libro> findAllPaginado(Pageable pageable) {
        return libroDao.findAll(pageable);
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public Page<Libro> findByAutorIdPaginado(Long autorId, Pageable pageable) {
        return libroDao.findByAutoresId(autorId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Libro> findByGeneroIdPaginado(Long generoId, Pageable pageable) {
        return libroDao.findByGenerosId(generoId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Libro> findByGeneroIdAndAutorIdPaginado(Long generoId,Long autorId, Pageable pageable) {
        return libroDao.findByGenerosIdAndAutoresId(generoId, autorId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Libro> getMejorValorados() {
    	//Actualizo valoraciones medias
    	List<Libro> todosLibros = libroDao.findAll();
    	for (Libro libro : todosLibros) {
    		if(!libro.getValoraciones().isEmpty()) {
    			double suma = libro.getValoraciones().stream()
    							.mapToDouble(valoracion -> valoracion.getPuntuacion())
    							.sum();
    			libro.setValoracionMedia(suma / libro.getValoraciones().size());
    			libroDao.save(libro);
    		}
    	}
        return libroDao.findTop10MejorValorados();
    }
    
    // Método para obtener el libro y su valoración media
    @Override
    @Transactional
    public Libro obtenerLibroConValoracionMedia(Long libroId) {
        Libro libro = libroDao.findById(libroId).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        
        //Actualizo valoracion media
        if(libro.getValoraciones().isEmpty()) {
        	libro.setValoracionMedia(0.0);
        } else {
        	double suma = libro.getValoraciones().stream()
        					.mapToDouble(valoracion -> valoracion.getPuntuacion())
        					.sum();
        	libro.setValoracionMedia(suma / libro.getValoraciones().size());
        }
        
        return libroDao.save(libro);
    }

	@Override
	@Transactional(readOnly = true)
	public Libro findById(Long id) {
		return libroDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Libro findByTitulo(String titulo) {
		return libroDao.findByTitulo(titulo).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Libro> findLibrosByAutorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Libro> findLibrosByFavoritoId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Libro> findLibrosByCarritoId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Libro save(Libro libro) {
		return libroDao.save(libro);
	}

	@Override
	@Transactional
	public void delete(Libro libro) {
		// Obtener los detalles de pedido relacionados con este libro
        List<DetallePedido> detallesPedido = detallePedidoService.findByPedidoId(libro.getId());
        
		// Desasociar el libro de todos sus autores
        for (Autor autor : libro.getAutores()) {
            autor.getLibros().remove(libro);
        }
        
        libro.getAutores().clear();
        
        // Desasociar el libro de todos sus géneros
        for (Genero genero : libro.getGeneros()) {
            genero.getLibros().remove(libro);
        }
        
        libro.getGeneros().clear();
        
        // Desasociar de pedidos
        for (DetallePedido detalle : detallesPedido) {
            detallePedidoService.delete(detalle);
        }
        
        // Actualizar el libro con las asociaciones eliminadas
        libroDao.save(libro);
        
        // Eliminar el libro
        libroDao.delete(libro);
	}

	@Override
	@Transactional
	public void deleteAll() {
		// Obtener todos los libros
        List<Libro> libros = findAll();
        
        // Obtener los detalles de los pedidos
        List<DetallePedido> detallesPedido = detallePedidoService.findAll();
        
        // Para cada libro, desasociar todos sus autores y géneros
        for (Libro libro : libros) {
            // Desasociar el libro de todos sus autores
            for (Autor autor : libro.getAutores()) {
                autor.getLibros().remove(libro);
            }
            libro.getAutores().clear();
            
            // Desasociar el libro de todos sus géneros
            for (Genero genero : libro.getGeneros()) {
                genero.getLibros().remove(libro);
            }
            libro.getGeneros().clear();
        }
        
        // Desasociar de pedidos
        for (DetallePedido detalle : detallesPedido) {
            detallePedidoService.delete(detalle);
        }
        
        // Actualizar los libros con las asociaciones eliminadas
        for (Libro libro : libros) {
            libroDao.save(libro);
        }
        
        // Eliminar todos los libros
        libroDao.deleteAll();
	}

}
