package com.roarmot.roarmot.models;

import jakarta.persistence.*;

/**
 * Esta clase es una entidad JPA que mapea la tabla 'subcategorias'.
 * Representa una subcategoría que pertenece a una categoría principal.
 */
@Entity
@Table(name = "subcategorias") // Nombre de la tabla en minúsculas
public class Subcategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") // Mapea el campo 'id' a la columna 'ID' en la base de datos
    private Long id;

    @Column(name = "NOMBRE") // Mapea el campo 'nombre' a la columna 'NOMBRE'
    private String nombre;

    /**
     * Mapea la relación Many-to-One con la entidad Categoria.
     * Muchas subcategorías pueden pertenecer a una sola categoría.
     * La anotación @JoinColumn indica la clave foránea en la tabla 'subcategorias'
     * que apunta a la clave primaria en la tabla 'categorias'.
     */
    @ManyToOne // Indica que hay una relación de "muchos a uno".
    @JoinColumn(name = "ID_CATEGORIA") // La columna de la clave foránea en la tabla 'subcategorias' es 'ID_CATEGORIA'
    private Categoria categoria; // La referencia al objeto Categoria al que pertenece esta subcategoría.

    // Constructor por defecto
    public Subcategoria() {
    }

    // Constructor con parámetros
    public Subcategoria(String nombre, Categoria categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
