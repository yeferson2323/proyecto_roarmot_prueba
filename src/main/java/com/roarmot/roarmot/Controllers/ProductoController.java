package com.roarmot.roarmot.Controllers;

import com.roarmot.roarmot.models.Producto;
import com.roarmot.roarmot.Services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// @RestController es una anotación de Spring que combina @Controller y @ResponseBody.
// Esto indica que la clase es un controlador REST y que los datos devueltos por los métodos se serializarán
// directamente en el cuerpo de la respuesta HTTP.
@RestController
// @RequestMapping define la URL base para todas las peticiones manejadas por este controlador.
// En este caso, todas las peticiones comenzarán con "/api/productos".
@RequestMapping("/api/productos")
public class ProductoController {

    // @Autowired es una anotación de Spring para inyectar dependencias.
    // Aquí, Spring creará una instancia de ProductoService y la asignará a esta variable,
    // permitiendo que el controlador use sus métodos.
    @Autowired
    private ProductoService productoService;

    // @GetMapping es una anotación para mapear peticiones HTTP GET.
    // Si la URL es "/api/productos", este método se ejecutará.
    // Retorna una lista de todos los productos en la base de datos.
    @GetMapping
    public List<Producto> findAll() {
        return productoService.findAll();
    }

    // @GetMapping("/{id}") mapea las peticiones GET que incluyen un ID en la URL.
    // @PathVariable se utiliza para extraer el valor del ID de la URL y pasarlo al método.
    // Retorna un ResponseEntity, que permite un control más granular sobre la respuesta HTTP,
    // incluyendo el código de estado (como 200 OK o 404 Not Found).
    @GetMapping("/{id}")
    public ResponseEntity<Producto> findById(@PathVariable Long id) {
        Optional<Producto> producto = productoService.findById(id);
        // Si el producto existe, devuelve 200 OK con el cuerpo del producto.
        // Si no existe, devuelve 404 Not Found.
        return producto.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // @PostMapping mapea las peticiones HTTP POST a "/api/productos".
    // @RequestBody se usa para deserializar el JSON del cuerpo de la petición HTTP en un objeto Producto.
    // Retorna un ResponseEntity para enviar el código de estado 201 Created al cliente,
    // indicando que el recurso se ha creado exitosamente.
    @PostMapping
    public ResponseEntity<Producto> save(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // @PutMapping("/{id}") mapea las peticiones HTTP PUT para actualizar un recurso.
    // @PathVariable y @RequestBody se combinan para tomar el ID de la URL y los datos actualizados del cuerpo.
    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id, @RequestBody Producto producto) {
        // Primero, se verifica si el producto con el ID proporcionado existe.
        if (!productoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build(); // Devuelve 404 si no se encuentra.
        }
        producto.setId(id); // Se asegura de que el ID del objeto sea el mismo que el de la URL.
        Producto productoActualizado = productoService.save(producto);
        return ResponseEntity.ok(productoActualizado); // Devuelve 200 OK con el producto actualizado.
    }

    // @DeleteMapping("/{id}") mapea las peticiones HTTP DELETE para eliminar un recurso.
    // Retorna un ResponseEntity con el código de estado 204 No Content,
    // que es la respuesta estándar para una eliminación exitosa sin devolver contenido.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Se verifica si el producto a eliminar existe.
        if (!productoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build(); // Devuelve 404 si no se encuentra.
        }
        productoService.deleteById(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content para una eliminación exitosa.
    }
}
