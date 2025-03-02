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
	
	//guardar con imagen
	public Libro saveWithImage(Libro libro, MultipartFile file) throws IOException {
        libro.setPortada(file.getBytes());
        libro.setTipoImagen(file.getContentType());
        return libroDao.save(libro);
    }
	
	//obtenerPortada
	public byte[] obtenerPortadaPorId(Long id) {
        Libro libro = libroDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));
        
        return libroDao.getPortadaBytes();
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
