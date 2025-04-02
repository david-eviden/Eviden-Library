package com.evidenlibrary.backend.apirest.controller;

import java.util.ArrayList;
import java.util.Date;
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
import com.evidenlibrary.backend.apirest.model.entity.DetalleCarrito;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.CarritoService;
import com.evidenlibrary.backend.apirest.model.service.DetalleCarritoService;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class DetalleCarritoController {

    @Autowired
    private DetalleCarritoService detalleCarritoService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/detalles-carrito")
    public List<DetalleCarrito> index() {
        return detalleCarritoService.findAll();
    }

    @GetMapping("/detalle-carrito/{id}")
    public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {
        DetalleCarrito detalleCarrito;
        Map<String, Object> response = new HashMap<>();

        try {
            detalleCarrito = detalleCarritoService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (detalleCarrito == null) {
            response.put("mensaje", "El detalle de carrito con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(detalleCarrito, HttpStatus.OK);
    }

    @GetMapping("/detalles-carrito/carrito/{id}")
    public ResponseEntity<?> findByCarritoId(@PathVariable(name = "id") Long carritoId) {
        List<DetalleCarrito> detalles;
        Map<String, Object> response = new HashMap<>();

        try {
            detalles = detalleCarritoService.findByCarritoId(carritoId);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }

    @PostMapping("/detalle-carrito")
    public ResponseEntity<?> create(@RequestBody DetalleCarrito detalleCarrito) {
        DetalleCarrito nuevoDetalle;
        Map<String, Object> response = new HashMap<>();

        try {
            nuevoDetalle = detalleCarritoService.save(detalleCarrito);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El detalle de carrito ha sido creado con éxito!");
        response.put("detalleCarrito", nuevoDetalle);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/detalle-carrito/{id}")
    public ResponseEntity<?> update(@RequestBody DetalleCarrito detalleCarrito, @PathVariable(name = "id") Long id) {
        DetalleCarrito detalleActual = detalleCarritoService.findById(id);
        DetalleCarrito detalleUpdated;
        Map<String, Object> response = new HashMap<>();

        if (detalleActual == null) {
            response.put("mensaje", "Error: no se pudo editar, el detalle de carrito con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            detalleActual.setCantidad(detalleCarrito.getCantidad());
            detalleActual.setCarrito(detalleCarrito.getCarrito());
            detalleActual.setLibro(detalleCarrito.getLibro());

            detalleUpdated = detalleCarritoService.save(detalleActual);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El detalle de carrito ha sido actualizado con éxito!");
        response.put("detalleCarrito", detalleUpdated);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/detalle-carrito/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            DetalleCarrito detalle = detalleCarritoService.findById(id);
            if (detalle != null) {
                detalleCarritoService.delete(detalle);
            } else {
                response.put("mensaje", "El detalle de carrito con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el detalle de carrito de la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El detalle de carrito ha sido eliminado con éxito!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/detalles-carrito/add/{usuarioId}")
    public ResponseEntity<?> addToCart(@PathVariable Long usuarioId, @RequestBody DetalleCarrito detalleCarrito) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Obtener el usuario
            Usuario usuario = usuarioService.findById(usuarioId);
            if (usuario == null) {
                response.put("mensaje", "Error: el usuario no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Buscar o crear el carrito del usuario
            List<Carrito> carritos = carritoService.findByUsuarioId(usuarioId);
            Carrito carrito;
            
            if (carritos.isEmpty() || carritos.stream().noneMatch(c -> "activo".equals(c.getEstado()))) {
                // Crear nuevo carrito si no existe uno activo
                carrito = new Carrito();
                carrito.setUsuario(usuario);
                carrito.setFechaCreacion(new Date());
                carrito.setEstado("activo");
                carrito = carritoService.save(carrito);
            } else {
                // Usar el carrito activo existente
                carrito = carritos.stream()
                    .filter(c -> "activo".equals(c.getEstado()))
                    .findFirst()
                    .get();
            }

            // Buscar si el libro ya existe en el carrito
            List<DetalleCarrito> detallesExistentes = detalleCarritoService.findByCarritoId(carrito.getId());
            DetalleCarrito detalleExistente = detallesExistentes.stream()
                .filter(d -> d.getLibro().getId().equals(detalleCarrito.getLibro().getId()))
                .findFirst()
                .orElse(null);

            DetalleCarrito nuevoDetalle;
            if (detalleExistente != null) {
                // Actualizar la cantidad del detalle existente
                detalleExistente.setCantidad(detalleExistente.getCantidad() + detalleCarrito.getCantidad());
                nuevoDetalle = detalleCarritoService.save(detalleExistente);
                response.put("mensaje", "Se ha actualizado la cantidad del libro en el carrito");
            } else {
                // Crear nuevo detalle
                detalleCarrito.setCarrito(carrito);
                nuevoDetalle = detalleCarritoService.save(detalleCarrito);
                response.put("mensaje", "El libro ha sido añadido al carrito con éxito!");
            }

            response.put("detalleCarrito", nuevoDetalle);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al añadir el libro al carrito");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detalles-carrito/usuario/{usuarioId}")
    public ResponseEntity<?> findByUsuarioIdAndCarritoActivo(@PathVariable Long usuarioId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Buscar el carrito activo del usuario
            List<Carrito> carritos = carritoService.findByUsuarioId(usuarioId);
            Carrito carritoActivo = carritos.stream()
                .filter(c -> "activo".equals(c.getEstado()))
                .findFirst()
                .orElse(null);

            if (carritoActivo == null) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }

            List<DetalleCarrito> detalles = detalleCarritoService.findByCarritoId(carritoActivo.getId());
            return new ResponseEntity<>(detalles, HttpStatus.OK);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 