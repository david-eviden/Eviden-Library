package com.evidenlibrary.backend.apirest.model.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evidenlibrary.backend.apirest.model.dao.GeneroDao;
import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@Service
public class GeneroServiceImpl implements GeneroService {
	
	@Autowired
	private GeneroDao generoDao;
	@Autowired
	private LibroDao libroDao;

	@Override
	@Transactional(readOnly = true)
	public List<Genero> findAll() {
		return (List<Genero>) generoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Genero findById(Long id) {
		return generoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Genero> findGenerosByLibroId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Genero> findGenerosByAutorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Genero save(Genero genero) {
		return generoDao.save(genero);
	}

	
	@Override
	@Transactional
	public void delete(Genero genero) {
		//Eliminar realciones en la tabla intermedia(libro_genero)
		for (Libro libro : genero.getLibros()) {
            libro.getGeneros().remove(genero); // Eliminar el género de los libros
            libroDao.save(libro); // Guardar el libro actualizado
        }
		// Eliminar el género
		generoDao.delete(genero);
	}
	
    @Override
    @Transactional
    public void deleteAll() {
        // Obtener todos los géneros
        List<Genero> generos = findAll();
        
        // Para cada género, desasociar todos sus libros
        for (Genero genero : generos) {
            // Obtener una nueva colección para evitar ConcurrentModificationException
            Set<Libro> librosDelGenero = new HashSet<>(genero.getLibros());
            
            // Eliminar la asociación entre el género y cada libro
            for (Libro libro : librosDelGenero) {
                libro.getGeneros().remove(genero);
                libroDao.save(libro); // Guardar el libro con la relación actualizada
            }
        }
        
        // Una vez que todas las asociaciones han sido eliminadas, eliminar todos los géneros
        generoDao.deleteAll();
    }

}
