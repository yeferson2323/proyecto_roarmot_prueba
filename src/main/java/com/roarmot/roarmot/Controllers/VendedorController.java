package com.roarmot.roarmot.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.roarmot.roarmot.dto.ProductoFormDTO;
import com.roarmot.roarmot.Services.FileStorageService;
import com.roarmot.roarmot.Services.ProductoService;
import com.roarmot.roarmot.models.Categoria;
import com.roarmot.roarmot.models.Producto;
import com.roarmot.roarmot.models.Subcategoria; 
import com.roarmot.roarmot.Services.SubcategoriaService;
import com.roarmot.roarmot.Services.CategoriaService;
// --- NUEVOS IMPORTS DE SEGURIDAD ---
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.util.ExcelGenerator;
import com.roarmot.roarmot.Services.CustomUserDetailsService; // <-- ¡Asegúrate de tener este servicio!
import com.roarmot.roarmot.Services.EmailService;
import com.roarmot.roarmot.dto.ProductoQuickEditDTO; // <--- NUEVO IMPORT
import com.roarmot.roarmot.repositories.ProductoRepository;
// PAQUETES ESTÁNDAR DE JAVA A AGREGAR PARA MAPAS Y LISTAS
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors; // Necesario para la lógica de errores
// --- NUEVOS IMPORTS DE SEGURIDAD ---
// Sustituimos la inyección directa por la genérica de Spring Security
import org.springframework.security.core.Authentication; 
import java.io.IOException; // ¡Importante!
import java.util.List;

import java.util.Arrays;




@Controller
@RequestMapping("/panel")
public class VendedorController {

    private final ProductoService productoService;
    private final FileStorageService fileStorageService;
    // DEPENDENCIA INYECTADA: Usaremos el servicio de seguridad para buscar el usuario
    private final CustomUserDetailsService customUserDetailsService; 

    
    @Autowired
    private SubcategoriaService subcategoriaService;
    
    @Autowired
    private CategoriaService categoriaService;

    @Autowired // <--- ¡Esta anotación es vital!
    private ProductoRepository productoRepository;

    @Autowired
    private EmailService emailService;

    // Inyección de dependencias en el constructor
    public VendedorController(
        ProductoService productoService, 
        FileStorageService fileStorageService,
        CustomUserDetailsService customUserDetailsService // INYECTAR SERVICIO
    ) {
        this.productoService = productoService;
        this.fileStorageService = fileStorageService;
        this.customUserDetailsService = customUserDetailsService; // ASIGNAR SERVICIO
    }

    // =========================================================================
    // MÉTODOS AUXILIARES NO PROVADO
    // =========================================================================

    /**
     * Busca el Usuario a partir de la autenticación de Spring Security.
     */
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
    
    /**
     * Busca un producto por su ID. NO PROVADO :d
     */
    private Producto getProductoById(Long idProducto) {
        return productoService.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto con ID " + idProducto + " no encontrado."));
    }

    // Método que muestra la página principal del panel de vendedor
    @GetMapping({"", "/inicio"})
    public String showPanelInicio(Model model, Authentication authentication) {
        model.addAttribute("contentFragment", "fragmentos/panelVendedor/panelInicio");
        // Opcional: pasar el nombre del usuario si lo necesitas en la vista
        if (authentication != null) {
            model.addAttribute("usuarioEmail", authentication.getName());
        }
        return "panelVendedor"; 
    }


    // Método para mostrar el formulario de agregar producto 
    @GetMapping("/addProduct")
    public String showAddProductForm(Model model) {
        // Cargar los datos necesarios para el formulario (categorías, subcategorías, enums de talla)
        try {
            model.addAttribute("subcategorias", subcategoriaService.findAllSubcategorias());
            model.addAttribute("categorias", categoriaService.findAllCategorias());
            model.addAttribute("TallaEnum", Producto.Talla.values());
            model.addAttribute("productoForm", new ProductoFormDTO()); // Objeto vacío para el formulario
            model.addAttribute("contentFragment", "fragmentos/panelVendedor/addProduct");
        } catch (Exception e) {
            System.err.println("Error al cargar datos iniciales del formulario: " + e.getMessage());
            model.addAttribute("errorGlobal", "Error interno al cargar datos del formulario.");
        }
        return "panelVendedor";
    }


    // --- 2. Lógica para procesar el formulario y guardar el producto ---
    @PostMapping("/ProductProcess")
    public String processAddProductForm(
            @Valid @ModelAttribute("productoForm") ProductoFormDTO productoForm, 
            BindingResult result, 
            Authentication authentication, 
            Model model) {

        Usuario usuarioActual = null;

        // --- INICIO DE LA LÓGICA DE RECARGA DE ATRIBUTOS DEL FORMULARIO ---
        // Se ejecuta para que, en caso de error, el formulario se recargue correctamente
        try {
            model.addAttribute("subcategorias", subcategoriaService.findAllSubcategorias());
            model.addAttribute("categorias", categoriaService.findAllCategorias());
            model.addAttribute("TallaEnum", Producto.Talla.values());
            model.addAttribute("contentFragment", "fragmentos/panelVendedor/addProduct");
        } catch (Exception e) {
            System.err.println("Error al recargar categorías/subcategorías: " + e.getMessage());
            model.addAttribute("errorGlobal", "Error interno al cargar datos del formulario.");
        }
        // --- FIN DE LA LÓGICA DE RECARGA DE ATRIBUTOS DEL FORMULARIO ---

        System.out.println("\n\n*******************************************************************");
        System.out.println("LOG DEBUG: Método processAddProductForm POST ALCANZADO.");

        // 1. **VERIFICACIÓN Y OBTENCIÓN DE LA IDENTIDAD DEL USUARIO**
        if (authentication != null && authentication.isAuthenticated()) {
            String emailUsuarioLogueado = authentication.getName(); // Esto es el username/email
            
            try {
                // CAMBIO AQUÍ: Llamamos al nuevo método en el servicio correcto
                usuarioActual = customUserDetailsService.findByCorreoUsuario(emailUsuarioLogueado);
                
                if (usuarioActual != null) {
                    Long idUsuarioActual = usuarioActual.getIdUsuario();
                    System.out.println("LOG DEBUG: ID del Usuario Vendedor Autenticado: " + idUsuarioActual);
                    System.out.println("LOG DEBUG: Nombre del Producto Recibido: " + productoForm.getNombre());
                } else {
                    System.out.println("LOG CRÍTICO: ERROR: usuarioActual es nulo. El usuario no fue encontrado en la DB.");
                    model.addAttribute("errorGlobal", "Error: Su usuario no pudo ser encontrado para guardar el producto.");
                    System.out.println("*******************************************************************\n");
                    return "panelVendedor"; 
                }
            } catch (Exception e) {
                 System.err.println("LOG ERROR: Fallo al buscar usuario en la BD: " + e.getMessage());
                 model.addAttribute("errorGlobal", "Error de base de datos al buscar su perfil.");
                 System.out.println("*******************************************************************\n");
                 return "panelVendedor";
            }
        } else {
            System.out.println("LOG DEBUG: ERROR: authentication es nulo o no autenticado.");
            model.addAttribute("errorGlobal", "Error de autenticación. Por favor, inicie sesión nuevamente.");
            System.out.println("*******************************************************************\n");
            return "panelVendedor";
        }
        
        System.out.println("*******************************************************************\n");
        // ------------------------------------

        // 2. VERIFICACIÓN DE LA VALIDACIÓN (Con el usuario ya validado)
        if (result.hasErrors()) {
            System.out.println("LOG DEBUG: FALLO DE VALIDACIÓN: El DTO tiene errores.");
            return "panelVendedor"; 
        }

        // 3. PROCESO DE IMAGEN (Ahora se accede desde el DTO)
         // Ya se validó @NotNull en el DTO, solo chequeamos si está vacío.
         MultipartFile imagenFile = productoForm.getImagen();

         if (imagenFile.isEmpty()) {
            System.out.println("LOG DEBUG: ERROR: El archivo de imagen está vacío o no se seleccionó correctamente.");
             // Agregamos un error al resultado de la validación manualmente si la imagen está vacía.
             // Usamos "imagen" como el campo del DTO para que Thymeleaf muestre el error correctamente.
             result.rejectValue("imagen", "error.producto.imagen.empty", "El archivo de imagen está vacío o no se seleccionó.");
            return "panelVendedor";
        }

        try {
            String nombreArchivoGuardado = fileStorageService.storeFile(imagenFile);
            
            // 4. GUARDADO EN BASE DE DATOS
            productoService.save(productoForm, nombreArchivoGuardado, usuarioActual.getIdUsuario());
            
            System.out.println("LOG DEBUG: Producto guardado exitosamente en la BD.");

            // 5. REDIRECCIÓN EXITOSA
            return "panelVendedor";

        } catch (Exception e) {
            System.out.println("LOG DEBUG: EXCEPCIÓN AL PROCESAR/GUARDAR EL PRODUCTO: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorGlobal", "Error al intentar guardar el producto: " + e.getMessage());
            return "panelVendedor";
        }
    }


    /**
     * Muestra la página principal del vendedor con la lista de sus productos.
     * @param model Objeto Model de Spring para pasar datos a la vista.
     * @return El nombre de la plantilla (view) a renderizar.
     */
    @GetMapping("/vendedor")
    public String vendedorPanel(Model model) {
        // 1. OBTENER INFORMACIÓN DE AUTENTICACIÓN
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Long idUsuarioActual = null;
        
        // 2. VERIFICAR AUTENTICACIÓN Y OBTENER EL EMAIL
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();
            
            // 3. USAR EL SERVICIO PARA OBTENER LA ENTIDAD USUARIO COMPLETA
            Usuario usuarioAutenticado = customUserDetailsService.findByCorreoUsuario(emailUsuario);
            
            // 4. EXTRAER EL ID DEL USUARIO
            if (usuarioAutenticado != null) {
                // Obtenemos el ID real de la base de datos
                idUsuarioActual = usuarioAutenticado.getIdUsuario();
            }
        }
        
        if (idUsuarioActual != null) {
            // 5. LLAMAR AL NUEVO MÉTODO DEL SERVICIO CON EL ID REAL
            List<Producto> productosVendedor = productoService.findAllByVendedorId(idUsuarioActual);
            
            // 6. PASAR LA LISTA DE PRODUCTOS A LA VISTA
            model.addAttribute("productos", productosVendedor);
        } else {
            // Si no hay usuario o no se encontró el ID, pasa una lista vacía
            model.addAttribute("productos", List.of());
            // Nota: Aquí, en un entorno de producción, probablemente haría una redirección al login.
        }

        //model.addAttribute("titulo", "Panel de Vendedor");
        model.addAttribute("contentFragment", "fragmentos/panelVendedor/editProduct");
        return "panelVendedor"; 
    }


    // =========================================================================
    // 3. EDICIÓN DE PRODUCTO (GET y POST)
    // =========================================================================
    
    /**
     * Muestra el formulario para editar un producto, pre-rellenado.
     */
    @GetMapping("/editProductForm")
    public String showEditProductForm(
        @RequestParam("id") Long idProducto, 
        Model model, 
        RedirectAttributes ra, 
        Authentication authentication) {
        
        try {
            // 1. Obtener el producto y el usuario
            Producto producto = getProductoById(idProducto);
            Usuario usuarioActual = getAuthenticatedUser(authentication);
            
            // 2. Verificación de propiedad: Solo el dueño puede editar
            if (!producto.getUsuario().getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                ra.addFlashAttribute("error", "No tiene permiso para editar este producto.");
                return "redirect:/panel/vendedor";
            }
            
            // 3. Convertir la entidad a DTO para pre-rellenar el formulario
            ProductoFormDTO productoFormDTO = productoService.convertToDTO(producto);

            // 4. Cargar datos necesarios para el formulario
            model.addAttribute("subcategorias", subcategoriaService.findAllSubcategorias());
             model.addAttribute("TallaEnum", Producto.Talla.values());
            
            // 5. Pasar datos específicos a la vista
            model.addAttribute("productoForm", productoFormDTO); 
            model.addAttribute("idProducto", idProducto); // Necesario para el POST de edición
            model.addAttribute("imagenActual", producto.getImagen()); // Nombre del archivo actual
            model.addAttribute("contentFragment", "fragmentos/panelVendedor/editProductForm");
            
            return "panelVendedor";
            
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "Error al cargar el producto para edición: " + e.getMessage());
            return "redirect:/panelVendedor";
        }
    }
    
    /**
     * Procesa la solicitud POST para actualizar un producto.
     */
    @PostMapping("/editProductProcess")
    public String processEditProductForm(
            @RequestParam("idProducto") Long idProducto, // ID del producto a editar
            @RequestParam("imagenActual") String imagenActual, // Nombre del archivo actual
            @Valid @ModelAttribute("productoForm") ProductoFormDTO productoForm, 
            BindingResult result, 
            Authentication authentication, 
            Model model,
            RedirectAttributes ra) {
        
        // --- INICIO LÓGICA DE RECARGA EN CASO DE ERROR ---
        try {
            model.addAttribute("subcategorias", subcategoriaService.findAllSubcategorias());
             model.addAttribute("TallaEnum", Producto.Talla.values());
            model.addAttribute("idProducto", idProducto);
            model.addAttribute("imagenActual", imagenActual); 
            model.addAttribute("contentFragment", "fragmentos/panelVendedor/editProductForm");

        } catch (Exception e) {
            model.addAttribute("errorGlobal", "Error interno al recargar datos del formulario.");
            return "panelVendedor";
        }
        // --- FIN LÓGICA DE RECARGA EN CASO DE ERROR ---
        
        // 1. Obtener usuario autenticado
        Usuario usuarioActual;
        try {
            usuarioActual = getAuthenticatedUser(authentication);
        } catch (RuntimeException e) {
            model.addAttribute("errorGlobal", e.getMessage());
            return "panelVendedor";
        }

        // 2. Verificar la validación
        if (result.hasErrors()) {
            
            // Si el único error es la imagen y ya hay una actual, lo ignoramos (en edición la imagen es opcional).
            if (result.getFieldErrorCount() == 1 && 
                result.hasFieldErrors("imagen") && 
                !imagenActual.isEmpty()) {
                // Continuamos ya que solo falló la imagen (y es un campo opcional en edición si ya hay una).
            } else if (result.hasFieldErrors()) {
                 // Si hay errores en otros campos (nombre, precio, etc.), volvemos al formulario.
                 return "panelVendedor";
            }
        }
        
        // 3. Lógica del Archivo de Imagen
        String nombreArchivoFinal = imagenActual; // Por defecto, conservamos el actual
        MultipartFile nuevaImagen = productoForm.getImagen();
        
        try {
            // Si se subió un nuevo archivo, lo guardamos y actualizamos el nombre
            if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
                nombreArchivoFinal = fileStorageService.storeFile(nuevaImagen);
                
                // Opcional: Eliminar la imagen anterior si es diferente (asumiendo que fileStorageService tiene el método)
                if (imagenActual != null && !imagenActual.isEmpty() && !imagenActual.equals(nombreArchivoFinal)) {
                    // fileStorageService.deleteFile(imagenActual); 
                }
            }
            
            // 4. Actualización en Base de Datos: Necesita que ProductoService tenga el método 'update'
            productoService.update(productoForm, nombreArchivoFinal, usuarioActual.getIdUsuario(), idProducto);
            
            ra.addFlashAttribute("success", "Producto actualizado exitosamente: " + productoForm.getNombre());
            return "redirect:/panel/vendedor";
            
        } catch (SecurityException e) {
            model.addAttribute("errorGlobal", "Error de seguridad: " + e.getMessage());
            return "panelVendedor";
        } catch (Exception e) {
            model.addAttribute("errorGlobal", "Error al intentar actualizar el producto: " + e.getMessage());
            return "panelVendedor";
        }
    }

    // =========================================================================
    // 3b. EDICIÓN RÁPIDA POR AJAX (NUEVO MÉTODO)
    // =========================================================================
    
    /**
     * Procesa la solicitud POST de edición rápida de productos desde la tabla.
     * Utiliza AJAX y el DTO simplificado ProductoQuickEditDTO.
     */
    @PostMapping("/editarProductoAjax")
    @ResponseBody // Indica que el resultado debe ser serializado directamente como JSON/XML
    public Map<String, Object> editProductAjax(
            @Valid @ModelAttribute ProductoQuickEditDTO quickEditDTO,
            BindingResult result,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();

        // 1. Obtener usuario autenticado
        Usuario usuarioActual;
        try {
            usuarioActual = getAuthenticatedUser(authentication);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error de autenticación: " + e.getMessage());
            return response;
        }

        // 2. Verificar la validación del DTO
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage
                ));
            response.put("success", false);
            response.put("message", "Errores de validación.");
            response.put("errors", errors);
            return response;
        }

        try {
            // 3. LLAMAR al nuevo método del servicio para la actualización rápida
            // NOTA: Debe crear 'updateQuick' en ProductoService.java
            productoService.updateQuick(quickEditDTO, usuarioActual.getIdUsuario());
            
            response.put("success", true);
            response.put("message", "Producto actualizado correctamente.");
            
        } catch (SecurityException e) {
            response.put("success", false);
            response.put("message", "Error de seguridad (Permiso denegado): " + e.getMessage());
        } catch (Exception e) {
            // Captura errores de negocio o de base de datos
            response.put("success", false);
            response.put("message", "Error al procesar la actualización: " + e.getMessage());
        }
        
        return response;
    }

    // =========================================================================
    // 4. ELIMINACIÓN DE PRODUCTO
    // =========================================================================

    /**
     * Procesa la solicitud POST para eliminar un producto.
     * Solo permite la eliminación si el usuario autenticado es el dueño del producto.
     */
    @PostMapping("/deleteProduct")
    public String deleteProduct(
            @RequestParam("idProducto") Long idProducto,
            Authentication authentication,
            RedirectAttributes ra) {
        
        try {
            // 1. Obtener usuario autenticado
            Usuario usuarioActual = getAuthenticatedUser(authentication);
            
            // 2. Obtener el producto (utilizando el método auxiliar)
            Producto producto = getProductoById(idProducto);
            
            // 3. Verificación de propiedad: Solo el dueño puede eliminar
            if (!producto.getUsuario().getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                ra.addFlashAttribute("error", "Error de seguridad: No tiene permiso para eliminar este producto.");
                return "redirect:/panel/vendedor";
            }
            
            // 4. Eliminar la imagen del sistema de archivos (IMPORTANTE: primero el archivo)
            String nombreImagen = producto.getImagen();
            if (nombreImagen != null && !nombreImagen.isEmpty()) {
                fileStorageService.deleteFile(nombreImagen);
            }
            
            // 5. Eliminar el producto de la base de datos
            productoService.deleteById(idProducto);
            
            ra.addFlashAttribute("success", "Producto '" + producto.getNombre() + "' eliminado exitosamente.");
            
        } catch (RuntimeException e) {
            System.err.println("Error al intentar eliminar el producto ID " + idProducto + ": " + e.getMessage());
            ra.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error no esperado al intentar eliminar el producto ID " + idProducto + ": " + e.getMessage());
            ra.addFlashAttribute("error", "Error interno al eliminar el producto.");
        }
        
        return "redirect:/panel/vendedor";
    }

    // =========================================================================
    // 5. ELIMINACIÓN DE PRODUCTO POR AJAX (NUEVO MÉTODO SUGERIDO)
    // =========================================================================

    /**
     * Procesa la solicitud DELETE para eliminar un producto (vía AJAX).
     * Utiliza @DeleteMapping para ser más RESTful.
     * @param idProducto El ID del producto a eliminar.
     * @return Un mapa JSON con el resultado de la operación.
     */
    @DeleteMapping("/deleteProductAjax/{idProducto}")
    @ResponseBody // Indica que el resultado debe ser serializado directamente como JSON/XML
    public Map<String, Object> deleteProductAjax(
            @PathVariable Long idProducto,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Obtener usuario autenticado
            Usuario usuarioActual = getAuthenticatedUser(authentication);
            
            // 2. Obtener el producto
            Producto producto = getProductoById(idProducto);
            
            // 3. Verificación de propiedad: Solo el dueño puede eliminar
            if (!producto.getUsuario().getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                throw new SecurityException("No tiene permiso para eliminar este producto.");
            }
            
            // 4. Eliminar la imagen del sistema de archivos
            String nombreImagen = producto.getImagen();
            if (nombreImagen != null && !nombreImagen.isEmpty()) {
                fileStorageService.deleteFile(nombreImagen);
            }
            
            // 5. Eliminar el producto de la base de datos
            productoService.deleteById(idProducto);
            
            // 6. Respuesta de éxito JSON
            response.put("success", true);
            response.put("message", "Producto '" + producto.getNombre() + "' eliminado exitosamente.");
            
        } catch (SecurityException e) {
            response.put("success", false);
            response.put("message", "Error de seguridad: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error al intentar eliminar el producto ID " + idProducto + ": " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error al eliminar el producto: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error no esperado al intentar eliminar el producto ID " + idProducto + ": " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al eliminar el producto.");
        }
        
        return response;
    }


    /**
     * Endpoint que genera y descarga el archivo Excel con los productos del vendedor.
     */
    @GetMapping("/generarReporteExcel") 
    public void generarReporteExcel(Authentication authentication, HttpServletResponse response) throws IOException {
        
        // 1. Obtener el objeto Usuario completo
        // Asegúrate de que 'getAuthenticatedUser' devuelve el objeto Usuario.
        Usuario usuarioActual = getAuthenticatedUser(authentication); 
        
        // *** CORRECCIÓN CLAVE: Obtener el ID Long ***
        Long idVendedor = usuarioActual.getIdUsuario(); 

        // 2. Configurar el encabezado de la respuesta HTTP para forzar la descarga
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        // Usamos el ID del vendedor para nombrar el archivo
        String headerValue = "attachment; filename=ReporteProductos_Vendedor_" + idVendedor + ".xlsx";
        response.setHeader(headerKey, headerValue);

        // 3. Obtener la lista de productos
        // *** Aquí se llama al método del servicio que acabamos de confirmar. ***
        List<Producto> listaProductos = productoService.findAllByVendedorId(idVendedor);
        
        // 4. Generar y escribir el archivo Excel
        try {
            // Se instancia la clase auxiliar que maneja la creación del Excel
            ExcelGenerator excelGenerator = new ExcelGenerator(listaProductos); 
            // El método 'export' escribe el archivo directamente en el flujo de respuesta
            excelGenerator.export(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Notificamos al cliente si hay un error en el backend
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno al generar el archivo Excel.");
        }
    }

    

    /**
     * Muestra la lista de productos del vendedor. 
     * También maneja la funcionalidad de búsqueda por nombre, filtrando siempre por el usuario autenticado.
     * @param model El objeto Model para pasar datos a la vista.
     * @param keyword Palabra clave de búsqueda (opcional).
     * @param authentication Objeto de autenticación para obtener el usuario actual.
     * @return El nombre de la plantilla (ej. "productos-gestion").
     */
    @GetMapping({"/productos", "/productos/buscar"})
    public String listarProductos(
            Model model, 
            @RequestParam(value = "keyword", required = false) String keyword,
            Authentication authentication) {
        
        // =========================================================================
        // PASO CLAVE: OBTENER EL ID DEL VENDEDOR AUTENTICADO
        // =========================================================================
        Usuario usuarioActual;
        try {
            usuarioActual = getAuthenticatedUser(authentication);
        } catch (RuntimeException e) {
            // Si hay un error de autenticación, retorna una vista de error o redirige.
            model.addAttribute("productos", List.of());
            model.addAttribute("errorMessage", "Error de autenticación: " + e.getMessage());
            model.addAttribute("contentFragment", "fragmentos/panelVendedor/editProduct"); // O la vista que uses
            return "panelVendedor"; // O la vista que maneje el error
        }
        
        Long idVendedor = usuarioActual.getIdUsuario();
        List<Producto> productos;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Caso 1: Hay una palabra clave de búsqueda.
            String trimmedKeyword = keyword.trim();
            
            // CORRECCIÓN: Usar el método que filtra por nombre Y VENDEDOR
            productos = productoRepository.buscarPorNombre(trimmedKeyword, idVendedor); 
            
            model.addAttribute("keyword", trimmedKeyword); 
        } else {
            // Caso 2: Listado completo (no hay búsqueda).
            
            // CORRECCIÓN: Usar el método que lista SOLO los productos del VENDEDOR
            productos = productoRepository.findByUsuario_IdUsuario(idVendedor); 
            model.addAttribute("keyword", ""); // Para limpiar la caja de texto en la vista
        }
        
        model.addAttribute("productos", productos);
        model.addAttribute("contentFragment", "fragmentos/panelVendedor/editProduct");
        // Nota: El nombre de la plantilla padre debe ser 'panelVendedor', no 'productos-gestion'
        return "panelVendedor"; 
    }

    // Método para mostrar el formulario de envío de promociones
    @GetMapping("/addPromociones")
    public String showPromocionesForm(Model model, Authentication authentication) {
        try {
            // Validar que el usuario esté autenticado
            Usuario usuarioActual = getAuthenticatedUser(authentication);
            model.addAttribute("nombreUsuario", usuarioActual.getNombre());
            
            model.addAttribute("contentFragment", "fragmentos/panelVendedor/addPromociones");
            // Podemos agregar datos adicionales si es necesario
        } catch (RuntimeException e) {
            model.addAttribute("errorGlobal", "Error de autenticación: " + e.getMessage());
            return "redirect:/login";
        } catch (Exception e) {
            System.err.println("Error al cargar formulario de promociones: " + e.getMessage());
            model.addAttribute("errorGlobal", "Error interno al cargar el formulario de promociones.");
        }
        return "panelVendedor";
    }

    // Método para procesar el envío de promociones (POST)
    @PostMapping("/enviar-promociones")
    public String enviarPromociones(
        @RequestParam String emails,
        @RequestParam String descuento,
        @RequestParam String producto,
        @RequestParam String fechaExpiracion,
        @RequestParam String subject,
        RedirectAttributes redirectAttributes,
        Authentication authentication) {
        
        try {
            // 1. Validar autenticación
            Usuario usuarioActual = getAuthenticatedUser(authentication);
            System.out.println("Usuario autenticado: " + usuarioActual.getNombre());
            
            // 2. Parsear emails
            List<String> listaEmails = Arrays.stream(emails.split("[,\\n\\r]+"))
                .map(String::trim)
                .filter(email -> !email.isEmpty() && email.contains("@"))
                .collect(Collectors.toList());
            
            System.out.println("Emails parseados: " + listaEmails);
            
            if (listaEmails.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No se proporcionaron emails válidos.");
                return "redirect:/panel/addPromociones";
            }
            
            // 3. Preparar variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("descuento", descuento);
            variables.put("producto", producto);
            variables.put("fechaExpiracion", fechaExpiracion);
            
            System.out.println("Variables preparadas: " + variables);
            
            // 4. Llamar al EmailService
            System.out.println("LLAMANDO A EmailService.enviarCorreoMasivoHTML...");
            emailService.enviarCorreoMasivoHTML(listaEmails, subject, "promocion.html", variables);
            System.out.println("EmailService ejecutado");
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Promociones enviadas exitosamente a " + listaEmails.size() + " destinatarios!");
                
        } catch (Exception e) {
            System.err.println("ERROR en enviarPromociones: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar promociones: " + e.getMessage());
        }
        
        return "redirect:/panel/addPromociones";
    }
}
    
