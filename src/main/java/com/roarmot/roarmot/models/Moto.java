package com.roarmot.roarmot.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "moto")
public class Moto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DATOSMOTO")
    private Long idDatosMoto;
    
    @Column(name = "MARCA_MOTO", length = 25, nullable = false)
    private String marcaMoto;
    
    @Column(name = "MODELO_MOTO", length = 50, nullable = false)
    private String modeloMoto;
    
    @Column(name = "COLOR_MOTO", length = 30, nullable = false)
    private String colorMoto;
    
    @Column(name = "IMAGEN_MOTO", length = 255)
    private String imagenMoto;
    
    @Column(name = "SOAT_MOTO", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date soatMoto;
    
    @Column(name = "TECNOMECANICA", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date tecnomecanica;
    
    @Column(name = "PLACA_MOTO", length = 45, nullable = false)
    private String placaMoto;
    
    @Column(name = "KILOMETRAJE", nullable = false)
    private Integer kilometraje;
    
    // RELACIÓN CORREGIDA - ManyToOne con objeto Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_ID_USUARIO", nullable = false)
    private Usuario usuario;

    // Constructores
    public Moto() {
    }

    // Constructor corregido
    public Moto(String marcaMoto, String modeloMoto, String colorMoto, 
                String imagenMoto, Date soatMoto, Date tecnomecanica, 
                String placaMoto, Integer kilometraje, Usuario usuario) {
        this.marcaMoto = marcaMoto;
        this.modeloMoto = modeloMoto;
        this.colorMoto = colorMoto;
        this.imagenMoto = imagenMoto;
        this.soatMoto = soatMoto;
        this.tecnomecanica = tecnomecanica;
        this.placaMoto = placaMoto;
        this.kilometraje = kilometraje;
        this.usuario = usuario;
    }

    // Getters y Setters (mantén todos los que tienes y agrega)
    public Long getIdDatosMoto() {
        return idDatosMoto;
    }

    public void setIdDatosMoto(Long idDatosMoto) {
        this.idDatosMoto = idDatosMoto;
    }

    public String getMarcaMoto() {
        return marcaMoto;
    }

    public void setMarcaMoto(String marcaMoto) {
        this.marcaMoto = marcaMoto;
    }

    public String getModeloMoto() {
        return modeloMoto;
    }

    public void setModeloMoto(String modeloMoto) {
        this.modeloMoto = modeloMoto;
    }

    public String getColorMoto() {
        return colorMoto;
    }

    public void setColorMoto(String colorMoto) {
        this.colorMoto = colorMoto;
    }

    public String getImagenMoto() {
        return imagenMoto;
    }

    public void setImagenMoto(String imagenMoto) {
        this.imagenMoto = imagenMoto;
    }

    public Date getSoatMoto() {
        return soatMoto;
    }

    public void setSoatMoto(Date soatMoto) {
        this.soatMoto = soatMoto;
    }

    public Date getTecnomecanica() {
        return tecnomecanica;
    }

    public void setTecnomecanica(Date tecnomecanica) {
        this.tecnomecanica = tecnomecanica;
    }

    public String getPlacaMoto() {
        return placaMoto;
    }

    public void setPlacaMoto(String placaMoto) {
        this.placaMoto = placaMoto;
    }

    public Integer getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(Integer kilometraje) {
        this.kilometraje = kilometraje;
    }

    // GETTER Y SETTER CORREGIDOS para usuario
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}