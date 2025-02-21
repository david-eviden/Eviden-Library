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

import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;
import com.evidenlibrary.backend.apirest.model.service.DetallePedidoService;


@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class DetallesPedidoController {

	@Autowired
	private DetallePedidoService detallePedidoService;

	// Obtener detalles pedido
	@GetMapping("/detallePedidos")
	public List<DetallePedido> index() {
		return detallePedidoService.findAll();
	}
	
	// Obtener detalles pedido por ID
	@GetMapping("/detallePedido/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		DetallePedido detallePedido = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			detallePedido = detallePedidoService.findById(id);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(detallePedido == null) {
			response.put("mensaje", "El detalles pedido con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<DetallePedido>(detallePedido, HttpStatus.OK); 
	}

	// Crear detalles pedido
	@PostMapping("/detallePedido")
	public ResponseEntity<?> create(@RequestBody DetallePedido detallePedido, BindingResult result) {
		
		DetallePedido nuevoDetallePedido = null;
		Map<String, Object> response = new HashMap<>();
		
		// Validamos campos
		if(result.hasErrors()) {
			List<String> errores = result.getFieldErrors()
				.stream()
				.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
				.collect(Collectors.toList());
			
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// Manejamos errores
		try {
			nuevoDetallePedido = detallePedidoService.save(detallePedido);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al insertar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El detalle pedido ha sido creado con éxito");
		response.put("detallePedido", nuevoDetallePedido);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// Actualizar detalles pedido
	@PutMapping("/detallePedido/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@RequestBody DetallePedido detallePedido, BindingResult result ,@PathVariable Long id) {
		
		DetallePedido currentDetallePedido = this.detallePedidoService.findById(id);
		DetallePedido nuevoDetallePedido = null;
		Map<String, Object> response = new HashMap<>();
		
		// Validamos campos
		if(result.hasErrors()) {
			
			List<String> errores = result.getFieldErrors()
				.stream()
				.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
				.collect(Collectors.toList());
			
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(currentDetallePedido == null) {
			response.put("mensaje", "No se puedo editar, los detalles del pedido con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			currentDetallePedido.setCantidad(detallePedido.getCantidad());
			currentDetallePedido.setLibro(detallePedido.getLibro());
			
			nuevoDetallePedido = detallePedidoService.save(currentDetallePedido);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al actualizar los detalles del pedido en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Los detalles del pedido han sido actualizado con éxito");
		response.put("Detalle Pedido", detallePedido);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// Eliminar detallePedido por ID
	@DeleteMapping("/detallePedido/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		DetallePedido currentDetallePedido = this.detallePedidoService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		try {
			detallePedidoService.delete(currentDetallePedido);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al eliminar los detalles del pedido en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Los detalles del pedido han sido eliminados con éxito");
		response.put("Detalle Pedido", currentDetallePedido);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
