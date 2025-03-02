package com.evidenlibrary.backend.apirest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evidenlibrary.backend.apirest.model.entity.Carrito;
import com.evidenlibrary.backend.apirest.model.service.CarritoService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/carritos")
    public List<Carrito> index() {
        List<Carrito> carritos = carritoService.findAll();
        // Agregar logging para debug
        carritos.forEach(carrito -> {
            System.out.println("Carrito ID: " + carrito.getId());
            System.out.println("Usuario: " + (carrito.getUsuario() != null ? 
                carrito.getUsuario().getNombre() : "null"));
            System.out.println("Número de detalles: " + 
                (carrito.getDetalles() != null ? carrito.getDetalles().size() : 0));
            if (carrito.getDetalles() != null) {
                carrito.getDetalles().forEach(detalle -> {
                    System.out.println("  Detalle - Libro: " + 
                        (detalle.getLibro() != null ? detalle.getLibro().getTitulo() : "null") +
                        ", Cantidad: " + detalle.getCantidad());
                });
            }
        });
        return carritos;
    }

    @GetMapping("/carrito/{id}")
    public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {
        Carrito carrito;
        Map<String, Object> response = new HashMap<>();

        try {
            carrito = carritoService.findById(id);
            // Forzar la carga de las relaciones
            if (carrito != null) {
                carrito.getUsuario(); // Forzar carga del usuario
                carrito.getDetalles().size(); // Forzar carga de detalles
                carrito.getDetalles().forEach(detalle -> {
                    detalle.getLibro(); // Forzar carga de cada libro
                });
            }
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (carrito == null) {
            response.put("mensaje", "El carrito con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(carrito, HttpStatus.OK);
    }

    @GetMapping("/carritos/usuario/{id}")
    public ResponseEntity<?> findByUsuarioId(@PathVariable(name = "id") Long usuarioId) {
        List<Carrito> carritos;
        Map<String, Object> response = new HashMap<>();

        try {
            carritos = carritoService.findByUsuarioId(usuarioId);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(carritos, HttpStatus.OK);
    }

    @PostMapping("/carrito")
    public ResponseEntity<?> create(@RequestBody Carrito carrito) {
        Carrito nuevoCarrito;
        Map<String, Object> response = new HashMap<>();

        try {
            nuevoCarrito = carritoService.save(carrito);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El carrito ha sido creado con éxito!");
        response.put("carrito", nuevoCarrito);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/carrito/{id}")
    public ResponseEntity<?> update(@RequestBody Carrito carrito, @PathVariable(name = "id") Long id) {
        Carrito carritoActual = carritoService.findById(id);
        Carrito carritoUpdated;
        Map<String, Object> response = new HashMap<>();

        if (carritoActual == null) {
            response.put("mensaje", "Error: no se pudo editar, el carrito con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            carritoActual.setEstado(carrito.getEstado());
            carritoActual.setFechaCreacion(carrito.getFechaCreacion());
            carritoActual.setUsuario(carrito.getUsuario());
            carritoActual.setDetalles(carrito.getDetalles());

            carritoUpdated = carritoService.save(carritoActual);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El carrito ha sido actualizado con éxito!");
        response.put("carrito", carritoUpdated);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/carrito/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Carrito carrito = carritoService.findById(id);
            if (carrito != null) {
                carritoService.delete(carrito);
            } else {
                response.put("mensaje", "El carrito con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el carrito de la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El carrito ha sido eliminado con éxito!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 