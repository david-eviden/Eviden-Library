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

import com.evidenlibrary.backend.apirest.model.entity.Valoracion;
import com.evidenlibrary.backend.apirest.model.service.ValoracionService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ValoracionController {

    @Autowired
    private ValoracionService valoracionService;

    @GetMapping("/valoraciones")
    public List<Valoracion> index() {
        return valoracionService.findAll();
    }

    @GetMapping("/valoracion/{id}")
    public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {
        Valoracion valoracion;
        Map<String, Object> response = new HashMap<>();

        try {
            valoracion = valoracionService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (valoracion == null) {
            response.put("mensaje", "La valoración con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(valoracion, HttpStatus.OK);
    }

    @GetMapping("/valoraciones/libro/{id}")
    public ResponseEntity<?> findByLibroId(@PathVariable(name = "id") Long libroId) {
        List<Valoracion> valoraciones;
        Map<String, Object> response = new HashMap<>();

        try {
            valoraciones = valoracionService.findByLibroId(libroId);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(valoraciones, HttpStatus.OK);
    }

    @GetMapping("/valoraciones/usuario/{id}")
    public ResponseEntity<?> findByUsuarioId(@PathVariable(name = "id") Long usuarioId) {
        List<Valoracion> valoraciones;
        Map<String, Object> response = new HashMap<>();

        try {
            valoraciones = valoracionService.findByUsuarioId(usuarioId);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(valoraciones, HttpStatus.OK);
    }
    
    //metodo para comprobar que el usuario no haya valorado antes el libro
    private boolean existeValoracionPreviaUsuarioLibro(Long usuarioId, Long libroId) {
        List<Valoracion> valoracionesUsuario = valoracionService.findByUsuarioId(usuarioId);
        for (Valoracion v : valoracionesUsuario) {
            if (v.getLibro().getId().equals(libroId)) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/valoracion")
    public ResponseEntity<?> create(@RequestBody Valoracion valoracion) {
        Valoracion nuevaValoracion;
        Map<String, Object> response = new HashMap<>();
        
        // Verificar si el usuario ya ha valorado este libro
        if (existeValoracionPreviaUsuarioLibro(valoracion.getUsuario().getId(), valoracion.getLibro().getId())) {
            response.put("mensaje", "Error: El usuario ya ha valorado este libro anteriormente");
            response.put("error", "Un usuario solo puede valorar un libro una vez");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }        

        try {
            nuevaValoracion = valoracionService.save(valoracion);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La valoración ha sido creada con éxito!");
        response.put("valoracion", nuevaValoracion);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /*
    @PostMapping("/valoracion/libro/{libroId}")
    public ResponseEntity<?> createByLibroId(@PathVariable Long libroId) {
        List<Valoracion> valoraciones;
        Map<String, Object> response = new HashMap<>();

        try {
            valoraciones = valoracionService.findByLibroId(libroId);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(valoraciones, HttpStatus.OK);
    } */

    @PutMapping("/valoracion/{id}")
    public ResponseEntity<?> update(@RequestBody Valoracion valoracion, @PathVariable(name = "id") Long id) {
        Valoracion valoracionActual = valoracionService.findById(id);
        Valoracion valoracionUpdated;
        Map<String, Object> response = new HashMap<>();

        if (valoracionActual == null) {
            response.put("mensaje", "Error: no se pudo editar, la valoración con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            valoracionActual.setPuntuacion(valoracion.getPuntuacion());
            valoracionActual.setComentario(valoracion.getComentario());
            valoracionActual.setFecha(valoracion.getFecha());
            valoracionActual.setUsuario(valoracion.getUsuario());
            valoracionActual.setLibro(valoracion.getLibro());

            valoracionUpdated = valoracionService.save(valoracionActual);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La valoración ha sido actualizada con éxito!");
        response.put("valoracion", valoracionUpdated);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/valoracion/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Valoracion valoracion = valoracionService.findById(id);
            if (valoracion != null) {
                valoracionService.delete(valoracion);
            } else {
                response.put("mensaje", "La valoración con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar la valoración de la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La valoración ha sido eliminada con éxito!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 