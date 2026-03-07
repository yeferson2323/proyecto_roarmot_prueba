package com.roarmot.roarmot.dto;

import com.roarmot.roarmot.models.Producto.Talla;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) para recibir los datos del formulario de registro de un producto.
 * Se han unificado los campos para eliminar duplicados y se ha asegurado la correcta
 * recepción del archivo de imagen (MultipartFile).
 */
public class ProductoFormDTO {

    // --- Datos Principales ---
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 45, message = "El nombre no debe exceder los 45 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no debe exceder los 50 caracteres")
    private String marca;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    private BigDecimal precio; // Usa BigDecimal para precisión monetaria

    // --- Inventario y Clasificación ---
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer stock;

    @NotBlank(message = "El lote/SKU es obligatorio")
    @Size(max = 15, message = "El lote no debe exceder los 15 caracteres")
    private String lote;

    @NotNull(message = "La talla es obligatoria")
    private Talla talla; // Asume que Producto.Talla es un Enum

    // ID de la Subcategoría (Unificado a Long)
    @NotNull(message = "La subcategoría es obligatoria")
    private Long idSubcategoria;

    // Campo especial para la IMAGEN (nombre 'imagen' para que coincida con getImagen())
    @NotNull(message = "La imagen principal es obligatoria")
    private MultipartFile imagen;

    
    // --- Getters y Setters ---

    // Getter que resuelve el error en tu Controller/Service
    public MultipartFile getImagen() {
        return imagen;
    }

    public void setImagen(MultipartFile imagen) {
        this.imagen = imagen;
    }

    // Getter/Setter para idSubcategoria
    public Long getIdSubcategoria() {
        return idSubcategoria;
    }

    public void setIdSubcategoria(Long idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
    }
    
    // El resto de Getters y Setters...
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    
    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    
}
