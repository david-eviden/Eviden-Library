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

import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.service.LibroService;

@CrossOrigin(origins = { "http://localhost:4200" }, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}, allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class LibroController {

	@Autowired
	private LibroService libroService;

	// Obtener libros
	@GetMapping("/libros")
	public List<Libro> index() {
		return libroService.findAll();
	}
	
    // Obtener libros (paginado)
    @GetMapping("/libros/page/{page}")
    public Page<Libro> index(@PathVariable Integer page) {
        return libroService.findAllPaginado(PageRequest.of(page, 6));
    }

	// Obtener libros por ID
	@GetMapping("/libro/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {

		Libro libro = new Libro();
		Map<String, Object> response = new HashMap<>();

		try {
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
	public ResponseEntity<?> update(@RequestBody Libro libro, BindingResult result, @PathVariable Long id) {

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
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Libro currentLibro = this.libroService.findById(id);
		Map<String, Object> response = new HashMap<>();

		// Validación de que exista el libro
		if (currentLibro == null) {
			response.put("mensaje", "El libro con ID: " + id + " no existe en la base de datos");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		try {
			libroService.delete(currentLibro);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el libro en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El libro ha sido eliminado con éxito");
		response.put("cliente", currentLibro);
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
