package com.roarmot.roarmot.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
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

    // Se vuelve opcional para que la app no falle si no hay configuración de correo
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private TemplateEngine templateEngine;

    // Método para verificar si el correo está configurado
    private boolean correoDisponible() {
        return mailSender != null;
    }

    // 1. Email simple
    public void enviarEmailSimple(String to, String subject, String text) {

        if (!correoDisponible()) {
            System.out.println("Servicio de correo no configurado. Email no enviado.");
            return;
        }

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

        if (!correoDisponible() || templateEngine == null) {
            System.out.println("Servicio de correo o TemplateEngine no configurado.");
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariable("nombreUsuario", "Cliente RoarMot");
        context.setVariable("producto", "Casco Modular de Seguridad");
        context.setVariable("descuento", "30%");

        String htmlContent = templateEngine.process(templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("promociones@roarmot.com");

        mailSender.send(message);
    }

    // 3. Correo masivo simple
    public void enviarCorreoMasivo(List<String> destinatarios, String subject) {

        if (!correoDisponible()) {
            System.out.println("Servicio de correo no configurado.");
            return;
        }

        int enviados = 0;
        int errores = 0;

        for (String email : destinatarios) {
            try {
                enviarEmailSimple(email, subject, "¡Promoción especial en RoarMot!");
                enviados++;
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

    // 4. Email HTML personalizado
    public void enviarEmailHTMLPersonalizado(String to, String subject, String templateName,
                                             Map<String, Object> variables) throws MessagingException {

        if (!correoDisponible() || templateEngine == null) {
            System.out.println("Servicio de correo o TemplateEngine no configurado.");
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();

        if (variables != null) {
            variables.forEach(context::setVariable);
        }

        if (variables == null || !variables.containsKey("nombreUsuario")) {
            context.setVariable("nombreUsuario", "Cliente RoarMot");
        }

        context.setVariable("urlDesuscribir", "https://roarmot.com/unsubscribe");

        String htmlContent = templateEngine.process("emails/" + templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("promociones@roarmot.com");

        mailSender.send(message);
    }

    // 5. Correo masivo HTML
    public void enviarCorreoMasivoHTML(List<String> destinatarios, String subject,
                                       String templateName, Map<String, Object> variables) {

        if (!correoDisponible()) {
            System.out.println("Servicio de correo no configurado.");
            return;
        }

        int enviados = 0;
        int errores = 0;

        for (String email : destinatarios) {
            try {

                Map<String, Object> userVariables = new HashMap<>(variables);
                userVariables.put("emailUsuario", email);

                if (!userVariables.containsKey("nombreUsuario")) {
                    String nombre = email.split("@")[0];
                    userVariables.put("nombreUsuario", nombre);
                }

                enviarEmailHTMLPersonalizado(email, subject, templateName, userVariables);
                enviados++;

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