package com.evidenlibrary.backend.apirest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	private String nombre;
	    
	@Column(nullable = false, unique = true)
	private String email;
	    
	@Column(nullable = false)
	private String password;
	    
	@Column(nullable = false)
	private String rol;
	    
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private List<Pedido> pedidos = new ArrayList<>();
	    
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private List<Carrito> carritos = new ArrayList<>();
	    
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private List<Valoracion> valoraciones = new ArrayList<>();
	
}
