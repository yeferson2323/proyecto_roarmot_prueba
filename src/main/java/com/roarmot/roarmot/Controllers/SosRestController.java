package com.roarmot.roarmot.Controllers;

import com.roarmot.roarmot.Services.MapService;
import com.roarmot.roarmot.dto.SosDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sos") // Ruta base para todos los endpoints de SOS API
public class SosRestController {

    private final MapService mapService;

    // Inyección de dependencia del MapService
    public SosRestController(MapService mapService) {
        this.mapService = mapService;
    }

    /**
     * Endpoint que recibe las coordenadas del frontend y delega la búsqueda de servicios.
     * URL: /api/sos/buscar-servicios
     */
    @PostMapping("/buscar-servicios")
    public ResponseEntity<Map<String, Object>> buscarServiciosCerca(@RequestBody SosDTO request) {
        
        // Validar datos de entrada básicos
        if (request.getKeyword() == null || request.getKeyword().isEmpty()) {
             return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Keyword de búsqueda faltante."));
        }

        try {
            // 1. Delegar la lógica de negocio al MapService
            List<Map<String, Object>> results = mapService.findNearbyServices(
                request.getLatitude(),
                request.getLongitude(),
                request.getKeyword()
            );

            // 2. Construir la respuesta JSON para el frontend
            Map<String, Object> response = Map.of(
                "status", "success",
                "results", results,
                "count", results.size()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error al procesar la búsqueda de servicios SOS: " + e.getMessage());
            
            // Devolver un error 500 si hay un fallo en la llamada a la API externa o en el servicio
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "Error interno al buscar servicios."));
        }
    }
}