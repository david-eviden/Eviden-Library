package com.evidenlibrary.backend.apirest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.persistence.EntityManager;

import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.service.AutorService;
import com.evidenlibrary.backend.apirest.model.service.GeneroService;
import com.evidenlibrary.backend.apirest.model.service.LibroService;

@CrossOrigin(origins = { "http://localhost:4200" }, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}, allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class LibroController {

	@Autowired
	private LibroService libroService;
	
	@Autowired
	private AutorService autorService;
	
	@Autowired
	private GeneroService generoService;

	@Autowired
	private EntityManager entityManager;

	// Obtener libros
	@GetMapping("/libros")
	public List<Libro> index() {
		return libroService.findAll();
	}
	
    // Obtener libros (paginado)
    @GetMapping("/libros/page/{page}")
    public Page<Libro> index(@PathVariable(name = "page") Integer page) {
        return libroService.findAllPaginado(PageRequest.of(page, 6));
    }
    
    // Obtener libros (paginado con tamaño personalizado)
    @GetMapping("/libros/page/{page}/size/{size}")
    public Page<Libro> index(@PathVariable(name = "page") Integer page, @PathVariable(name = "size") Integer size) {
        return libroService.findAllPaginado(PageRequest.of(page, size));
    }
    
    // Obtener libros de un autor específico (paginado con tamaño personalizado)
    @GetMapping("/libros/autor/{autorId}/page/{page}/size/{size}")
    public Page<Libro> getLibrosPorAutor(
            @PathVariable(name = "autorId") Long autorId,
            @PathVariable(name = "page") Integer page, 
            @PathVariable(name = "size") Integer size) {
        return libroService.findByAutorIdPaginado(autorId, PageRequest.of(page, size));
    }
    
    // Obtener libros de un genero específico (paginado con tamaño personalizado)
    @GetMapping("/libros/genero/{generoId}/page/{page}/size/{size}")
    public Page<Libro> getLibrosPorGenero(
            @PathVariable(name = "generoId") Long generoId,
            @PathVariable(name = "page") Integer page, 
            @PathVariable(name = "size") Integer size) {
        return libroService.findByGeneroIdPaginado(generoId, PageRequest.of(page, size));
    }
    
    // Obtener libros de un genero y autor específico (paginado con tamaño personalizado)
    @GetMapping("/libros/autor/{autorId}/genero/{generoId}/page/{page}/size/{size}")
    public Page<Libro> getLibrosPorGeneroYAutor(
    		@PathVariable(name = "autorId") Long autorId,
            @PathVariable(name = "generoId") Long generoId,            
            @PathVariable(name = "page") Integer page, 
            @PathVariable(name = "size") Integer size) {
        return libroService.findByGeneroIdAndAutorIdPaginado(generoId, autorId, PageRequest.of(page, size));
    }
    
    //Obtener mejor valorados
    @GetMapping("/libros/mejor-valorados")
    public List<Libro> getMejorValorados() {
        return libroService.getMejorValorados();
    }
    

	// Obtener libros por ID
	@GetMapping("/libro/{id}")
	public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {

		Libro libro = new Libro();
		Map<String, Object> response = new HashMap<>();

		try {
			//calcula la media cada vez que carga
			libroService.obtenerLibroConValoracionMedia(id);
			libro = libroService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (libro == null) {
			response.put("mensaje", "El libro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(libro, HttpStatus.OK);
	}

	// Crear libro
	@PostMapping("/libro")
	public ResponseEntity<?> create(@RequestBody Libro libro, BindingResult result) {

		Libro nuevoLibro;
		Map<String, Object> response = new HashMap<>();

		// Validamos campos
		if (result.hasErrors()) {
			List<String> errores = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("errores", errores);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// Procesamos autores y géneros
		if (libro.getAutores() != null && !libro.getAutores().isEmpty()) {
			for (Autor autor : libro.getAutores()) {
				if (autor.getId() != null) {
					Autor autorExistente = autorService.findById(autor.getId());
					if (autorExistente != null) {
						libro.getAutores().remove(autor);
						libro.getAutores().add(autorExistente);
					}
				}
			}
		}
		
		if (libro.getGeneros() != null && !libro.getGeneros().isEmpty()) {
			for (Genero genero : libro.getGeneros()) {
				if (genero.getId() != null) {
					Genero generoExistente = generoService.findById(genero.getId());
					if (generoExistente != null) {
						libro.getGeneros().remove(genero);
						libro.getGeneros().add(generoExistente);
					}
				}
			}
		}

		// Manejamos errores
		try {
			nuevoLibro = libroService.save(libro);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al insertar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El libro ha sido creado con éxito");
		response.put("libro", nuevoLibro);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Actualizar libro
	@PutMapping("/libro/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@RequestBody Libro libro, BindingResult result, @PathVariable(name = "id") Long id) {

	    Libro currentLibro = this.libroService.findById(id);
	    Libro nuevoLibro;
	    Map<String, Object> response = new HashMap<>();

	    // Validamos campos
	    if (result.hasErrors()) {
	        List<String> errores = result.getFieldErrors().stream()
	                .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
	                .collect(Collectors.toList());

	        response.put("errores", errores);
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }

	    if (currentLibro == null) {
	        response.put("mensaje", "No se pudo editar, el libro con ID: "
	                .concat(id.toString().concat(" no existe en la base de datos")));
	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    }

	    try {
	        // Verificamos si el campo es nulo antes de actualizarlo
	        if (libro.getPrecio() != null) {
	            currentLibro.setPrecio(libro.getPrecio());
	        }
	        if (libro.getStock() != null) {
	            currentLibro.setStock(libro.getStock());
	        }
	        if (libro.getTitulo() != null && !libro.getTitulo().isEmpty()) {
	            currentLibro.setTitulo(libro.getTitulo());
	        }
	        if (libro.getDescripcion() != null && !libro.getDescripcion().isEmpty()) {
	            currentLibro.setDescripcion(libro.getDescripcion());
	        }
	        if (libro.getImagen() != null) {
	            currentLibro.setImagen(libro.getImagen());
	        }
	        
	        // Procesamos autores
	        if (libro.getAutores() != null && !libro.getAutores().isEmpty()) {
	            // Limpiamos los autores actuales
	            currentLibro.getAutores().clear();
	            
	            // Añadimos los nuevos autores
	            for (Autor autor : libro.getAutores()) {
	                if (autor.getId() != null) {
	                    Autor autorExistente = autorService.findById(autor.getId());
	                    if (autorExistente != null) {
	                        currentLibro.getAutores().add(autorExistente);
	                    }
	                }
	            }
	        }
	        
	        // Procesamos géneros
	        if (libro.getGeneros() != null && !libro.getGeneros().isEmpty()) {
	            // Limpiamos los géneros actuales
	            currentLibro.getGeneros().clear();
	            
	            // Añadimos los nuevos géneros
	            for (Genero genero : libro.getGeneros()) {
	                if (genero.getId() != null) {
	                    Genero generoExistente = generoService.findById(genero.getId());
	                    if (generoExistente != null) {
	                        currentLibro.getGeneros().add(generoExistente);
	                    }
	                }
	            }
	        }
	        
	        // Guardamos el libro actualizado
	        nuevoLibro = libroService.save(currentLibro);
	    } catch (DataAccessException e) {
	        response.put("mensaje", "Error al actualizar el libro en la base de datos");
	        response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    response.put("mensaje", "El libro ha sido actualizado con éxito");
	    response.put("libro", nuevoLibro);
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Eliminar libro por ID
	@DeleteMapping("/libro/{id}")
	public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
		Libro currentLibro = this.libroService.findById(id);
		Map<String, Object> response = new HashMap<>();

		// Validación de que exista el libro
		if (currentLibro == null) {
			response.put("mensaje", "El libro con ID: " + id + " no existe en la base de datos");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		try {
			// Verificar si el libro tiene pedidos asociados
			Long count = (Long) entityManager.createQuery(
				"SELECT COUNT(dp) FROM DetallePedido dp WHERE dp.libro.id = :libroId")
				.setParameter("libroId", id)
				.getSingleResult();
				
			if (count > 0) {
				response.put("mensaje", "No se puede eliminar el libro porque está asociado a uno o más pedidos");
				response.put("error", "El libro tiene pedidos asociados. Elimine primero los pedidos o implemente una eliminación lógica.");
				return new ResponseEntity<>(response, HttpStatus.CONFLICT);
			}
			
			libroService.delete(currentLibro);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el libro en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El libro ha sido eliminado con éxito");
		response.put("libro", currentLibro);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// Eliminar todos los libros
	@DeleteMapping("/libros")
	public ResponseEntity<?> deleteAll() {
		Map<String, Object> response = new HashMap<>();

		try {
		    libroService.deleteAll();
		} catch(DataAccessException e) {
		    response.put("mensaje", "Error al eliminar los libros en la base de datos");
		    response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
		    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Todos los libros han sido eliminados con éxito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
