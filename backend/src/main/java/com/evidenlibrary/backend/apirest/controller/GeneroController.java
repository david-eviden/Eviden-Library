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

import com.evidenlibrary.backend.apirest.model.entity.Genero;
import com.evidenlibrary.backend.apirest.model.service.GeneroService;



@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class GeneroController {

	@Autowired
	private GeneroService generoService;

	// Obtener generos
	@GetMapping("/generos")
	public List<Genero> index() {
		return generoService.findAll();
	}

	// Obtener generos por ID
	@GetMapping("/genero/{id}")
	public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {

		Genero genero;
		Map<String, Object> response = new HashMap<>();

		try {
			genero = generoService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (genero == null) {
			response.put("mensaje", "El genero con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(genero, HttpStatus.OK);
	}

	// Crear genero
	@PostMapping("/genero")
	public ResponseEntity<?> create(@RequestBody Genero genero, BindingResult result) {

		Genero nuevoGenero;
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
			nuevoGenero = generoService.save(genero);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al insertar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El genero ha sido creado con éxito");
		response.put("autor", nuevoGenero);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Actualizar genero
	@PutMapping("/genero/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@RequestBody Genero genero, BindingResult result, @PathVariable(name = "id") Long id) {

		Genero currentGenero = this.generoService.findById(id);
		Genero nuevoGenero;
		Map<String, Object> response = new HashMap<>();

		// Validamos campos
		if (result.hasErrors()) {

			List<String> errores = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("errores", errores);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		if (currentGenero == null) {
			response.put("mensaje", "No se puedo editar, el genero con ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentGenero.setDescripcion(genero.getDescripcion());
			currentGenero.setNombre(genero.getNombre());

			nuevoGenero = generoService.save(currentGenero);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el genero en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El genero ha sido actualizado con éxito");
		response.put("cliente", nuevoGenero);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Eliminar genero por ID
	@DeleteMapping("/generos/{id}")
	public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
		Genero currentGenero = this.generoService.findById(id);
		Map<String, Object> response = new HashMap<>();

		// Validación de que exista el genero
		if (currentGenero == null) {
			response.put("mensaje", "El genero con ID: " + id + " no existe en la base de datos");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		try {
			generoService.delete(currentGenero);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el genero en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El genero ha sido eliminado con éxito");
		response.put("cliente", currentGenero);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
