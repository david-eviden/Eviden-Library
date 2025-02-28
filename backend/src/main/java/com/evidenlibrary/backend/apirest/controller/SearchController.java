package com.evidenlibrary.backend.apirest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evidenlibrary.backend.apirest.model.service.SearchService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class SearchController {
	@Autowired
    private SearchService searchService;

	
	@GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String query) {

		Map<String,Object> results = new HashMap<>();
		results.put("libros", searchService.searchLibros(query));
		results.put("autores", searchService.searchAutores(query));
		results.put("generos", searchService.searchGeneros(query));
		return ResponseEntity.ok(results);
	}
}