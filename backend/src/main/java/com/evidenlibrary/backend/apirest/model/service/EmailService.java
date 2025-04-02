package com.evidenlibrary.backend.apirest.model.service;

import com.evidenlibrary.backend.apirest.model.entity.Pedido;

public interface EmailService {
    
    /**
     * Envía un email de confirmación de pedido al usuario
     * 
     * @param pedido El pedido realizado
     * @param emailDestino El email del destinatario
     * @return true si el email se envió correctamente, false en caso contrario
     */
    boolean enviarEmailConfirmacionPedido(Pedido pedido, String emailDestino);
} 