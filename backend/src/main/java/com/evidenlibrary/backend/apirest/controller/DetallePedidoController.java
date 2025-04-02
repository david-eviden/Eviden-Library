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

import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;
import com.evidenlibrary.backend.apirest.model.service.DetallePedidoService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    @GetMapping("/detalles-pedido")
    public List<DetallePedido> index() {
        return detallePedidoService.findAll();
    }

    @GetMapping("/detalle-pedido/{id}")
    public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {
        DetallePedido detallePedido;
        Map<String, Object> response = new HashMap<>();

        try {
            detallePedido = detallePedidoService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (detallePedido == null) {
            response.put("mensaje", "El detalle de pedido con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(detallePedido, HttpStatus.OK);
    }

    @GetMapping("/detalles-pedido/pedido/{id}")
    public ResponseEntity<?> findByPedidoId(@PathVariable(name = "id") Long pedidoId) {
        List<DetallePedido> detalles;
        Map<String, Object> response = new HashMap<>();

        try {
            detalles = detallePedidoService.findByPedidoId(pedidoId);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }

    @PostMapping("/detalle-pedido")
    public ResponseEntity<?> create(@RequestBody DetallePedido detallePedido) {
        DetallePedido nuevoDetalle;
        Map<String, Object> response = new HashMap<>();

        try {
            nuevoDetalle = detallePedidoService.save(detallePedido);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El detalle de pedido ha sido creado con éxito!");
        response.put("detallePedido", nuevoDetalle);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/detalle-pedido/{id}")
    public ResponseEntity<?> update(@RequestBody DetallePedido detallePedido, @PathVariable(name = "id") Long id) {
        DetallePedido detalleActual = detallePedidoService.findById(id);
        DetallePedido detalleUpdated;
        Map<String, Object> response = new HashMap<>();

        if (detalleActual == null) {
            response.put("mensaje", "Error: no se pudo editar, el detalle de pedido con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            detalleActual.setCantidad(detallePedido.getCantidad());
            detalleActual.setPrecioUnitario(detallePedido.getPrecioUnitario());
            detalleActual.setPedido(detallePedido.getPedido());
            detalleActual.setLibro(detallePedido.getLibro());

            detalleUpdated = detallePedidoService.save(detalleActual);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El detalle de pedido ha sido actualizado con éxito!");
        response.put("detallePedido", detalleUpdated);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/detalle-pedido/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            DetallePedido detalle = detallePedidoService.findById(id);
            if (detalle != null) {
                detallePedidoService.delete(detalle);
            } else {
                response.put("mensaje", "El detalle de pedido con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el detalle de pedido de la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El detalle de pedido ha sido eliminado con éxito!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 