package com.roarmot.roarmot.Services;

import com.roarmot.roarmot.repositories.MotoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class ReporteService {
    
    @Autowired
    private MotoRepository motoRepository;
    
    public byte[] generarReporteEstadisticasMotos() {
        try {
            // 1. Cargar plantilla .jrxml
            InputStream reportStream = getClass()
                .getResourceAsStream("/report/estadisticas_motos.jrxml");
            
            // 2. Compilar reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            
            // 3. Obtener datos de BD y convertir al formato que espera el reporte
            List<Object[]> datosBD = motoRepository.contarMotosPorMarca();
            List<Map<String, Object>> datosReporte = new ArrayList<>();
            
            for (Object[] fila : datosBD) {
                Map<String, Object> dato = new HashMap<>();
                dato.put("marca", fila[0]);      // marcaMoto
                dato.put("total", fila[1]);      // COUNT
                dato.put("etiqueta", "Motos " + fila[0]);
                datosReporte.add(dato);
            }
            
            // 4. Crear datasource para Jasper
            JRDataSource dataSource = new JRBeanCollectionDataSource(datosReporte);
            
            // 5. Parámetros del reporte
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaGeneracion", new Date());
            
            // 6. Generar PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, parametros, dataSource);
            
            return JasperExportManager.exportReportToPdf(jasperPrint);
            
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte", e);
        }
    }
}