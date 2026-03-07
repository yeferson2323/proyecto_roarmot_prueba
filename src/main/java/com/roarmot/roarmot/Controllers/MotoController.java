package com.roarmot.roarmot.Controllers;
import com.roarmot.roarmot.dto.MotoDTO;
import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.Services.MotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.net.Authenticator;
import java.util.List;
import com.roarmot.roarmot.Services.CustomUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;




@RestController
@RequestMapping("/api/motos")
@CrossOrigin(origins = "*") // Ajusta según tu frontend
public class MotoController {

    @Autowired
    private MotoService motoService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Crear nueva moto para el usuario autenticado
    @PostMapping
    public ResponseEntity<?> crearMoto(@RequestBody MotoDTO motoDTO, 
                                    Authentication authentication) { // ← Agregar este parámetro
        try {
            // Obtener el usuario autenticado (igual que en VendedorController)
            Usuario usuarioActual = getAuthenticatedUser(authentication);
            Long usuarioId = usuarioActual.getIdUsuario();
            
            System.out.println("🔍 Usuario autenticado: " + usuarioActual.getEmail() + " ID: " + usuarioId);
            
            // Asignar imagen por defecto si está vacía
            if (motoDTO.getImagenMoto() == null || motoDTO.getImagenMoto().trim().isEmpty()) {
                motoDTO.setImagenMoto("/imagenes/moto-default.jpg");
            }
            
            MotoDTO motoCreada = motoService.crearMoto(motoDTO, usuarioId);
            return ResponseEntity.ok(motoCreada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Agrega este método (igual que en VendedorController)
    private Usuario getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error de autenticación. Por favor, inicie sesión nuevamente.");
        }
        
        String emailUsuarioLogueado = authentication.getName(); 
        Usuario usuarioActual = customUserDetailsService.findByCorreoUsuario(emailUsuarioLogueado);
        
        if (usuarioActual == null) {
            throw new RuntimeException("Error: Su usuario no pudo ser encontrado en la base de datos.");
        }
        return usuarioActual;
    }

    // Obtener todas las motos del usuario autenticado
    @GetMapping
    public ResponseEntity<?> obtenerMotosDelUsuario(Authentication authentication) {
        try {
            // 1. Obtenemos usuario autenticado
            Usuario usuarioActual = getAuthenticatedUser(authentication);
            Long usuarioId = usuarioActual.getIdUsuario();
            
            System.out.println("Obteniendo motos para usuario ID: " + usuarioId);

            // 2. Obtenemos motos solo de Este usuario    
            List<MotoDTO> motos = motoService.obtenerMotosPorUsuario(usuarioId);

            System.out.println("Total motos encontradas: " + motos.size());
            if (!motos.isEmpty()) {
                System.out.println("Primera moto: " + motos.get(0).getPlacaMoto());
            }

            return ResponseEntity.ok(motos);
            
        } catch (RuntimeException e) {
             System.err.println("Error al obtener motos: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    // Obtener una moto específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMotoPorId(@PathVariable Long id) {
        try {
            MotoDTO moto = motoService.obtenerMotoPorId(id);
            return ResponseEntity.ok(moto);
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    // Actualizar una moto
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMoto(@PathVariable Long id, @RequestBody MotoDTO motoDTO) {
        try {
            MotoDTO motoActualizada = motoService.actualizarMoto(id, motoDTO);
            return ResponseEntity.ok(motoActualizada);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    // Eliminar una moto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMoto(@PathVariable Long id) {
        try {
            motoService.eliminarMoto(id);
            return ResponseEntity.ok().body("Moto eliminada correctamente");
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    // MÉTODO TEMPORAL - Reemplazar cuando implementes Spring Security
    private Long obtenerUsuarioIdAutenticado(HttpServletRequest request) {
        // Por ahora retornamos un ID fijo para pruebas
        // Más adelante lo reemplazamos con:
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // return ((Usuario) authentication.getPrincipal()).getIdUsuario();
        
        return 1L; // ID temporal - cambiar por el ID del usuario logueado
    }

    // Método para subir la imagen 
    @PostMapping("/subir-imagen")
    public ResponseEntity<?> subirImagenMoto(
            @RequestParam("imagenMoto") MultipartFile archivo,
            Authentication authentication) {
        
        try {
            // 1. Obtener usuario autenticado
            Usuario usuarioActual = getAuthenticatedUser(authentication);
            Long usuarioId = usuarioActual.getIdUsuario();
            
            // 2. Delegar al Service la lógica de guardado
            String rutaImagen = motoService.guardarImagenMoto(archivo, usuarioId);
            
            // 3. Crear respuesta
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("nombreArchivo", rutaImagen);
            respuesta.put("rutaCompleta", rutaImagen);
            respuesta.put("mensaje", "Imagen subida exitosamente");
            
            return ResponseEntity.ok(respuesta);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al subir imagen: " + e.getMessage());
        }
    }

}
