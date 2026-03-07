package com.roarmot.roarmot.util;

import com.roarmot.roarmot.models.Producto;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Clase utilitaria para generar archivos Excel (XLSX) a partir de una lista de productos.
 */
public class ExcelGenerator {

    private List<Producto> listaProductos;
    private XSSFWorkbook workbook;
    private Sheet sheet;

    // Encabezados de las columnas. Nota: Ya no incluye 'Fecha de Registro'
    private static final String[] HEADERS = {
        "ID", "Nombre", "Marca", "Cantidad", "Precio", "Fecha Descarga Reporte" 
    };

    /**
     * Constructor que inicializa el generador con la lista de productos.
     * @param listaProductos La lista de objetos Producto a exportar.
     */
    public ExcelGenerator(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
        this.workbook = new XSSFWorkbook();
    }

    /**
     * Crea la hoja de trabajo con los estilos y encabezados.
     */
    private void createSheet() {
        sheet = workbook.createSheet("Productos de Vendedor");
        sheet.setDefaultColumnWidth(20);
        
        // Estilo para el encabezado
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Crear la fila de encabezados
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Escribe las filas de datos de los productos.
     */
    private void writeDataLines() {
        // Estilo para los datos normales
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);

        // Formateador para la fecha actual
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaDescarga = LocalDate.now().format(dateFormatter);

        int rowCount = 1; // Empezamos después del encabezado (fila 0)

        for (Producto producto : listaProductos) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            // 1. ID (Corregido a getId() según Producto.java)
            createCell(row, columnCount++, producto.getId(), style); 
            
            // 2. Nombre
            createCell(row, columnCount++, producto.getNombre(), style);
            
            // 3. Marca
            createCell(row, columnCount++, producto.getMarca(), style);
            
            // 4. Cantidad
            createCell(row, columnCount++, producto.getCantidad(), style);
            
            // 5. Precio
            // Usamos String.valueOf para evitar problemas con Double o BigDecimal
            createCell(row, columnCount++, String.valueOf(producto.getPrecio()), style); 
            
            // 6. Fecha de Descarga (Fecha actual solicitada)
            createCell(row, columnCount++, fechaDescarga, style);
        }
        
        // Ajustar automáticamente el ancho de las columnas
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Método auxiliar para crear celdas con datos de diferentes tipos.
     */
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        cell.setCellStyle(style);

        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
             cell.setCellValue((Double) value);
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }

    /**
     * Genera el archivo Excel completo y lo escribe en el flujo de respuesta HTTP.
     * @param response El objeto HttpServletResponse para la descarga del archivo.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public void export(HttpServletResponse response) throws IOException {
        createSheet();
        writeDataLines();
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
