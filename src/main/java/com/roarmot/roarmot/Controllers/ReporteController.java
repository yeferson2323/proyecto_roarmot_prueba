package com.roarmot.roarmot.Controllers;

import com.roarmot.roarmot.Services.ReporteProductoService;
import com.roarmot.roarmot.Services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;



@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    
    @Autowired
    private ReporteService reporteService;

    @Autowired
    private ReporteProductoService reporteProductoService;
    
    @GetMapping("/estadisticas-motos")
    public ResponseEntity<byte[]> descargarReporteMotos() {
        try {
            byte[] pdfBytes = reporteService.generarReporteEstadisticasMotos();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "estadisticas_motos_" + LocalDate.now() + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /* GENERAMOS REPORTES ESTADÍSTICOS PARA PRODUCTOS */

    @GetMapping("/productos-por-marca")
    public ResponseEntity<byte[]> descargarReporteProductosMarca() {
        try {
            byte[] pdfBytes = reporteProductoService.generarReporteProductosPorMarca();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "productos_por_marca_" + LocalDate.now() + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/productos-por-talla")  
    public ResponseEntity<byte[]> descargarReporteProductosTalla() {
        try {
            byte[] pdfBytes = reporteProductoService.generarReporteProductosPorTalla();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "productos_por_talla_" + LocalDate.now() + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/productos-por-subcategoria")  
    public ResponseEntity<byte[]> descargarReporteProductosCategoria() {
        try {
            byte[] pdfBytes = reporteProductoService.generarReporteProductosPorCategoria();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "productos_por_subcategoria_" + LocalDate.now() + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}