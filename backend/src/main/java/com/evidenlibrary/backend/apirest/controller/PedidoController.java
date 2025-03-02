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

import com.evidenlibrary.backend.apirest.model.entity.Pedido;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.PedidoService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/pedidos")
    public List<Pedido> index() {
        List<Pedido> pedidos = pedidoService.findAll();
        pedidos.forEach(pedido -> {
            Usuario usuario = pedido.getUsuario();
            if (usuario != null) {
                System.out.println("Pedido ID: " + pedido.getId() + 
                                 ", Usuario ID: " + usuario.getId() + 
                                 ", Nombre: " + usuario.getNombre());
            } else {
                System.out.println("Pedido ID: " + pedido.getId() + ", Usuario: null");
            }
        });
        return pedidos;
    }

    @GetMapping("/pedido/{id}")
    public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {
        Pedido pedido;
        Map<String, Object> response = new HashMap<>();

        try {
            pedido = pedidoService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (pedido == null) {
            response.put("mensaje", "El pedido con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(pedido, HttpStatus.OK);
    }

    @GetMapping("/pedidos/usuario/{id}")
    public ResponseEntity<?> findByUsuarioId(@PathVariable(name = "id") Long usuarioId) {
        List<Pedido> pedidos;
        Map<String, Object> response = new HashMap<>();

        try {
            pedidos = pedidoService.findPedidosByUsuarioId(usuarioId);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @PostMapping("/pedido")
    public ResponseEntity<?> create(@RequestBody Pedido pedido) {
        Pedido nuevoPedido;
        Map<String, Object> response = new HashMap<>();

        try {
            nuevoPedido = pedidoService.save(pedido);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El pedido ha sido creado con éxito!");
        response.put("pedido", nuevoPedido);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/pedido/{id}")
    public ResponseEntity<?> update(@RequestBody Pedido pedido, @PathVariable(name = "id") Long id) {
        Pedido pedidoActual = pedidoService.findById(id);
        Pedido pedidoUpdated;
        Map<String, Object> response = new HashMap<>();

        if (pedidoActual == null) {
            response.put("mensaje", "Error: no se pudo editar, el pedido con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            pedidoActual.setEstado(pedido.getEstado());
            pedidoActual.setFechaPedido(pedido.getFechaPedido());
            pedidoActual.setTotal(pedido.getTotal());
            pedidoActual.setDireccionEnvio(pedido.getDireccionEnvio());
            pedidoActual.setUsuario(pedido.getUsuario());
            pedidoActual.setDetalles(pedido.getDetalles());

            pedidoUpdated = pedidoService.save(pedidoActual);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El pedido ha sido actualizado con éxito!");
        response.put("pedido", pedidoUpdated);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/pedido/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Pedido pedido = pedidoService.findById(id);
            if (pedido != null) {
                pedidoService.delete(pedido);
            } else {
                response.put("mensaje", "El pedido con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el pedido de la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El pedido ha sido eliminado con éxito!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 