package com.roarmot.roarmot.Services;

import com.roarmot.roarmot.repositories.ProductoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class ReporteProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public byte[] generarReporteProductosPorMarca() {
        try {
            // 1. Cargar plantilla .jrxml
            InputStream reportStream = getClass()
                .getResourceAsStream("/report/reporte_estadisticas_productos.jrxml");
            
            // 2. Compilar reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            
            // 3. Obtener datos de BD - Productos por MARCA
            List<Object[]> datosBD = productoRepository.contarProductosPorMarca();
            List<Map<String, Object>> datosReporte = new ArrayList<>();
            
            for (Object[] fila : datosBD) {
                Map<String, Object> dato = new HashMap<>();
                Long countLong = (Long) fila[1];
                dato.put("categoria", fila[0]);
                dato.put("total", countLong.intValue()); // ← Convertir a Integer
                dato.put("etiqueta", "Productos " + fila[0]);
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
            throw new RuntimeException("Error generando reporte de productos", e);
        }
    }
    
    // MÉTODO ADICIONAL: Productos por TALLA
    public byte[] generarReporteProductosPorTalla() {
        try {
            System.out.println("=== PASO 1: Buscando archivo .jrxml ===");
            InputStream reportStream = getClass()
                .getResourceAsStream("/report/reporte_estadisticas_productos.jrxml");
            
            if (reportStream == null) {
                System.err.println("❌ ERROR: Archivo NO encontrado");
                throw new RuntimeException("Archivo no encontrado");
            }
            System.out.println("✅ PASO 1: Archivo encontrado");

            System.out.println("=== PASO 2: Compilando reporte ===");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            System.out.println("✅ PASO 2: Reporte compilado");

            System.out.println("=== PASO 3: Obteniendo datos ===");
            List<Object[]> datosBD = productoRepository.contarProductosPorTalla();
            System.out.println("✅ PASO 3: Datos obtenidos - " + datosBD.size() + " registros");

            // DEBUG: Mostrar los datos
            for (int i = 0; i < datosBD.size(); i++) {
                Object[] fila = datosBD.get(i);
                System.out.println("Dato " + i + ": " + Arrays.toString(fila));
            }

            System.out.println("=== PASO 4: Preparando datos para Jasper ===");
            List<Map<String, Object>> datosReporte = new ArrayList<>();
            
            for (Object[] fila : datosBD) {
                Map<String, Object> dato = new HashMap<>();
                
                // CONVERTIR Long a Integer
                Long countLong = (Long) fila[1];
                Integer countInteger = countLong.intValue();
                
                dato.put("categoria", "Talla " + fila[0]);
                dato.put("total", countInteger); // ← Usar Integer en lugar de Long
                dato.put("etiqueta", "Talla " + fila[0]);
                datosReporte.add(dato);
            }
            System.out.println("✅ PASO 4: Datos preparados");

            System.out.println("=== PASO 5: Creando datasource ===");
            JRDataSource dataSource = new JRBeanCollectionDataSource(datosReporte);
            System.out.println("✅ PASO 5: Datasource creado");

            System.out.println("=== PASO 6: Configurando parámetros ===");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaGeneracion", new Date());
            System.out.println("✅ PASO 6: Parámetros configurados");

            System.out.println("=== PASO 7: Generando JasperPrint ===");
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, parametros, dataSource);
            System.out.println("✅ PASO 7: JasperPrint generado");

            System.out.println("=== PASO 8: Exportando a PDF ===");
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            System.out.println("✅ PASO 8: PDF exportado - " + pdfBytes.length + " bytes");

            return pdfBytes;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO en generarReporteProductosPorTalla:");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Tipo: " + e.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException("Error generando reporte por talla: " + e.getMessage(), e);
        }
    }

    // MÉTODO ADICIONAL: Productos por subcategoria
    public byte[] generarReporteProductosPorCategoria() {
        try {
            InputStream reportStream = getClass()
                .getResourceAsStream("/report/reporte_estadisticas_productos.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            
            // Usar consulta de SUBCATEGORÍAS
            List<Object[]> datosBD = productoRepository.contarProductosPorSubcategoria();
            List<Map<String, Object>> datosReporte = new ArrayList<>();
            
            for (Object[] fila : datosBD) {
                Map<String, Object> dato = new HashMap<>();
                Long countLong = (Long) fila[1];
                dato.put("categoria", fila[0]);
                dato.put("total", countLong.intValue()); // ← Convertir a Integer
                dato.put("etiqueta", fila[0] + " - " + countLong + " productos");
                datosReporte.add(dato);
            }
            
            JRDataSource dataSource = new JRBeanCollectionDataSource(datosReporte);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaGeneracion", new Date());
            parametros.put("tituloReporte", "Productos por Subcategoría"); // Cambiar título
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, parametros, dataSource);
            
            return JasperExportManager.exportReportToPdf(jasperPrint);
            
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte por subcategoría", e);
        }
    }
}