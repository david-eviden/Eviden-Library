package com.evidenlibrary.backend.apirest.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.service.LibroService;

@CrossOrigin(origins = { "http://localhost:4200" })
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
        return libroService.findAllPaginado(PageRequest.of(page, 9));
    }
    
    //Obtener mejor valorados
    @GetMapping("/libros/mejor-valorados")
    public List<Libro> getMejorValorados() {
        return libroService.getMejorValorados();
    }

	// Obtener libros por ID
	@GetMapping("/libro/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {

		Libro libro;
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
	public ResponseEntity<?> create(@RequestBody Libro libro, @RequestParam("imagen") MultipartFile imagen, BindingResult result) throws IOException {

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
			// Obtener los bytes de la imagen y el tipo MIME
	        byte[] imagenBytes = imagen.getBytes();
	        String tipoImagen = imagen.getContentType();

	        // Asignar la imagen y el tipo MIME al objeto libro
	        libro.setPortada(imagenBytes);
	        libro.setTipoImagen(tipoImagen);

	        // Guardar el libro
	        nuevoLibro = libroService.save(libro);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al insertar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El libro ha sido creado con éxito");
		response.put("autor", nuevoLibro);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Actualizar libro
	@PutMapping("/libro/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@RequestBody Libro libro,@RequestParam("imagen") MultipartFile imagen, BindingResult result, @PathVariable Long id) throws IOException {

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
			response.put("mensaje", "No se puedo editar, el libro con ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentLibro.setPrecio(libro.getPrecio());
			currentLibro.setStock(libro.getStock());
			currentLibro.setTitulo(libro.getTitulo());
			currentLibro.setDescripcion(libro.getDescripcion());
			
			// Si se ha enviado una nueva imagen, actualizar la portada
	        if (imagen != null && !imagen.isEmpty()) {
	        	byte[] imagenBytes = imagen.getBytes();
		        String tipoImagen = imagen.getContentType();

	            currentLibro.setPortada(imagenBytes);
	            currentLibro.setTipoImagen(tipoImagen);
	        }

			nuevoLibro = libroService.save(currentLibro);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el libro en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El libro ha sido actualizado con éxito");
		response.put("cliente", nuevoLibro);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Eliminar libro por ID
	@DeleteMapping("/libros/{id}")
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
	
}
