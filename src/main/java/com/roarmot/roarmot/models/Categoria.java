package com.roarmot.roarmot.models;

import jakarta.persistence.*;

/**
 * Esta clase es una entidad JPA que mapea la tabla 'CATEGORIAS' de la base de datos.
 * Cada instancia de esta clase representa una fila en esa tabla.
 */
@Entity
@Table(name = "categorias") // Nombre de la tabla debe ser igual
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") // Mapea el campo 'id' a la columna 'ID'
    private Long id;

    @Column(name = "NOMBRE") // Mapea el campo 'nombre' a la columna 'NOMBRE'
    private String nombre;

    /**
     * Constructor por defecto. Es necesario para que JPA pueda crear instancias de la entidad.
     */
    public Categoria() {
    }

    /**
     * Constructor con parámetros. Útil para crear nuevas instancias de Categoria de forma conveniente.
     */
    public Categoria(String nombre) {
        this.nombre = nombre;
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
}
