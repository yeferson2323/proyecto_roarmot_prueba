package com.roarmot.roarmot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class MotoDTO {
    
    private Long idDatosMoto; 
    private String marcaMoto;
    private String modeloMoto;
    private String colorMoto;
    private String imagenMoto;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date soatMoto;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date tecnomecanica;
    
    private String placaMoto;
    private Integer kilometraje;

    // Constructor vacío
    public MotoDTO() {
    }

    // Constructor con parámetros (Actualizado para incluir idDatosMoto)
    public MotoDTO(Long idDatosMoto, String marcaMoto, String modeloMoto, String colorMoto,
                   String imagenMoto, Date soatMoto, Date tecnomecanica,
                   String placaMoto, Integer kilometraje) {
        this.idDatosMoto = idDatosMoto;
        this.marcaMoto = marcaMoto;
        this.modeloMoto = modeloMoto;
        this.colorMoto = colorMoto;
        this.imagenMoto = imagenMoto;
        this.soatMoto = soatMoto;
        this.tecnomecanica = tecnomecanica;
        this.placaMoto = placaMoto;
        this.kilometraje = kilometraje;
    }

    // Getters y Setters
    
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
}