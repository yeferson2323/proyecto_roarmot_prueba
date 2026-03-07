package com.roarmot.roarmot.repositories;

import com.roarmot.roarmot.models.Alerta;
import com.roarmot.roarmot.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    
    // Encontrar alertas por usuario
    List<Alerta> findByUsuario(Usuario usuario);
    
    // Encontrar alertas activas por usuario
    List<Alerta> findByUsuarioAndActivaTrue(Usuario usuario);
    
    // Encontrar alertas no leídas por usuario
    List<Alerta> findByUsuarioAndLeidaFalse(Usuario usuario);
    
    // Encontrar alertas por tipo y usuario
    List<Alerta> findByTipoAndUsuario(String tipo, Usuario usuario);
    
    // Encontrar alertas próximas a vencer (SOAT)
    @Query("SELECT a FROM Alerta a WHERE a.vencimientoSoat BETWEEN :startDate AND :endDate AND a.activa = true")
    List<Alerta> findSoatProximosAVencer(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Encontrar alertas por nivel de urgencia
    List<Alerta> findByNivelAndActivaTrue(String nivel);
}