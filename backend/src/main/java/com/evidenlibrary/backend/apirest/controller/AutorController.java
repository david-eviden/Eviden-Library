package com.evidenlibrary.backend.apirest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.service.AutorService;


@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class AutorController {

	@Autowired
	private AutorService autorService;

	// Obtener autores
	@GetMapping("/autores")
	public List<Autor> index() {
		return autorService.findAll();
	}
	
	// Obtener autores por ID
	@GetMapping("/autor/{id}")
	public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {
		
		Autor autor;
		Map<String, Object> response = new HashMap<>();
		
		try {
			autor = autorService.findById(id);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(autor == null) {
			response.put("mensaje", "El autor con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(autor, HttpStatus.OK); 
	}

	// Crear autor
	@PostMapping("/autor")
	public ResponseEntity<?> create(@RequestBody Autor autor, BindingResult result) {
		
		Autor nuevoAutor;
		Map<String, Object> response = new HashMap<>();
		
		// Validamos campos
		if(result.hasErrors()) {
			List<String> errores = result.getFieldErrors()
				.stream()
				.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
				.collect(Collectors.toList());
			
			response.put("errores", errores);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		// Manejamos errores
		try {
			nuevoAutor = autorService.save(autor);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al insertar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El autor ha sido creado con éxito");
		response.put("autor", nuevoAutor);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Actualizar autor
	@PutMapping("/autor/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@RequestBody Autor autor, BindingResult result ,@PathVariable(name = "id") Long id) {
		
		Autor currentAutor = this.autorService.findById(id);
		Autor nuevoAutor;
		Map<String, Object> response = new HashMap<>();
		
		// Validamos campos
		if(result.hasErrors()) {
			
			List<String> errores = result.getFieldErrors()
				.stream()
				.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
				.collect(Collectors.toList());
			
			response.put("errores", errores);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(currentAutor == null) {
			response.put("mensaje", "No se puedo editar, el autor con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			currentAutor.setNombre(autor.getNombre());
			currentAutor.setBiografia(autor.getBiografia());
			
			nuevoAutor = autorService.save(currentAutor);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al actualizar el autor en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El autor ha sido actualizado con éxito");
		response.put("autor", nuevoAutor);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Eliminar autor por ID
	@DeleteMapping("/autor/{id}")
	public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
		Autor currentAutor = this.autorService.findById(id);
		Map<String, Object> response = new HashMap<>();

		// Validación de que exista el autor
		if (currentAutor == null) {
			response.put("mensaje", "El autor con ID: " + id + " no existe en la base de datos");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		try {
			autorService.delete(currentAutor);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el autor en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El autor ha sido eliminado con éxito");
		response.put("autor", currentAutor);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
		
	// Eliminar todos los autores
	@DeleteMapping("/autores")
	public ResponseEntity<?> deleteAll() {
		Map<String, Object> response = new HashMap<>();

		try {
		    autorService.deleteAll();
		} catch(DataAccessException e) {
		    response.put("mensaje", "Error al eliminar los autores en la base de datos");
		    response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
		    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Todos los autors han sido eliminados con éxito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
