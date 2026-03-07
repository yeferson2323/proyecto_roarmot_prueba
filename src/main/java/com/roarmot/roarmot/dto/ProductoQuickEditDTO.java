package com.roarmot.roarmot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO utilizado exclusivamente para la edición rápida de productos 
 * en la tabla del vendedor (vía AJAX). Contiene solo los campos 
 * que son visibles y editables en la vista.
 *
 * NOTA: Los campos 'stock' y 'precio' usan validaciones mínimas para evitar errores lógicos.
 */
public class ProductoQuickEditDTO {

    // Identificador único del producto (Clave primaria para la edición)
    @NotNull(message = "El ID del producto es obligatorio.")
    private Long idProducto;

    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "La marca no puede estar vacía.")
    private String marca;

    // Utilizamos 'stock' para coincidir con el modelo de Producto, 
    // aunque en la vista se muestre como 'cantidad'.
    @NotNull(message = "La cantidad (stock) no puede ser nula.")
    @Min(value = 0, message = "El stock debe ser 0 o superior.")
    private Integer stock;

    @NotNull(message = "El precio no puede ser nulo.")
    @DecimalMin(value = "0.01", message = "El precio debe ser superior a 0.")
    private Double precio;

    // --- Constructor sin argumentos ---
    public ProductoQuickEditDTO() {
    }

    // --- Getters y Setters ---

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    // Opcional: toString para debugging
    @Override
    public String toString() {
        return "ProductoQuickEditDTO{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", stock=" + stock +
                ", precio=" + precio +
                '}';
    }
}
