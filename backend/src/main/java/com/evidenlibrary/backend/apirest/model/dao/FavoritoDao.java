package com.evidenlibrary.backend.apirest.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evidenlibrary.backend.apirest.model.entity.Favorito;

public interface FavoritoDao extends JpaRepository<Favorito, Long> {
	@Query("SELECT f FROM Favorito f WHERE f.usuario.id = :usuarioId")
	List<Favorito> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
