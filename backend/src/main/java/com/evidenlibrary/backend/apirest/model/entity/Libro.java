package com.evidenlibrary.backend.apirest.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "libros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Libro implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String titulo;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = true, columnDefinition = "LONGTEXT")
    private String imagen;
    
    @Column(nullable = true)
    private String anio;

	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "libro_autor",
        joinColumns = @JoinColumn(name = "libro_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private final Set<Autor> autores = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "libro_genero",
        joinColumns = @JoinColumn(name = "libro_id"),
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private final Set<Genero> generos = new HashSet<>();
    
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<Valoracion> valoraciones = new ArrayList<>();
    
    @OneToMany(mappedBy = "libro", fetch = FetchType.EAGER)
    @JsonIgnore
    private final List<Favorito> favoritos = new ArrayList<>();
    
    @Column(name = "valoracion_media", nullable = true)
    private Double valoracionMedia;
    
    // Getter/Setter
    
    public String getDescripcion() {
		return descripcion;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public List<Favorito> getFavoritos() {
		return favoritos;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
    
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Set<Autor> getAutores() {
		return autores;
	}

	public Set<Genero> getGeneros() {
		return generos;
	}

	public List<Valoracion> getValoraciones() {
		return valoraciones;
	}

	public Long getId() {
		return id;
	}	
	
	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	//Valoracion media
	public Double getValoracionMedia() {
        if (valoraciones.isEmpty()) { // No hay valoraciones
        	this.valoracionMedia = 0.0;
        	return this.valoracionMedia;
        }
        // Calcular la media
        /*double suma = 0.0;
        for (Valoracion v : valoraciones) {
            suma += v.getPuntuacion();
        }*/
        double suma = valoraciones.stream()
        			.mapToDouble(Valoracion::getPuntuacion)
        			.sum();
        this.valoracionMedia = suma / valoraciones.size();
        return valoracionMedia;
    }

	public void setValoracionMedia(Double valoracionMedia) {
		this.valoracionMedia = valoracionMedia;
	}
	
	
}
