package com.roarmot.roarmot.repositories;

import com.roarmot.roarmot.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Este método le dice a Spring que genere una consulta SQL para buscar por email.
    // El nombre "findByEmail" debe coincidir con la propiedad 'email' en tu clase Usuario.
    Optional<Usuario> findByEmail(String email);
}