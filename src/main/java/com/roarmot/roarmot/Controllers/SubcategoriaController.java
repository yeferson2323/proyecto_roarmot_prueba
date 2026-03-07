package com.roarmot.roarmot.Controllers;

import com.roarmot.roarmot.models.Subcategoria;
import com.roarmot.roarmot.Services.SubcategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Anotación que marca esta clase como un controlador REST.
@RestController
// Mapea todas las peticiones a la ruta base "/api/subcategorias".
@RequestMapping("/api/subcategorias")
public class SubcategoriaController {

    // Inyecta la dependencia del servicio SubcategoriaService.
    @Autowired
    private SubcategoriaService subcategoriaService;

    // Obtener todas las subcategorías
    @GetMapping
    public List<Subcategoria> findAll() {
        return subcategoriaService.findAllSubcategorias();
    }

    // Obtener una subcategoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<Subcategoria> findById(@PathVariable Long id) {
        Optional<Subcategoria> subcategoria = subcategoriaService.findById(id);
        return subcategoria.map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Guardar una nueva subcategoría
    @PostMapping
    public ResponseEntity<Subcategoria> save(@RequestBody Subcategoria subcategoria) {
        Subcategoria nuevaSubcategoria = subcategoriaService.save(subcategoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSubcategoria);
    }

    // Actualizar una subcategoría existente
    @PutMapping("/{id}")
    public ResponseEntity<Subcategoria> update(@PathVariable Long id, @RequestBody Subcategoria subcategoria) {
        if (!subcategoriaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        subcategoria.setId(id);
        Subcategoria subcategoriaActualizada = subcategoriaService.save(subcategoria);
        return ResponseEntity.ok(subcategoriaActualizada);
    }

    // Eliminar una subcategoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!subcategoriaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        subcategoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
