package com.roarmot.roarmot.Controllers;

import com.roarmot.roarmot.Services.EmailService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java.util.Map;




@RestController
@RequestMapping("/api/email")
public class EmailController {
    
    @Autowired
    private EmailService emailService;

    // Endpoint de Prueba
    @PostMapping("/test")
    public ResponseEntity<String> enviarEmailPrueba(){
        try {
            emailService.enviarEmailSimple(
                "tu-email@gmail.com", 
                "Prueba RoarMot - Funciona!", 
                "Hola, Este es un Email de prueba dede Roarmot con spring boot 3.5.5"
            );
            return ResponseEntity.ok("Email de prueba enviado correctamente");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    // Endpoint para campaña masiva
    @PostMapping("/campaña")
    public ResponseEntity<String> enviarCampanaMasiva(@RequestBody List<String> emails) {
        try {
            emailService.enviarCorreoMasivo(
                emails, 
                "Nueva Promoción en RoarMot!"
            );
            return ResponseEntity.ok("Campaña enviada a " + emails.size() + "usuarios");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en Campañ: " + e.getMessage());
        }
    
    }

    // Endpoint para campaña HTML
    @PostMapping("/campaña-promociones")
    public ResponseEntity<String> enviarCampanaHTML(@RequestBody Map<String, Object> requestData) {
        try {
            @SuppressWarnings("unchecked")
            List<String> emails = (List<String>) requestData.get("emails");
            String templateName = (String) requestData.get("template");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = (Map<String, Object>) requestData.get("variables"); 
            
            if (variables == null) {
                variables = new HashMap<>();
            }
            
            int enviados = 0;
            for (String email : emails) {
                try {
                    // Personalizar por usuario
                    Map<String, Object> userVariables = new HashMap<>(variables);
                    userVariables.put("nombreUsuario", obtenerNombreUsuario(email)); // Puedes personalizar esto
                    userVariables.put("emailUsuario", email);
                    
                    emailService.enviarEmailHTMLPersonalizado(
                        email, 
                        "🎁 Promoción Especial RoarMot", 
                        templateName, 
                        userVariables
                    );
                    enviados++;
                    
                    Thread.sleep(1000); // Pausa entre envíos
                    
                } catch (Exception e) {
                    System.err.println("Error enviando a: " + email + " - " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok("Campaña HTML enviada a " + enviados + " usuarios");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en campaña HTML: " + e.getMessage());
        }
    }

    // Método auxiliar para obtener nombre (puedes mejorarlo)
    private String obtenerNombreUsuario(String email) {
        // Por ahora, devuelve la parte antes del @
        return email.split("@")[0];
    }
    
}
