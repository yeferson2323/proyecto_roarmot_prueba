package com.roarmot.roarmot.Controllers; // Asegúrate de que este sea el paquete correcto

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Importaciones necesarias
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority; // Nuevo: para el rol
import org.springframework.security.core.context.SecurityContextHolder;
import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.repositories.UsuarioRepository;
import com.roarmot.roarmot.Services.MotoService;
import com.roarmot.roarmot.dto.MotoDTO;
import java.util.Optional;
import java.util.List;
import java.lang.StringBuilder; // Nuevo: para el método ofuscarEmail

@Controller
public class SosController {

    // Dependencias inyectadas previamente
    private final UsuarioRepository usuarioRepository;
    private final MotoService motoService;

    // Constructor para inyección de dependencias
    public SosController(UsuarioRepository usuarioRepository, MotoService motoService) {
        this.usuarioRepository = usuarioRepository;
        this.motoService = motoService;
    }

    @GetMapping("/sos")
    public String mostrarAsistenteEnCarretera(Model model) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(username);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            
            // --- 1. Variables requeridas por el NAVBAR ---
            
            // 1a. USUARIO
            model.addAttribute("usuario", usuario);
            
            // 1b. EMAIL OFUSCADO (m****a@gmail.com)
            model.addAttribute("ofuscatedEmail", ofuscarEmail(usuario.getEmail())); 
            
            // 1c. ROL (Para habilitar el Panel Proveedor)
            String rolNombre = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("default"); 
            String rolSinPrefijo = rolNombre.replace("ROLE_", "");
            model.addAttribute("rol_nombre", rolSinPrefijo);
            
            // --- 2. Variable requerida por la funcionalidad SOS (Moto) ---
            try {
                // Obtener moto del usuario (lógica copiada del HomeController)
                List<MotoDTO> motos = motoService.obtenerMotosPorUsuario(usuario.getIdUsuario());
                MotoDTO motoUsuario = null;
                
                if (motos != null && !motos.isEmpty()) {
                    motoUsuario = motos.get(0);
                }
                
                model.addAttribute("motoDelUsuario", motoUsuario);
                
            } catch (Exception e) {
                System.err.println("Error al obtener moto en SosController: " + e.getMessage());
                model.addAttribute("motoDelUsuario", null);
            }
            
            return "asistenteCarretera"; 
        }
        
        return "redirect:/login"; 
    }
    
    // --- 3. Método privado copiado del HomeController para ofuscar el email ---
    private String ofuscarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }

        String nombre = email.substring(0, atIndex);
        String dominio = email.substring(atIndex);

        StringBuilder ofuscado = new StringBuilder();
        ofuscado.append(nombre.charAt(0));
        ofuscado.append("****");
        if (nombre.length() > 1) {
            ofuscado.append(nombre.charAt(nombre.length() - 1));
        }
        ofuscado.append(dominio);

        return ofuscado.toString();
    }
}