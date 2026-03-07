package com.roarmot.roarmot.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.persistence.*;

import com.roarmot.roarmot.models.EstadoUsuario; // <-- Importar el nuevo Enum
import com.roarmot.roarmot.models.Moto;
import java.util.List; 


@Entity // marcamos la clase como entidad JPA
@Table(name = "usuario") // Mapea a nuestra tabla usuario en la DB
public class Usuario {

    // ID_Usurrio
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremental en MySQL
    @Column(name = "ID_USUARIO")
    private Long idUsuario;
    
    // 2. TIPO_DOCUMENTO, NUMERO_USUARIO, TEL_USUARIO
    @Column(name = "TIPO_DOCUMENTO")
    private String tipoDocumento;

    @Column(name = "NUMERO_USUARIO")
    private String numeroUsuario;

    @Column(name = "TEL_USUARIO")
    private String telefono; 

    // nombre y apellido
    @Column(name = "NOMBRE_USUARIO")
    private String nombre;

    @Column(name = "APELLIDO_USUARIO")
    private String apellido;

    // CORREO Y CONTRASEÑA
    @Column(name = "CORREO_USUARIO", nullable = false, unique = true)
    private String email; // Usamos email en java, mapeado a CORREO_USUARIO

    @Column(name = "CONTRASENA", nullable = false)
    private String password; // !contraseña hasheada¡

    // ROL ID ROL y Nombre Empresa 
    // 
    @Column(name = "ROL_ID_ROL", nullable = false)
    private Integer rolId; // Usamos Integer para el ID del rol (int(11) en MySQL)

    @Column(name = "NOMBRE_EMPRESA")
    private String nombreEmpresa;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion = LocalDateTime.now();


    // ESTADO_USUARIO (Mapeo correcto para ENUM)
    @Enumerated(EnumType.STRING) // Indica a JPA que guarde el nombre del Enum (ej: "Activo")
    @Column(name = "ESTADO_USUARIO", nullable = false) 
    private EstadoUsuario estadoUsuario = EstadoUsuario.Activo; // Inicialización por defecto en Java

    // Columna para Almacenar la imagen del perfil del usurio
    @Column(name = "URL_IMAGEN_PERFIL")
    private String urlImagenPerfil;

    // Columna para Almacenar la imagen de la Moto del usuario
    @Column(name = "URL_IMAGEN_MOTO")
    private String urlImagenMoto;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Moto> motos = new ArrayList<>();

    // MÉTODOS HELPER - aquí van justo después de declarar la lista
    public void agregarMoto(Moto moto) {
        motos.add(moto);
        moto.setUsuario(this);
    }

    public void removerMoto(Moto moto) {
        motos.remove(moto);
        moto.setUsuario(null);
    }

    // CONTROLADORES 
    public Usuario(){

    }
    
    // Constructor útil para crear el objeto desde el formulario 
    public Usuario(String email, Integer rolId, String password, String nombre, String apellido, String telefono){
        this.email = email;
        this.rolId = rolId;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }


    // GETTERS Y SETTERS (Necesarios para Spring Data JPA y Thymeleaf)

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroUsuario() {
        return numeroUsuario;
    }

    public void setNumeroUsuario(String numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public EstadoUsuario getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(EstadoUsuario estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public String getUrlImagenPerfil() {
        return urlImagenPerfil;
    }

    public void setUrlImagenPerfil(String urlImagenPerfil) {
        this.urlImagenPerfil = urlImagenPerfil;
    }

    public String getUrlImagenMoto() {
        return urlImagenMoto;
    }

    public void setUrlImagenMoto(String urlImagenMoto) {
        this.urlImagenMoto = urlImagenMoto;
    }

    public List<Moto> getMotos() {
        return motos;
    }

    public void setMotos(List<Moto> motos) {
        this.motos = motos;
    }

}
