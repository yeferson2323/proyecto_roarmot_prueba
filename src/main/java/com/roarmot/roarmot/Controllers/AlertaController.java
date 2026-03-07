package com.roarmot.roarmot.Controllers;

import com.roarmot.roarmot.models.Alerta;
import com.roarmot.roarmot.Services.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen
public class AlertaController {
    
    @Autowired
    private AlertaService alertaService;
    
    // Endpoint básico para obtener alertas
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Alerta>> getAlertasUsuario(@PathVariable Long usuarioId) {
        try {
            List<Alerta> alertas = alertaService.getAlertasByUsuarioId(usuarioId);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Endpoint para dashboard (solo alertas activas)
    @GetMapping("/dashboard/{usuarioId}")
    public ResponseEntity<List<Alerta>> getAlertasDashboard(@PathVariable Long usuarioId) {
        try {
            List<Alerta> alertas = alertaService.getAlertasActivasByUsuarioId(usuarioId);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
