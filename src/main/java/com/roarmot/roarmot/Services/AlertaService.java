package com.roarmot.roarmot.Services;

import com.roarmot.roarmot.models.Alerta;
import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.repositories.AlertaRepository;
import com.roarmot.roarmot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlertaService {
    
    @Autowired
    private AlertaRepository alertaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Obtener todas las alertas de un usuario
    public List<Alerta> getAlertasByUsuarioId(Long usuarioId) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtener alertas del usuario
        return alertaRepository.findByUsuario(usuario);
    }
    
    // Obtener alertas activas
    public List<Alerta> getAlertasActivasByUsuarioId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return alertaRepository.findByUsuarioAndActivaTrue(usuario);
    }
}