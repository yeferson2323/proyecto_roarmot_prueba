package com.roarmot.roarmot.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.repositories.UsuarioRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

@Controller
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/RegistroPaso1")
    public String registro1() {
        return "registro1";
    }

    @GetMapping("/RegistroPaso2")
    public String showRegistro2(@RequestParam(name = "email", required = false) String email, @RequestParam(name = "rol", required = false) String rol, Model model) {
        if (email != null && rol != null) {
            model.addAttribute("email", email);
            model.addAttribute("rol", rol);
        }
        return "registro2";
    }

    @GetMapping("/RegistroPaso3")
    public String mostrarRegistro3(@RequestParam(required = false) String email,
                               @RequestParam(required = false) String rol,
                               Model model) {
        
        if (email != null && rol != null) {
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            model.addAttribute("usuario", usuario);
            model.addAttribute("rol", rol);
            return "registro3";
        }
        
        System.err.println("Acceso a RegistroPaso3 sin datos. Redirigiendo a Paso 1.");
        return "redirect:/RegistroPaso1";
    }
    
    @PostMapping("/enviar-codigo")
    public String enviarCodigo(@RequestParam("email") String email, @RequestParam("rol") String rol, HttpSession session) {
        String codigo = String.format("%06d", new java.util.Random().nextInt(1000000));
        session.setAttribute("codigoVerificacion", codigo);

        System.out.println("Código de verificación: " + codigo + " enviado a " + email);

        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());
            String encodedRol = URLEncoder.encode(rol, StandardCharsets.UTF_8.toString());
            return "redirect:/RegistroPaso2?email=" + encodedEmail + "&rol=" + encodedRol;
        } catch (java.io.UnsupportedEncodingException e) {
            System.err.println("Error de codificación de URL: " + e.getMessage());
            return "redirect:/RegistroPaso1";
        }
    }

    @PostMapping("/validar-codigo")
    public String validarCodigo(
        @RequestParam("email") String email, 
        @RequestParam("rol") String rol, 
        @RequestParam("d1") String d1, 
        @RequestParam("d2") String d2, 
        @RequestParam("d3") String d3, 
        @RequestParam("d4") String d4, 
        @RequestParam("d5") String d5, 
        @RequestParam("d6") String d6,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        String codigoIngresado = d1 + d2 + d3 + d4 + d5 + d6;
        String codigoCorrecto = (String) session.getAttribute("codigoVerificacion"); 

        if (codigoCorrecto == null || !codigoIngresado.equals(codigoCorrecto)) {
            redirectAttributes.addFlashAttribute("error", "Código incorrecto. Intenta de nuevo.");
            redirectAttributes.addAttribute("email", email);
            redirectAttributes.addAttribute("rol", rol);
            return "redirect:/RegistroPaso2";
        }
        
        session.removeAttribute("codigoVerificacion");
        redirectAttributes.addFlashAttribute("email", email);
        redirectAttributes.addFlashAttribute("rol", rol);
        System.out.println("Validación Exitosa: Redirigiendo a Paso 3");
        return "redirect:/RegistroPaso3?email=" + email + "&rol=" + rol;
    }
    
    @PostMapping("/guardar-usuario")
    public String guardarUsuario(
            @ModelAttribute("usuario") @Valid Usuario usuario,
            BindingResult result,
            @RequestParam("rol") String rol,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("rol", rol);
            return "registro3";
        }

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            model.addAttribute("error", "Error al registrar el usuario. El correo ya existe.");
            model.addAttribute("rol", rol);
            return "registro3";
        }

        // Se define el ID del rol a partir de su valor en String.
        int rolId;
        switch (rol.toLowerCase()) {
            case "vendedor":
                rolId = 1;
                break;
            case "comprador":
                rolId = 2;
                break;
            default:
                // Asignar un rol por defecto para cualquier otro caso
                System.err.println("Rol desconocido: " + rol);
                rolId = 2; // Por defecto, es 'comprador'
                break;
        }

        // Se asigna el ID de rol (Integer) al usuario.
        usuario.setRolId(rolId);

        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        usuario.setFechaCreacion(LocalDateTime.now());

        try {
            usuarioRepository.save(usuario);
            System.out.println("Usuario guardado exitosamente: " + usuario.getEmail());
            return "redirect:/login?registro_exitoso";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al registrar el usuario. Intenta de nuevo.");
            model.addAttribute("rol", rol);
            return "registro3";
        }
    }
}
