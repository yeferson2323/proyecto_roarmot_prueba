package com.roarmot.roarmot.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder; // <-- ¡IMPORTACIÓN NECESARIA!
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Indica a Spring que esta clase es un servicio inyectable
@Service
public class MapServiceImpl implements MapService {

    // URL base de la API y clave inyectadas desde application.properties
    @Value("${google.maps.api.key}")
    private String apiKey;
    
    @Value("${google.maps.api.url}")
    private String apiUrl; // Debe ser: https://maps.googleapis.com/maps/api/place/nearbysearch/json

    private final WebClient webClient;
    private final ObjectMapper objectMapper; 

    // Constructor para inyección de dependencias
    public MapServiceImpl(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Map<String, Object>> findNearbyServices(double lat, double lng, String keyword) {
        
        final int radius = 5000; // 5 km de radio de búsqueda
        String location = lat + "," + lng;

        // --- LÓGICA DE BÚSQUEDA CORREGIDA Y MEJORADA ---
        String type = getTypeFromKeyword(keyword); 

        // 1. Usamos UriComponentsBuilder para CONSTRUIR la URI limpiamente (CORRECCIÓN DEL ERROR DE COMPILACIÓN)
        String requestUri = UriComponentsBuilder.fromHttpUrl(apiUrl)
            .queryParam("location", location)
            .queryParam("radius", radius)
            .queryParam("key", apiKey)
            // Lógica para usar 'type' (si está mapeado) o 'keyword' (si no lo está)
            .queryParam(type.isEmpty() ? "keyword" : "type", type.isEmpty() ? keyword : type)
            .encode()
            .toUriString(); 
        
        // 2. Log de Depuración (IMPRIMIRÁ LA URL COMPLETA)
        System.out.println("DEBUG: Solicitando a Google Places con URI: " + requestUri); 
        // ---------------------------------------------------

        try {
            // 3. Llama a la API de Google Places (usando la URI ya construida)
            String jsonResponse = webClient.get()
                .uri(requestUri) 
                .retrieve()
                .bodyToMono(String.class)
                .block(); 

            if (jsonResponse == null) {
                return new ArrayList<>();
            }
            
            // Procesa el JSON y extrae los resultados
            return processGooglePlacesResponse(jsonResponse, lat, lng);

        } catch (Exception e) {
            System.err.println("Error al llamar a la API de Google Places: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para mapear el keyword a un type de Google Places (Paso 2)
    private String getTypeFromKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }
        String lowerCaseKeyword = keyword.toLowerCase();

        if (lowerCaseKeyword.contains("moto repair") || lowerCaseKeyword.contains("taller")) {
            return "car_repair"; 
        } else if (lowerCaseKeyword.contains("gas station") || lowerCaseKeyword.contains("gasolinera")) {
            return "gas_station"; 
        }
        // Si no se mapea, devuelve vacío
        return "";
    }

    // Método para parsear la respuesta JSON de Google
    private List<Map<String, Object>> processGooglePlacesResponse(String jsonResponse, double userLat, double userLng) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode resultsNode = rootNode.path("results");
        
        List<Map<String, Object>> cleanResults = new ArrayList<>();

        if (resultsNode.isArray()) {
            for (JsonNode node : resultsNode) {
                Map<String, Object> service = new HashMap<>();
                
                service.put("nombre", node.path("name").asText());
                service.put("direccion", node.path("vicinity").asText());
                
                JsonNode locationNode = node.path("geometry").path("location");
                double placeLat = locationNode.path("lat").asDouble();
                double placeLng = locationNode.path("lng").asDouble();
                
                // Agregamos las coordenadas para que el JavaScript pueda poner el marcador
                service.put("lat", placeLat); 
                service.put("lng", placeLng); 
                
                // Calcula la distancia (función de apoyo)
                double distance = calculateDistance(userLat, userLng, placeLat, placeLng);
                service.put("distancia", String.format("%.2f", distance)); 

                cleanResults.add(service);
            }
        }
        return cleanResults;
    }

    // Implementación de la fórmula Haversine simplificada
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en kilómetros

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                     + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                     * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distancia en km
    }
}