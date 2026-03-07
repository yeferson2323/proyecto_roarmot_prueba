package com.roarmot.roarmot.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.repositories.UsuarioRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.lang.StringBuilder;

import com.roarmot.roarmot.Services.MotoService;
import com.roarmot.roarmot.dto.MotoDTO;
import java.util.List;

@Controller
public class HomeController {

    // Agrega esta variable:
    private final MotoService motoService;

    // Define la constante para el directorio de subida de imágenes de perfil.
    // Es una buena práctica usar una constante para evitar errores de escritura y facilitar la modificación.
    private static final String UPLOAD_PERFILES_DIR = "src/main/resources/static/uploads/perfiles/";
    // Nueva constante para el directorio de subida de imágenes de moto.
    private static final String UPLOAD_MOTOS_DIR = "src/main/resources/static/uploads/motos/";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    

    // Constructor para inyectar las dependencias de UsuarioRepository y PasswordEncoder.
    public HomeController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, MotoService motoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
         this.motoService = motoService;
    }

    // Maneja la solicitud GET para la ruta principal /.
    // Muestra la vista de la página de inicio.
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Maneja la solicitud GET para la ruta /login.
    // Muestra la vista del formulario de inicio de sesión.
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // Maneja la solicitud GET para la ruta /dashboard.
    // Muestra la vista del panel de control del usuario.
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Obtiene el objeto de autenticación del contexto de seguridad.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Busca al usuario en la base de datos por su email.
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(username);

        // Si el usuario existe, se agregan sus datos al modelo para que estén disponibles en la vista.
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("ofuscatedEmail", ofuscarEmail(usuario.getEmail()));

            // --- CÓDIGO AÑADIR A PARTIR DE AQUÍ ---
            // Obtener el rol del usuario de la sesión de Spring Security
            String rolNombre = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("default"); // Asigna un valor por defecto si no se encuentra un rol

            // --- LÍNEA DE DEPURACIÓN AGREGADA AQUÍ ---
            System.out.println("Valor del rol en el controlador: " + rolNombre);
            // --- FIN DE LÍNEA DE DEPURACIÓN ---

            // Elimina el prefijo "ROLE_" que Spring Security añade por defecto
            String rolSinPrefijo = rolNombre.replace("ROLE_", "");
            
            // Y agrega esta variable al modelo para que la vista la pueda usar
            model.addAttribute("rol_nombre", rolSinPrefijo);

             // --- NUEVO CÓDIGO: Obtener moto del usuario ---
            try {
                // 1. Obtener motos SOLO de este usuario
                List<MotoDTO> motos = motoService.obtenerMotosPorUsuario(usuario.getIdUsuario());
                
                // 2. Si tiene motos, tomar la primera (normalmente 1 por usuario)
                MotoDTO motoUsuario = null;
                if (motos != null && !motos.isEmpty()) {
                    motoUsuario = motos.get(0);
                    System.out.println("Moto encontrada para " + username + ": " + motoUsuario.getPlacaMoto());
                } else {
                    System.out.println("Usuario " + username + " no tiene motos registradas");
                }
                
                // 3. Pasar a la vista (IMPORTANTE)
                model.addAttribute("motoDelUsuario", motoUsuario);
                
            } catch (Exception e) {
                System.err.println("Error al obtener moto: " + e.getMessage());
                model.addAttribute("motoDelUsuario", null);
            }
            // --- FIN DEL NUEVO CÓDIGO ---
            return "dashboard";
        }
        
        // Si el usuario no se encuentra, se redirige a la página de login.
        return "redirect:/login"; 
    }

    // ---
    
    // Maneja la solicitud POST para la ruta /actualizar-perfil.
    // Este método gestiona la subida de una nueva imagen de perfil.
    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(@RequestParam("profileUpload") MultipartFile file, 
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication) {
        // Obtiene el nombre de usuario autenticado.
        String username = authentication.getName();
        // Busca el usuario en el repositorio.
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(username);

        // Verifica si el usuario existe.
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            // Verifica si se ha seleccionado un archivo.
            if (!file.isEmpty()) {
                try {
                    // Genera un nombre de archivo único utilizando UUID para evitar conflictos.
                    String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    // Crea la ruta completa del archivo de destino.
                    Path path = Paths.get(UPLOAD_PERFILES_DIR + nombreArchivo);

                    // Asegura que el directorio de subida exista. Si no, lo crea.
                    Files.createDirectories(path.getParent());
                    // Copia el archivo subido al directorio de destino, reemplazando si existe.
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    // Actualiza la URL de la imagen de perfil en el objeto Usuario.
                    usuario.setUrlImagenPerfil(nombreArchivo);
                    // Guarda el objeto Usuario actualizado en la base de datos.
                    usuarioRepository.save(usuario);

                    // Añade un mensaje de éxito para la redirección.
                    redirectAttributes.addFlashAttribute("mensaje", "Imagen de perfil actualizada con éxito!");
                } catch (IOException e) {
                    // Si ocurre un error de I/O, lo imprime y añade un mensaje de error.
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "Error al subir la imagen.");
                }
            } else {
                // Si no se selecciona un archivo, añade un mensaje de error.
                redirectAttributes.addFlashAttribute("error", "No se seleccionó ninguna imagen.");
            }
        } else {
            // Si el usuario no se encuentra, añade un mensaje de error.
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
        }

        // Redirige al dashboard para mostrar los mensajes de éxito o error.
        return "redirect:/dashboard";
    }
    
    // Maneja la subida de la imagen de la moto.
    @PostMapping("/actualizar-imagen-moto")
    public String actualizarImagenMoto(@RequestParam("motoUpload") MultipartFile file, 
                                       RedirectAttributes redirectAttributes,
                                       Authentication authentication) {
        String username = authentication.getName();
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(username);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (!file.isEmpty()) {
                try {
                    String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(UPLOAD_MOTOS_DIR + nombreArchivo);

                    Files.createDirectories(path.getParent());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    // Se actualiza la nueva columna urlImagenMoto
                    usuario.setUrlImagenMoto(nombreArchivo);
                    usuarioRepository.save(usuario);

                    redirectAttributes.addFlashAttribute("mensaje", "Imagen de la moto actualizada con éxito!");
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "Error al subir la imagen de la moto.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "No se seleccionó ninguna imagen.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
        }

        return "redirect:/dashboard";
    }

    // Método privado para ofuscar el email del usuario.
    // Esto se hace para proteger la privacidad del usuario en la vista.
    // Por ejemplo, "usuario@dominio.com" se convierte en "u****o@dominio.com".
    private String ofuscarEmail(String email) {
        // Si el email es nulo o está vacío, devuelve una cadena vacía.
        if (email == null || email.isEmpty()) {
            return "";
        }

        // Encuentra la posición del '@' en el email.
        int atIndex = email.indexOf('@');
        // Si el email tiene un formato inválido, devuelve el email original.
        if (atIndex <= 1) {
            return email;
        }

        // Separa el nombre de usuario y el dominio.
        String nombre = email.substring(0, atIndex);
        String dominio = email.substring(atIndex);

        // Construye el email ofuscado.
        StringBuilder ofuscado = new StringBuilder();
        // Añade la primera letra del nombre de usuario.
        ofuscado.append(nombre.charAt(0));
        // Añade los asteriscos para ocultar el nombre.
        ofuscado.append("****");
        // Si el nombre de usuario tiene más de una letra, añade la última letra.
        if (nombre.length() > 1) {
            ofuscado.append(nombre.charAt(nombre.length() - 1));
        }
        // Añade el dominio.
        ofuscado.append(dominio);

        return ofuscado.toString();
    }
}
