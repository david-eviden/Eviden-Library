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
import com.evidenlibrary.backend.apirest.model.entity.Pedido;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;
import com.evidenlibrary.backend.apirest.model.service.DetallePedidoService;
import com.evidenlibrary.backend.apirest.model.service.EmailService;
import com.evidenlibrary.backend.apirest.model.service.PedidoService;
import com.evidenlibrary.backend.apirest.model.service.UsuarioService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private DetallePedidoService detallePedidoService;

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

    @PostMapping("/pedido/enviar-email/{id}")
    public ResponseEntity<?> enviarEmailConfirmacion(@PathVariable(name = "id") Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Obtener el pedido completo con todos sus detalles
            Pedido pedido = pedidoService.findById(id);
            
            if (pedido == null) {
                response.put("mensaje", "El pedido con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            // Cargar explícitamente los detalles del pedido
            List<DetallePedido> detalles = detallePedidoService.findByPedidoId(id);
            pedido.setDetalles(detalles);
            
            // Obtener el usuario completo para acceder a su email
            if (pedido.getUsuario() != null && pedido.getUsuario().getId() != null) {
                Usuario usuario = usuarioService.findById(pedido.getUsuario().getId());
                if (usuario != null && usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                    // Enviar email de confirmación
                    boolean emailEnviado = emailService.enviarEmailConfirmacionPedido(pedido, usuario.getEmail());
                    if (emailEnviado) {
                        response.put("emailEnviado", true);
                        response.put("mensaje", "Email de confirmación enviado correctamente a: " + usuario.getEmail());
                        System.out.println("Email de confirmación enviado a: " + usuario.getEmail());
                    } else {
                        response.put("emailEnviado", false);
                        response.put("mensaje", "No se pudo enviar el email de confirmación a: " + usuario.getEmail());
                        System.out.println("No se pudo enviar el email de confirmación a: " + usuario.getEmail());
                    }
                } else {
                    response.put("emailEnviado", false);
                    response.put("mensaje", "No se pudo enviar el email de confirmación: email de usuario no disponible");
                    System.out.println("No se pudo enviar el email de confirmación: email de usuario no disponible");
                }
            } else {
                response.put("emailEnviado", false);
                response.put("mensaje", "No se pudo enviar el email de confirmación: usuario no disponible");
                System.out.println("No se pudo enviar el email de confirmación: usuario no disponible");
            }
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al enviar el email de confirmación");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/pedido")
    public ResponseEntity<?> create(@RequestBody Pedido pedido) {
        Pedido nuevoPedido;
        Map<String, Object> response = new HashMap<>();

        try {
            nuevoPedido = pedidoService.save(pedido);
            
            // Ya no enviamos el email aquí, lo haremos después de crear todos los detalles
            // desde el frontend
            
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