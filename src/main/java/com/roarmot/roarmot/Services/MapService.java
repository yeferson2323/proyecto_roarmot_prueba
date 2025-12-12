package com.roarmot.roarmot.Services;

import java.util.List;
import java.util.Map; // Usaremos Map para representar los resultados del servicio

public interface MapService {
    // Busca servicios cercanos y devuelve una lista de resultados mapeados
    List<Map<String, Object>> findNearbyServices(double lat, double lng, String keyword);
}