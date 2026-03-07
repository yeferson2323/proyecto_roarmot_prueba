package com.roarmot.roarmot.models;

import jakarta.persistence.*;
import java.math.BigDecimal; // Necesario para el tipo de dato decimal
import java.util.Date; // Necesario para tipos de datos de fecha, si los tuvieras

import com.roarmot.roarmot.models.Usuario; // conectamos con el modelo Usurio

/**
 * Esta clase es una entidad JPA que mapea la tabla 'producto'.
 * Representa un producto en el inventario.
 */
@Entity
@Table(name = "producto") // Nombre de la tabla en minúsculas
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") // Mapea el campo 'id' a la columna 'ID'
    private Long id;

    @Column(name = "NOMBRE") // Mapea el campo 'nombre' a la columna 'NOMBRE'
    private String nombre;

    @Column(name = "DESCRIPCION") // Mapea el campo 'descripcion' a la columna 'DESCRIPCION'
    private String descripcion;

    @Column(name = "MARCA") // Mapea el campo 'marca' a la columna 'MARCA'
    private String marca;

    @Column(name = "IMAGEN") // Mapea el campo 'imagen' a la columna 'IMAGEN'
    private String imagen;

    @Enumerated(EnumType.STRING)
    @Column(name = "TALLA") // Mapea el campo 'talla' a la columna 'TALLA'
    private Talla talla; // Usaremos un Enum para manejar las tallas

    @Column(name = "LOTE") // Mapea el campo 'lote' a la columna 'LOTE'
    private String lote;

    @Column(name = "CANTIDAD") // Mapea el campo 'cantidad' a la columna 'CANTIDAD'
    private Integer cantidad;

    @Column(name = "PRECIO") // Mapea el campo 'precio' a la columna 'PRECIO'
    private Double precio;

    /**
     * Mapea la relación Many-to-One con la entidad Subcategoria.
     * La columna de la clave foránea en la tabla 'producto' es 'ID_SUBCATEGORIA'.
     */
    @ManyToOne
    @JoinColumn(name = "ID_SUBCATEGORIA")
    private Subcategoria subcategoria;

    /**
     * Mapea la relación Many-to-One con la entidad Usuario.
     * La columna de la clave foránea en la tabla 'producto' es 'ID_USUARIO'.
     */
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario; // aquí hacemos referencia a la clase usuario

    // Puedes crear un Enum para las tallas, lo que es una buena práctica.
    public enum Talla {
        S, L, M, XS, XL, NO_APLICA
    }

    // Constructor por defecto
    public Producto() {
    }

    // Constructor con parámetros
    public Producto(String nombre, String descripcion, String marca, String imagen, Talla talla, String lote, Integer cantidad, Double precio, Subcategoria subcategoria, Usuario usuario) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.imagen = imagen;
        this.talla = talla;
        this.lote = lote;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subcategoria = subcategoria;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Subcategoria getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    
}
