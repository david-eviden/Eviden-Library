package com.evidenlibrary.backend.apirest.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		List<Libro> libros = (List<Libro>) libroDao.findAll();
        libros.forEach(libro -> {
            if (libro.getPortada() == null && libro.getRutaImagen() != null) {
                try {
                    convertirImagenABytes(libro);
                    libroDao.save(libro); // Guardar los cambios
                } catch (IOException e) {
                    System.err.println("Error al convertir imagen a bytes para libro " + libro.getId() + ": " + e.getMessage());
                }
            }
        });
        return libros;
	}
	
    @Override
    @Transactional(readOnly = true)
    public Page<Libro> findAllPaginado(Pageable pageable) {
        return libroDao.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Libro> getMejorValorados() {
        return libroDao.findTop10MejorValorados();
    }

	@Override
	@Transactional(readOnly = true)
	public Libro findById(Long id) {
		Libro libro = libroDao.findById(id).orElse(null);
        if (libro != null) {
            // Verificar si se necesita convertir la imagen a bytes
            if (libro.getPortada() == null && libro.getRutaImagen() != null) {
                try {
                    convertirImagenABytes(libro);
                    libroDao.save(libro); // Guardar los cambios
                } catch (IOException e) {
                    System.err.println("Error al convertir imagen a bytes para libro " + libro.getId() + ": " + e.getMessage());
                }
            }
        }
        return libro;
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
	
	// Método privado para convertir la imagen a bytes
    private void convertirImagenABytes(Libro libro) throws IOException {
        Path imagePath = Paths.get(libro.getRutaImagen());
        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            libro.setPortada(imageBytes); // Asignar los bytes de la imagen a la propiedad portada
            libro.setTipoImagen(determinarTipoImagen(imagePath.toString())); // Determinar el tipo de imagen
        }
    }

    // Método privado para determinar el tipo de la imagen
    private String determinarTipoImagen(String path) {
        if (path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (path.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }

}
