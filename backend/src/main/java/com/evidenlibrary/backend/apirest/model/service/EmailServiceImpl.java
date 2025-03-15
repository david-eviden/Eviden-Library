package com.evidenlibrary.backend.apirest.model.service;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;
import com.evidenlibrary.backend.apirest.model.entity.Pedido;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public boolean enviarEmailConfirmacionPedido(Pedido pedido, String emailDestino) {
        try {
            // Imprimir información de depuración
            System.out.println("Enviando email de confirmación para el pedido #" + pedido.getId());
            System.out.println("Email destino: " + emailDestino);
            System.out.println("Dirección de envío: " + pedido.getDireccionEnvio());
            System.out.println("Número de detalles: " + (pedido.getDetalles() != null ? pedido.getDetalles().size() : 0));
            
            if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                System.out.println("Detalles del pedido:");
                for (DetallePedido detalle : pedido.getDetalles()) {
                    System.out.println("  - Libro: " + (detalle.getLibro() != null ? detalle.getLibro().getTitulo() : "null") + 
                                     ", Cantidad: " + detalle.getCantidad() + 
                                     ", Precio: " + detalle.getPrecioUnitario());
                }
            } else {
                System.out.println("No hay detalles disponibles para este pedido");
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(emailDestino);
            helper.setSubject("Confirmación de pedido #" + pedido.getId() + " - Eviden Library");
            
            String contenido = generarContenidoEmailPedido(pedido);
            
            helper.setText(contenido, true);
            
            mailSender.send(message);
            System.out.println("Email enviado correctamente");
            return true;
        } catch (MessagingException e) {
            System.err.println("Error al enviar el email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private String generarContenidoEmailPedido(Pedido pedido) {
        StringBuilder contenido = new StringBuilder();
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
        
        // Encabezado
        contenido.append("<html><body>");
        contenido.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        contenido.append("<div style='background-color: #4a148c; color: white; padding: 20px; text-align: center;'>");
        contenido.append("<h1>¡Gracias por tu compra!</h1>");
        contenido.append("</div>");
        
        // Información del pedido
        contenido.append("<div style='padding: 20px; background-color: #f9f9f9;'>");
        contenido.append("<h2>Detalles del pedido #").append(pedido.getId()).append("</h2>");
        
        // Formatear la fecha para mostrar solo DD/MM/YYYY
        String fechaFormateada = "";
        if (pedido.getFechaPedido() != null) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                fechaFormateada = sdf.format(pedido.getFechaPedido());
            } catch (Exception e) {
                System.err.println("Error al formatear la fecha: " + e.getMessage());
                fechaFormateada = String.valueOf(pedido.getFechaPedido());
            }
        } else {
            fechaFormateada = "No disponible";
        }
        
        contenido.append("<p><strong>Fecha:</strong> ").append(fechaFormateada).append("</p>");
        contenido.append("<p><strong>Estado:</strong> ").append(pedido.getEstado()).append("</p>");
        
        // Dirección de envío (verificar que no sea nula)
        String direccionEnvio = pedido.getDireccionEnvio();
        if (direccionEnvio == null || direccionEnvio.trim().isEmpty() || "Sin dirección especificada".equals(direccionEnvio)) {
            // Si no hay dirección, intentar obtenerla del usuario
            if (pedido.getUsuario() != null && pedido.getUsuario().getDireccion() != null && !pedido.getUsuario().getDireccion().trim().isEmpty()) {
                direccionEnvio = pedido.getUsuario().getDireccion();
            } else {
                direccionEnvio = "No especificada";
            }
        }
        contenido.append("<p><strong>Dirección de envío:</strong> ").append(direccionEnvio).append("</p>");
        
        // Tabla de productos
        contenido.append("<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>");
        contenido.append("<tr style='background-color: #eeeeee;'>");
        contenido.append("<th style='padding: 10px; text-align: left; border-bottom: 1px solid #ddd;'>Libro</th>");
        contenido.append("<th style='padding: 10px; text-align: center; border-bottom: 1px solid #ddd;'>Cantidad</th>");
        contenido.append("<th style='padding: 10px; text-align: right; border-bottom: 1px solid #ddd;'>Precio</th>");
        contenido.append("<th style='padding: 10px; text-align: right; border-bottom: 1px solid #ddd;'>Subtotal</th>");
        contenido.append("</tr>");
        
        // Detalles de los productos
        boolean hayDetalles = false;
        if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                if (detalle.getLibro() != null) {
                    hayDetalles = true;
                    contenido.append("<tr>");
                    contenido.append("<td style='padding: 10px; border-bottom: 1px solid #ddd;'>")
                            .append(detalle.getLibro().getTitulo())
                            .append("</td>");
                    contenido.append("<td style='padding: 10px; text-align: center; border-bottom: 1px solid #ddd;'>")
                            .append(detalle.getCantidad())
                            .append("</td>");
                    contenido.append("<td style='padding: 10px; text-align: right; border-bottom: 1px solid #ddd;'>")
                            .append(formatoMoneda.format(detalle.getPrecioUnitario()))
                            .append("</td>");
                    contenido.append("<td style='padding: 10px; text-align: right; border-bottom: 1px solid #ddd;'>")
                            .append(formatoMoneda.format(detalle.getPrecioUnitario() * detalle.getCantidad()))
                            .append("</td>");
                    contenido.append("</tr>");
                } else {
                    System.out.println("Detalle con libro nulo encontrado: ID=" + detalle.getId() + ", Cantidad=" + detalle.getCantidad());
                }
            }
        } else {
            System.out.println("No hay detalles disponibles para el pedido #" + pedido.getId());
        }
        
        // Si no hay detalles, mostrar un mensaje
        if (!hayDetalles) {
            contenido.append("<tr>");
            contenido.append("<td colspan='4' style='padding: 10px; text-align: center; border-bottom: 1px solid #ddd;'>")
                    .append("No hay detalles disponibles para este pedido")
                    .append("</td>");
            contenido.append("</tr>");
        }
        
        // Total
        contenido.append("<tr>");
        contenido.append("<td colspan='3' style='padding: 10px; text-align: right; font-weight: bold;'>Total:</td>");
        contenido.append("<td style='padding: 10px; text-align: right; font-weight: bold;'>")
                .append(formatoMoneda.format(pedido.getTotal()))
                .append("</td>");
        contenido.append("</tr>");
        contenido.append("</table>");
        
        // Pie de página
        contenido.append("<div style='margin-top: 30px; padding: 20px; background-color: #f1f1f1; text-align: center;'>");
        contenido.append("<p>Gracias por comprar en Eviden Library. Si tienes alguna pregunta sobre tu pedido, ");
        contenido.append("no dudes en contactarnos.</p>");
        contenido.append("<p style='margin-top: 20px; font-size: 12px; color: #666;'>© 2025 - Eviden Library - Fernando Robla y Maria Amigo</p>");
        contenido.append("</div>");
        
        contenido.append("</div>");
        contenido.append("</body></html>");
        
        return contenido.toString();
    }
} 