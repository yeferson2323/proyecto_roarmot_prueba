package com.roarmot.roarmot.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // 1. Email simple para pruebas
    public void enviarEmailSimple(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("notificaciones@roarmot.com");
        
        mailSender.send(message);
    }

    // 2. Email HTML con Thymeleaf
    public void enviarEmailHTML(String to, String subject, String templateName) 
            throws MessagingException {
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // Contexto para Thymeleaf
        Context context = new Context();
        context.setVariable("nombreUsuario", "Cliente RoarMot");
        context.setVariable("producto", "Casco Modular de Seguridad");
        context.setVariable("descuento", "30%");
        
        String htmlContent = templateEngine.process(templateName, context);
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML
        helper.setFrom("promociones@roarmot.com");
        
        mailSender.send(message);
    }

    // 3. Correo masivo
    public void enviarCorreoMasivo(List<String> destinatarios, String subject) {
        int enviados = 0;
        int errores = 0;
        
        for (String email : destinatarios) {
            try {
                enviarEmailSimple(email, subject, "¡Promoción especial en RoarMot!");
                enviados++;
                
                // Pausa de 1 segundo entre envíos
                Thread.sleep(1000);
                
            } catch (Exception e) {
                errores++;
                System.err.println("Error enviando a: " + email + " - " + e.getMessage());
            }
        }
        
        System.out.println("Envío masivo completado:");
        System.out.println("Enviados: " + enviados);
        System.out.println("Errores: " + errores);
    }

    // 4. Email HTML con plantilla personalizada y variables dinámicas
    public void enviarEmailHTMLPersonalizado(String to, String subject, String templateName, 
                                            Map<String, Object> variables) throws MessagingException {
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // Contexto para Thymeleaf con variables dinámicas
        Context context = new Context();
        
        // Agregar todas las variables al contexto
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        
        // Variables por defecto si no se proporcionan
        if (!variables.containsKey("nombreUsuario")) {
            context.setVariable("nombreUsuario", "Cliente RoarMot");
        }
        if (!variables.containsKey("urlDesuscribir")) {
            context.setVariable("urlDesuscribir", "https://roarmot.com/unsuscribe");
        }
        
        String htmlContent = templateEngine.process("emails/" + templateName, context);
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML
        helper.setFrom("promociones@roarmot.com");
        
        mailSender.send(message);
    }

    // 5. Correo masivo HTML
    public void enviarCorreoMasivoHTML(List<String> destinatarios, String subject, 
                                      String templateName, Map<String, Object> variables) {
        int enviados = 0;
        int errores = 0;
        
        for (String email : destinatarios) {
            try {
                // Personalizar variables por usuario
                Map<String, Object> userVariables = new HashMap<>(variables);
                userVariables.put("emailUsuario", email);
                
                // Si no hay nombre específico, usar parte del email
                if (!userVariables.containsKey("nombreUsuario")) {
                    String nombre = email.split("@")[0];
                    userVariables.put("nombreUsuario", nombre);
                }
                
                enviarEmailHTMLPersonalizado(email, subject, templateName, userVariables);
                enviados++;
                
                // Pausa de 1 segundo entre envíos
                Thread.sleep(1000);
                
            } catch (Exception e) {
                errores++;
                System.err.println("Error enviando HTML a: " + email + " - " + e.getMessage());
            }
        }
        
        System.out.println("Envío masivo HTML completado:");
        System.out.println("Enviados: " + enviados);
        System.out.println("Errores: " + errores);
    }
}