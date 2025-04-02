package com.evidenlibrary.backend.apirest.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import com.evidenlibrary.backend.apirest.model.entity.DetallePedido;
import com.evidenlibrary.backend.apirest.model.entity.Libro;
import com.evidenlibrary.backend.apirest.model.entity.Pedido;
import com.evidenlibrary.backend.apirest.model.entity.Usuario;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    
    @InjectMocks
    private EmailServiceImpl emailService;
    
    private Pedido pedido;
    private Usuario usuario;
    private List<DetallePedido> detalles;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@example.com");
        
        pedido = new Pedido();
        pedido.setFechaPedido(new Date());
        pedido.setTotal(99.98);
        pedido.setEstado("PENDIENTE");
        pedido.setDireccionEnvio("Calle Test 123");
        pedido.setUsuario(usuario);
        
        // Configurar detalles del pedido
        detalles = new ArrayList<>();
        
        Libro libro1 = new Libro();
        libro1.setTitulo("El Quijote");
        libro1.setPrecio(49.99);
        
        Libro libro2 = new Libro();
        libro2.setTitulo("Cien años de soledad");
        libro2.setPrecio(49.99);
        
        DetallePedido detalle1 = new DetallePedido();
        detalle1.setLibro(libro1);
        detalle1.setCantidad(1);
        detalle1.setPrecioUnitario(49.99);
        detalle1.setPedido(pedido);
        
        DetallePedido detalle2 = new DetallePedido();
        detalle2.setLibro(libro2);
        detalle2.setCantidad(1);
        detalle2.setPrecioUnitario(49.99);
        detalle2.setPedido(pedido);
        
        detalles.add(detalle1);
        detalles.add(detalle2);
        
        pedido.setDetalles(detalles);
        
        // Mock del mensaje MIME
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session)null));
    }
    
    @Test
    void enviarEmailConfirmacionPedido_EnvioExitoso_RetornaTrue() {
        // Configurar el mock para que el envío sea exitoso
        doNothing().when(mailSender).send(any(MimeMessage.class));
        
        // Ejecutar método a probar
        boolean resultado = emailService.enviarEmailConfirmacionPedido(pedido, "juan.perez@example.com");
        
        // Verificar
        assertTrue(resultado);
        
        // Verificar que se llamaron los métodos correctos
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
    
    @Test
    void enviarEmailConfirmacionPedido_ErrorAlEnviar_RetornaFalse() {
        // Configurar el mock para que el envío falle
        doThrow(new RuntimeException("Error al enviar")).when(mailSender).send(any(MimeMessage.class));
        
        // Ejecutar método a probar
        boolean resultado = emailService.enviarEmailConfirmacionPedido(pedido, "juan.perez@example.com");
        
        // Verificar
        assertFalse(resultado);
        
        // Verificar que se llamaron los métodos correctos
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
    
    @Test
    void enviarEmailConfirmacionPedido_PedidoSinDetalles_RetornaTrue() {
        // Configurar pedido sin detalles
        pedido.setDetalles(new ArrayList<>());
        
        // Configurar el mock para que el envío sea exitoso
        doNothing().when(mailSender).send(any(MimeMessage.class));
        
        // Ejecutar método a probar
        boolean resultado = emailService.enviarEmailConfirmacionPedido(pedido, "juan.perez@example.com");
        
        // Verificar
        assertTrue(resultado);
        
        // Verificar que se llamaron los métodos correctos
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
    
    @Test
    void enviarEmailConfirmacionPedido_PedidoConDetallesSinLibro_RetornaTrue() {
        // Configurar detalles sin libro
        for (DetallePedido detalle : detalles) {
            detalle.setLibro(null);
        }
        
        // Configurar el mock para que el envío sea exitoso
        doNothing().when(mailSender).send(any(MimeMessage.class));
        
        // Ejecutar método a probar
        boolean resultado = emailService.enviarEmailConfirmacionPedido(pedido, "juan.perez@example.com");
        
        // Verificar
        assertTrue(resultado);
        
        // Verificar que se llamaron los métodos correctos
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
} 