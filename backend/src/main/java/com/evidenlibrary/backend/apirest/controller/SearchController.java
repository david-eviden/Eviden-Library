package com.evidenlibrary.backend.apirest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evidenlibrary.backend.apirest.model.entity.Autor;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.service.AutorService;
import com.evidenlibrary.backend.apirest.model.service.LibroService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class SearchController {
	@Autowired
    private LibroService libroService;

	
	@GetMapping("/search")
    public ResponseEntity<?> busqueda(@RequestParam String query) {
		String lowerCaseQuery = query.toLowerCase();

		List<Libro> resultados = libroService.findAll()
		    .stream()
		    .filter(libro -> 
		            	// Buscar en título
		                libro.getTitulo().toLowerCase().contains(lowerCaseQuery) || 
		                // Buscar en autores
		                libro.getAutores().stream()
		                    .anyMatch(autor -> autor.getNombre().toLowerCase().contains(lowerCaseQuery)) ||
		                // Buscar en géneros
		                libro.getGeneros().stream()
		                    .anyMatch(genero -> genero.getNombre().toLowerCase().contains(lowerCaseQuery))
		            )
		     .collect(Collectors.toList());
		return ResponseEntity.ok(resultados);

		}
	
	/*@GetMapping("/titulo")
    public ResponseEntity<?> busquedaTitulo(@RequestParam String query) {
		
	}
	@GetMapping("/genero")
    public ResponseEntity<?> busquedaGenero(@RequestParam String query) {
		
	}*/
}