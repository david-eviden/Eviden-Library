package com.evidenlibrary.backend.apirest.model.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Autor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    @Size(min=5, max=50, message="debe tener entre 5 y 50 caracteres")
    @NotEmpty(message = "no puede estar vacío")
    private String nombre;

    @Column(nullable = false)
    @Size(min=10, max=250, message="debe tener entre 10 y 250 caracteres")
    @NotEmpty(message = "no puede estar vacío")
    private String biografia;

    @ManyToMany(mappedBy = "autores", fetch = FetchType.LAZY)
    @JsonBackReference
    private final Set<Libro> libros = new HashSet<>();

    @JsonProperty("libros")
    public Set<LibroSimple> getLibrosSimples() {
        return libros.stream()
            .map(libro -> new LibroSimple(libro.getId(), libro.getTitulo(), libro.getPrecio(), libro.getStock()))
            .collect(Collectors.toSet());
    }

    public static class LibroSimple {
        private Long id;
        private String titulo;
        private Double precio;
        private Integer stock;

        public LibroSimple(Long id, String titulo, Double precio, Integer stock) {
            this.id = id;
            this.titulo = titulo;
            this.precio = precio;
            this.stock = stock;
        }

        public LibroSimple(Long id, Double precio, Integer stock, String titulo) {
            this.id = id;
            this.precio = precio;
            this.stock = stock;
            this.titulo = titulo;
        }

        // Getters
        public Long getId() { return id; }
        public String getTitulo() { return titulo; }
        public Double getPrecio() { return precio; }
        public Integer getStock() { return stock; }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setPrecio(Double precio) {
            this.precio = precio;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }
    
    // Getters and setters

    public Long getId() {
        return id;
    }
    
    
    public void setId(Long id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public Set<Libro> getLibros() {
        return libros;
    }
}