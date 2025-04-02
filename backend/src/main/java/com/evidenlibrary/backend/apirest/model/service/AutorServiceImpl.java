package com.evidenlibrary.backend.apirest.model.service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.evidenlibrary.backend.apirest.model.dao.AutorDao;
import com.evidenlibrary.backend.apirest.model.dao.LibroDao;
import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Libro;

@Service
public class AutorServiceImpl implements AutorService {
	@Autowired
	private AutorDao autorDao;
	@Autowired
	private LibroDao libroDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Autor> findAll() {
		return (List<Autor>) autorDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Autor findById(Long id) {
		return autorDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Autor findByLibroId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	@Transactional
	public Autor save(Autor autor) {
		return autorDao.save(autor);
	}
	
	@Override
	@Transactional
	public void delete(Autor autor) {
	    // Eliminar el autor de los libros asociados
	    for (Libro libro : autor.getLibros()) {
	        libro.getAutores().remove(autor); // Eliminar el autor de la lista de autores del libro
	        libroDao.save(libro);  // Guardar el libro con la relación actualizada
	    }
	    // Luego, eliminar el autor de la base de datos
	    autorDao.delete(autor);
	}
	
	@Override
    @Transactional
    public void deleteAll() {
        // Obtener todos los autores
        List<Autor> autores = findAll();
        
        // Para cada autor, desasociar todos sus libros
        for (Autor autor : autores) {
            // Obtener una nueva colección para evitar ConcurrentModificationException
            Set<Libro> librosDelAutor = new HashSet<>(autor.getLibros());
            
            // Eliminar la asociación entre el autor y cada libro
            for (Libro libro : librosDelAutor) {
                libro.getAutores().remove(autor);
                libroDao.save(libro); // Guardar el libro con la relación actualizada
            }
        }
        
        // Una vez que todas las asociaciones han sido eliminadas, eliminar todos los autores
        autorDao.deleteAll();
    }
}