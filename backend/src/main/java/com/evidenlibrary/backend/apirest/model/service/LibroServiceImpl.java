package com.evidenlibrary.backend.apirest.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@Service
public class LibroServiceImpl implements LibroService {

	@Autowired
	private LibroDao libroDao;
	
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
    public List<Libro> getMejorValorados() {
        return libroDao.findTop10MejorValorados();
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
	
	//guardar portada
	public Libro guardarPortada(Long libroId, MultipartFile file) {
		try {
			// Verificar que el archivo no esté vacío
	        if (file.isEmpty()) {
	            throw new IllegalArgumentException("El archivo no puede estar vacío.");
	        }

	        // Verificar el tamaño del archivo (limite de 5MB)
	        if (file.getSize() > 5 * 1024 * 1024) {
	            throw new IllegalArgumentException("El archivo excede el tamaño permitido (5MB).");
	        }

	        // Obtener el libro desde la base de datos
	        Libro libro = libroDao.findById(libroId).orElseThrow(() -> new RuntimeException("Libro no encontrado"));

	        // Convertir el archivo en un arreglo de bytes
	        byte[] portadaBytes = file.getBytes();
	        
	        // Obtener el tipo de imagen 
	        String tipoImagen = file.getContentType();

	        libro.setPortada(portadaBytes);
	        libro.setTipoImagen(tipoImagen);

	        return libroDao.save(libro);
	    } catch (IOException e) {	        
	        throw new RuntimeException("Error al procesar la portada del libro", e);
	    }catch (IllegalArgumentException e) {
	        throw new IllegalArgumentException("Error en el archivo: " + e.getMessage());
	    }
    }

	@Override
	@Transactional
	public void delete(Libro libro) {
		libroDao.delete(libro);

	}

	@Override
	@Transactional
	public void deleteAll() {
		libroDao.deleteAll();

	}

}
