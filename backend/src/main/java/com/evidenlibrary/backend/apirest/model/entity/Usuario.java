package com.evidenlibrary.backend.apirest.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	    
	@Column(nullable = false)
	@JsonProperty("nombre")
	private String nombre;
	
	@Column(nullable = false)
	@JsonProperty("apellido")
	private String apellido;
	
	@Column(nullable = true)
	private String direccion;
	    
	@Column(nullable = false, unique = true)
	private String email;
	    
	@JsonIgnore
	@Column(nullable = false)
	private String password;
	    
	@Column(nullable = false)
	private String rol;
	
	@Column(columnDefinition = "LONGTEXT")
	private String foto;
	    
	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	public final List<Pedido> pedidos = new ArrayList<>();
	
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	@JsonIgnore
	public final List<Carrito> carritos = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	public final List<Valoracion> valoraciones = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE)
	public final List<Favorito> favoritos = new ArrayList<>();

	
	//Getters y setters

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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public List<Carrito> getCarritos() {
		return carritos;
	}


	public List<Valoracion> getValoraciones() {
		return valoraciones;
	}

	public List<Favorito> getFavoritos() {
		return favoritos;
	}
	
	
}
