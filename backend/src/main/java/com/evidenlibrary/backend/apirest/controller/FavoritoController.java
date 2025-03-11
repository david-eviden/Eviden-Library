package com.evidenlibrary.backend.apirest.controller;
 
import java.util.Date;
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

import com.evidenlibrary.backend.apirest.model.entity.Favorito;
import com.evidenlibrary.backend.apirest.model.service.FavoritoService;
 
 
@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class FavoritoController {
 
	@Autowired
	private FavoritoService favoritoService;
 
	// Obtener favoritos
	@GetMapping("/favoritos")
	public List<Favorito> index() {
		return favoritoService.findAll();
	}
	
	// Obtener favoritos por ID
	@GetMapping("/favorito/{id}")
	public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {
		
		Favorito favorito;
		Map<String, Object> response = new HashMap<>();
		
		try {
			favorito = favoritoService.findById(id);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(favorito == null) {
			response.put("mensaje", "El favorito con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(favorito, HttpStatus.OK);
	}
 
	// Crear favorito
	@PostMapping("/favorito")
	public ResponseEntity<?> create(@RequestBody Favorito favorito, BindingResult result) {
		
		Favorito nuevoFavorito;
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
			if (favorito.getFechaAgregado() == null) {
				favorito.setFechaAgregado(new Date());
	        }
			nuevoFavorito = favoritoService.save(favorito);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al insertar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El favorito ha sido creado con éxito");
		response.put("favorito", nuevoFavorito);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
 
	// Actualizar favorito
	@PutMapping("/favorito/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@RequestBody Favorito favorito, BindingResult result ,@PathVariable(name = "id") Long id) {
		
		Favorito currentFavorito = this.favoritoService.findById(id);
		Favorito nuevoFavorito;
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
		
		if(currentFavorito == null) {
			response.put("mensaje", "No se puedo editar, el favorito con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			currentFavorito.setLibro(favorito.getLibro());
			currentFavorito.setUsuario(favorito.getUsuario());
			
			nuevoFavorito = favoritoService.save(currentFavorito);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al actualizar el favorito en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El favorito ha sido actualizado con éxito");
		response.put("cliente", nuevoFavorito);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
 
	// Eliminar favorito por ID
	@DeleteMapping("/favorito/{id}")
	public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			Favorito currentFavorito = this.favoritoService.findById(id);
			
			if (currentFavorito == null) {
				response.put("mensaje", "El favorito con ID: " + id + " no existe en la base de datos");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			favoritoService.delete(currentFavorito);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al eliminar el favorito en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El favorito ha sido eliminado con éxito");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
 
	// Eliminar favorito por usuario y libro
	@DeleteMapping("/favorito/usuario/{usuarioId}/libro/{libroId}")
	public ResponseEntity<?> deleteByUsuarioAndLibro(
		@PathVariable Long usuarioId,
		@PathVariable Long libroId
	) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<Favorito> favoritos = favoritoService.findByUsuarioId(usuarioId);
			Favorito favoritoToDelete = favoritos.stream()
				.filter(f -> f.getLibro().getId().equals(libroId))
				.findFirst()
				.orElse(null);

			if (favoritoToDelete == null) {
				response.put("mensaje", "No se encontró el favorito para el usuario " + usuarioId + " y libro " + libroId);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			favoritoService.delete(favoritoToDelete);
			response.put("mensaje", "El favorito ha sido eliminado con éxito");
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch(DataAccessException e) {
			response.put("mensaje", "Error al eliminar el favorito en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
 
	// Obtener favoritos por ID de usuario
	@GetMapping("/favorito/usuario/{usuarioId}")
	public ResponseEntity<?> getFavoritosByUsuarioId(@PathVariable Long usuarioId) {
		List<Favorito> favoritos;
		Map<String, Object> response = new HashMap<>();
		
		try {
			favoritos = favoritoService.findByUsuarioId(usuarioId);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(favoritos.isEmpty()) {
			response.put("mensaje", "El usuario con ID: ".concat(usuarioId.toString().concat(" no tiene favoritos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(favoritos, HttpStatus.OK);
	}
 
}
