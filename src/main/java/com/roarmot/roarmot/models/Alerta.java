package com.roarmot.roarmot.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ALERTA")
public class Alerta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALERTA")
    private Long idAlerta;
    
    // CAMPOS ESPECÍFICOS DE VENCIMIENTO
    @Column(name = "VENCIMIENTO_SOAT")
    private LocalDate vencimientoSoat;
    
    @Column(name = "VENCIMIENTO_TECNOMECANICA")
    private LocalDate vencimientoTecnomecanica;
    
    @Column(name = "VENCIMIENTO_LICENCIA_CONDUCCION")
    private LocalDate vencimientoLicenciaConduccion;
    
    @Column(name = "MANTENIMIENTO_GENERAL")
    private LocalDate mantenimientoGeneral;
    
    // CAMPOS MULTIPROPÓSITO
    @Column(name = "TIPO")
    private String tipo; // SOAT, TECNOMECANICA, PROMOCION, ACTUALIZACION
    
    @Column(name = "TITULO")
    private String titulo;
    
    @Column(name = "MENSAJE")
    private String mensaje;
    
    @Column(name = "ICONO")
    private String icono = "warning";
    
    @Column(name = "NIVEL")
    private String nivel = "warning"; // info, warning, danger, urgent, promocion
    
    @Column(name = "ACTIVA")
    private Boolean activa = true;
    
    @Column(name = "LEIDA")
    private Boolean leida = false;
    
    @Column(name = "ACCION_URL")
    private String accionUrl;
    
    @Column(name = "ACCION_TEXTO")
    private String accionTexto;
    
    // RELACIONES
    @ManyToOne
    @JoinColumn(name = "ID_DATOSMOTO")
    private Moto moto;
    
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;
    
    // CONSTRUCTORES
    public Alerta() {}
    
    public Alerta(String tipo, String titulo, String mensaje, Usuario usuario) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.usuario = usuario;
        this.activa = true;
        this.leida = false;
    }
    
    // GETTERS Y SETTERS (te los muestro abreviados)
    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }


    public LocalDate getVencimientoSoat() {
        return vencimientoSoat;
    }

    public void setVencimientoSoat(LocalDate vencimientoSoat) {
        this.vencimientoSoat = vencimientoSoat;
    }

    public LocalDate getVencimientoTecnomecanica() {
        return vencimientoTecnomecanica;
    }

    public void setVencimientoTecnomecanica(LocalDate vencimientoTecnomecanica) {
        this.vencimientoTecnomecanica = vencimientoTecnomecanica;
    }

    public LocalDate getVencimientoLicenciaConduccion() {
        return vencimientoLicenciaConduccion;
    }

    public void setVencimientoLicenciaConduccion(LocalDate vencimientoLicenciaConduccion) {
        this.vencimientoLicenciaConduccion = vencimientoLicenciaConduccion;
    }

    public LocalDate getMantenimientoGeneral() {
        return mantenimientoGeneral;
    }

    public void setMantenimientoGeneral(LocalDate mantenimientoGeneral) {
        this.mantenimientoGeneral = mantenimientoGeneral;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public String getAccionUrl() {
        return accionUrl;
    }

    public void setAccionUrl(String accionUrl) {
        this.accionUrl = accionUrl;
    }

    public String getAccionTexto() {
        return accionTexto;
    }

    public void setAccionTexto(String accionTexto) {
        this.accionTexto = accionTexto;
    }

    public Moto getMoto() {
        return moto;
    }

    public void setMoto(Moto moto) {
        this.moto = moto;
    }

    
    
    // ... agregar getters/setters para los demás campos
}