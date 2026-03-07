package com.roarmot.roarmot.repositories;

import com.roarmot.roarmot.models.Moto;
import com.roarmot.roarmot.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MotoRepository extends JpaRepository<Moto, Long> {
    // Encontramos todas las motos de un usuario
    List<Moto> findByUsuario(Usuario usuario);

   // Encontrar todas las motos de un usuario por ID
    List<Moto> findByUsuarioIdUsuario(Long idUsuario);
    
    // Encontrar moto por placa
    Optional<Moto> findByPlacaMoto(String placaMoto);
    
    // Verificar si existe una placa (para validaciones)
    boolean existsByPlacaMoto(String placaMoto);
    
    // Verificar si un usuario ya tiene una moto con cierta placa
    boolean existsByPlacaMotoAndUsuario(String placaMoto, Usuario usuario);
    
    // Contar cuántas motos tiene un usuario
    Long countByUsuario(Usuario usuario);
    
    // Encontrar motos por marca
    List<Moto> findByMarcaMoto(String marcaMoto);
    
    // Encontrar motos con SOAT próximo a vencer (ejemplo de query personalizada)
    // List<Moto> findMotosConSoatProximoAVencer(Date fechaLimite);

    // En MotoRepository.java - agrega este método:
    @Query("SELECT m.marcaMoto, COUNT(m) FROM Moto m GROUP BY m.marcaMoto")
    List<Object[]> contarMotosPorMarca();
}
