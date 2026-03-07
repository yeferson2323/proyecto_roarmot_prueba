package com.roarmot.roarmot.Services;

import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.repositories.UsuarioRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// @Service le dice a Spring que esta clase es un servicio, lo que permite que sea inyectada en otras partes de tu aplicación.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + email));

        // Obtener el ID del rol del usuario
        Integer rolId = usuario.getRolId();

        // Mapear el ID del rol a su nombre correspondiente
        String rolNombre = mapRolIdToRolName(rolId);
        
        // Crear una lista de autoridades con el rol del usuario, añadiendo el prefijo "ROLE_"
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + rolNombre)
        );

        // Devolver el objeto User de Spring Security con los roles del usuario
        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
    
    /**
     * Mapea un ID de rol a un nombre de rol en formato String.
     * Puedes adaptar esta lógica según la tabla de roles en tu base de datos.
     * Por ejemplo, si los IDs son 1, 2, 3, puedes mapearlos a "Administrador", "Vendedor" y "Cliente".
     * @param rolId El ID del rol del usuario.
     * @return El nombre del rol.
     */
    private String mapRolIdToRolName(Integer rolId) {
        if (rolId == null) {
            return "default"; // O algún otro rol por defecto si el ID es nulo
        }
        switch (rolId) {
            case 1:
                return "Vendedor";
            case 2:
                return "Motero";
            case 3:
                return "default";
            default:
                return "default"; // Rol por defecto si el ID no coincide
        }
    }
     /**
     * Permite a otros servicios (como VendedorController) obtener el objeto Usuario completo.
     * Este es el método que se llama desde VendedorController.
     * @param email Correo electrónico del usuario.
     * @return Objeto Usuario o null si no se encuentra (para manejarlo en el controlador).
     */
    public Usuario findByCorreoUsuario(String email) {
        // Usamos findByEmail, que es el método existente en tu repositorio
        // .orElse(null) nos permite devolver null si el usuario no existe.
        return usuarioRepository.findByEmail(email).orElse(null); 
    }
    
}
